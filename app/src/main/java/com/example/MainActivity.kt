// --- PART 1 START ---
package com.example

import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import java.util.UUID

val PremiereCyan = Color(0xFF00E5FF)
val PremiereAmber = Color(0xFFFFB300)
val PremiereEmerald = Color(0xFF10B981)
val StudioDark = Color(0xFF0B0D13)
val StudioSurface = Color(0xFF161922)
val StudioSurfaceElevated = Color(0xFF222634)
val StudioBorder = Color(0xFF2E3446)
val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)

private val CineCutColorScheme = darkColorScheme(
    primary = PremiereCyan, onPrimary = Color.Black,
    secondary = PremiereAmber, surface = StudioSurface,
    background = StudioDark, onSurface = TextPrimary, onBackground = TextPrimary
)

data class TimelineClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val uri: Uri,
    val sourceDurationMs: Long = 15000L,
    val startMs: Long = 0L,
    val endMs: Long = 15000L
) {
    val durationMs: Long get() = (endMs - startMs).coerceAtLeast(200L)
}

data class SequenceConfig(
    val name: String = "Sequence 01",
    val aspectRatio: String = "16:9",
    val resolution: String = "1080p",
    val frameRate: String = "30 FPS",
    val videoUri: Uri? = null
)

fun formatTimecode(ms: Long): String {
    val totalSec = ms / 1000
    val m = totalSec / 60
    val s = totalSec % 60
    val f = ((ms % 1000) * 30 / 1000).coerceIn(0, 29)
    return "%02d:%02d:%02d".format(m, s, f)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = CineCutColorScheme) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    CineCutNavigationApp()
                }
            }
        }
    }
}
// --- PART 1 END ---
// --- PART 2 START ---
@Composable
fun CineCutNavigationApp() {
    val navController = rememberNavController()
    var activeConfig by remember { mutableStateOf(SequenceConfig()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            CineCutHomeScreen(onStartProject = { navController.navigate("sequence_settings") })
        }
        composable("sequence_settings") {
            SequenceSettingsScreen(
                onBack = { navController.popBackStack() },
                onSequenceCreated = { config ->
                    activeConfig = config
                    navController.navigate("editing_workspace")
                }
            )
        }
        composable("editing_workspace") {
            EditingWorkspaceScreen(
                config = activeConfig,
                onBack = { navController.popBackStack("home", inclusive = false) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineCutHomeScreen(onStartProject: () -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("PROJECTS (0)", "PRESETS", "EXPORTS", "SETTINGS")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CINECUT PRO", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 2.sp, color = TextPrimary) },
                actions = { IconButton(onClick = onStartProject) { Icon(Icons.Default.Add, contentDescription = "New Sequence", tint = PremiereCyan) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onStartProject, containerColor = PremiereCyan, contentColor = Color.Black, shape = RoundedCornerShape(14.dp)) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("NEW SEQUENCE", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
            }
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTabIndex, containerColor = StudioSurface, contentColor = PremiereCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]), height = 3.dp, color = PremiereCyan)
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index, onClick = { selectedTabIndex = index },
                        text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium, color = if (selectedTabIndex == index) PremiereCyan else TextSecondary, maxLines = 1) }
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> ProjectsEmptyState(onStartProject = onStartProject)
                1 -> PresetsTab()
                2 -> ExportsTab()
                3 -> SettingsTab()
            }
        }
    }
}

@Composable
fun ProjectsEmptyState(onStartProject: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Card(modifier = Modifier.fillMaxWidth().border(BorderStroke(1.dp, StudioBorder), RoundedCornerShape(16.dp)), colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated), shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFF003842)).border(2.dp, PremiereCyan, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = PremiereCyan, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text("NO EDITING SEQUENCES YET", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Create a project sequence to edit multiple video tracks, split and trim clips, and render with hardware acceleration.", color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onStartProject, colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth().height(46.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START NEW PROJECT", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}
// --- PART 2 END ---
// --- PART 3 START ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequenceSettingsScreen(onBack: () -> Unit, onSequenceCreated: (SequenceConfig) -> Unit) {
    var sequenceName by remember { mutableStateOf("Sequence 01") }
    var selectedAspect by remember { mutableStateOf("16:9") }
    var selectedResolution by remember { mutableStateOf("1080p") }
    var selectedFps by remember { mutableStateOf("30 FPS") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NEW SEQUENCE SETTINGS", fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiereCyan) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioSurface)
            )
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("SEQUENCE NAME", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            OutlinedTextField(
                value = sequenceName, onValueChange = { sequenceName = it }, modifier = Modifier.fillMaxWidth(), singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PremiereCyan, unfocusedBorderColor = StudioBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary)
            )

            Text("ASPECT RATIO", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("9:16", "16:9", "1:1").forEach { ratio ->
                    val isSel = selectedAspect == ratio
                    Button(
                        onClick = { selectedAspect = ratio }, modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSel) PremiereCyan else StudioSurfaceElevated, contentColor = if (isSel) Color.Black else TextPrimary),
                        shape = RoundedCornerShape(8.dp), border = if (isSel) null else BorderStroke(1.dp, StudioBorder)
                    ) { Text(ratio, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                }
            }

            Text("RESOLUTION", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("720p", "1080p", "2K", "4K").forEach { res ->
                    val isSel = selectedResolution == res
                    Button(
                        onClick = { selectedResolution = res }, modifier = Modifier.weight(1f).height(42.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSel) PremiereCyan else StudioSurfaceElevated, contentColor = if (isSel) Color.Black else TextPrimary),
                        shape = RoundedCornerShape(8.dp), border = if (isSel) null else BorderStroke(1.dp, StudioBorder), contentPadding = PaddingValues(0.dp)
                    ) { Text(res, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                }
            }

            Text("FRAME RATE", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("24 FPS", "25 FPS", "30 FPS", "50 FPS", "60 FPS").forEach { fps ->
                    val isSel = selectedFps == fps
                    Button(
                        onClick = { selectedFps = fps }, modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSel) PremiereCyan else StudioSurfaceElevated, contentColor = if (isSel) Color.Black else TextPrimary),
                        shape = RoundedCornerShape(6.dp), border = if (isSel) null else BorderStroke(1.dp, StudioBorder), contentPadding = PaddingValues(0.dp)
                    ) { Text(fps.replace(" FPS", "p"), fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onSequenceCreated(SequenceConfig(name = sequenceName.ifBlank { "Sequence 01" }, aspectRatio = selectedAspect, resolution = selectedResolution, frameRate = selectedFps, videoUri = null)) },
                modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black), shape = RoundedCornerShape(8.dp)
            ) { Text("CREATE SEQUENCE", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp) }
        }
    }
}

@Composable
fun PresetsTab() {
    val presets = listOf(
        Triple("YouTube & 4K Cinema", "16:9 Landscape • 3840x2160 • 60 FPS", "LANDSCAPE"),
        Triple("TikTok & Instagram Reels", "9:16 Vertical • 1080x1920 • 30 FPS", "VERTICAL"),
        Triple("Instagram Square", "1:1 Square • 1080x1080 • 30 FPS", "SQUARE"),
        Triple("Anamorphic UltraWide", "21:9 Scope • 2560x1080 • 24 FPS", "CINEMA")
    )
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Text("SEQUENCE PRESETS (100% UNLOCKED)", color = PremiereCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
        items(presets) { (title, subtitle, tag) ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated), border = BorderStroke(1.dp, StudioBorder), shape = RoundedCornerShape(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold); Text(subtitle, color = TextSecondary, fontSize = 11.sp) }
                    Text(tag, color = PremiereCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
fun ExportsTab() {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(StudioSurfaceElevated).border(1.dp, StudioBorder, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Folder, contentDescription = null, tint = PremiereAmber, modifier = Modifier.size(32.dp)) }
            Text("NO EXPORTED VIDEOS YET", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("Videos exported from CineCut Pro are saved to Movies/CineCutPro at full quality without watermarks.", color = TextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
        }
    }
}

@Composable
fun SettingsTab() {
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2419)), border = BorderStroke(1.dp, PremiereEmerald), shape = RoundedCornerShape(10.dp)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PremiereEmerald, modifier = Modifier.size(24.dp))
                    Column {
                        Text("100% FREE FOREVER GUARANTEE", color = PremiereEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("No subscriptions • No watermarks • No ads • Works 100% offline", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated), border = BorderStroke(1.dp, StudioBorder), shape = RoundedCornerShape(10.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("ENGINE SPECIFICATIONS", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    listOf("• Hardware Acceleration: Enabled (MediaCodec)", "• Video Pipeline: AndroidX Media3 Transformer & ExoPlayer 1.5.1", "• Maximum Resolution: 4K UHD (3840x2160)", "• Frame Rates Supported: 24, 25, 30, 50, 60 FPS").forEach { Text(it, color = TextPrimary, fontSize = 12.sp) }
                }
            }
        }
    }
}
// --- PART 3 END ---
// --- PART 4 START ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditingWorkspaceScreen(config: SequenceConfig, onBack: () -> Unit) {
    val context = LocalContext.current
    var clips by remember { mutableStateOf<List<TimelineClip>>(emptyList()) }
    var selectedClipId by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPlayheadMs by remember { mutableLongStateOf(0L) }
    val totalDurationMs = clips.sumOf { it.durationMs }.coerceAtLeast(1000L)

    val exoPlayer = remember { ExoPlayer.Builder(context).build().apply { repeatMode = Player.REPEAT_MODE_ALL } }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val name = uri.lastPathSegment ?: "Imported Clip"
            val newClip = TimelineClip(name = name, uri = uri, sourceDurationMs = 15000L, startMs = 0L, endMs = 15000L)
            clips = clips + newClip
            selectedClipId = newClip.id
            exoPlayer.setMediaItem(MediaItem.fromUri(uri))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener); exoPlayer.release() }
    }

    LaunchedEffect(isPlaying, exoPlayer, totalDurationMs) {
        while (isPlaying) {
            currentPlayheadMs = exoPlayer.currentPosition.coerceIn(0L, totalDurationMs)
            delay(30)
        }
    }

    // Split Function (Scissor Tool)
    val onSplitClicked = {
        val cutPos = currentPlayheadMs
        var accum = 0L
        val updated = mutableListOf<TimelineClip>()
        var didSplit = false
        for (clip in clips) {
            val clipEnd = accum + clip.durationMs
            if (!didSplit && cutPos > accum + 200L && cutPos < clipEnd - 200L) {
                val cutSourceMs = clip.startMs + (cutPos - accum)
                val clipA = clip.copy(id = UUID.randomUUID().toString(), endMs = cutSourceMs)
                val clipB = clip.copy(id = UUID.randomUUID().toString(), startMs = cutSourceMs)
                updated.add(clipA)
                updated.add(clipB)
                selectedClipId = clipB.id
                didSplit = true
            } else {
                updated.add(clip)
            }
            accum = clipEnd
        }
        if (didSplit) clips = updated
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text(config.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${config.resolution} • ${config.frameRate} • ${config.aspectRatio}", color = PremiereCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiereCyan) } },
                actions = {
                    Button(
                        onClick = { videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) },
                        colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceElevated, contentColor = PremiereCyan),
                        shape = RoundedCornerShape(6.dp), modifier = Modifier.height(34.dp).padding(end = 6.dp), border = BorderStroke(1.dp, PremiereCyan)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (clips.isEmpty()) "IMPORT" else "ADD MEDIA", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(34.dp).padding(end = 8.dp)) {
                        Text("EXPORT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioSurface)
            )
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Video Player Monitor Viewport with Real ExoPlayer
            Box(modifier = Modifier.fillMaxWidth().weight(1.05f).background(Color.Black).padding(8.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color(0xFF07080B)).border(1.dp, StudioBorder, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    if (clips.isNotEmpty()) {
                        AndroidView(factory = { ctx -> PlayerView(ctx).apply { player = exoPlayer; useController = false; layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) } }, modifier = Modifier.fillMaxSize())
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFF003842)).border(1.dp, PremiereCyan, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.VideoLibrary, contentDescription = "Import", tint = PremiereCyan, modifier = Modifier.size(28.dp))
                            }
                            Text("NO MEDIA IMPORTED", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Button(onClick = { videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }, colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(36.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("IMPORT VIDEO CLIP", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                    Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(Color(0xCC000000), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) { Text("${config.aspectRatio} | ${config.resolution}", color = PremiereCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(Color(0xCC000000), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                        Text("${formatTimecode(currentPlayheadMs)} / ${formatTimecode(totalDurationMs)}", color = if (isPlaying) PremiereEmerald else Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Playback and Editing Controls Strip
            Row(modifier = Modifier.fillMaxWidth().background(StudioSurface).border(BorderStroke(1.dp, StudioBorder)).padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onSplitClicked) { Icon(Icons.Default.ContentCut, contentDescription = "Split Clip", tint = if (clips.isNotEmpty()) PremiereAmber else TextSecondary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Audiotrack, contentDescription = "Audio", tint = TextPrimary) }
                }
                IconButton(
                    onClick = {
                        if (clips.isNotEmpty()) {
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                        } else {
                            videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                        }
                    },
                    modifier = Modifier.size(42.dp).clip(CircleShape).background(PremiereCyan)
                ) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play/Pause", tint = Color.Black)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {}) { Icon(Icons.Default.AspectRatio, contentDescription = "Canvas", tint = TextPrimary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary) }
                }
            }

            // Interactive Multi-track Timeline Area with Playhead Scrubber & Trim Handles
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1.05f).background(StudioDark)) {
                val timelineWidthPx = constraints.maxWidth.toFloat().coerceAtLeast(1f)

                Column(modifier = Modifier.fillMaxSize()) {
                    // Time Ruler with Scrub Detection
                    Box(
                        modifier = Modifier.fillMaxWidth().height(26.dp).background(Color(0xFF12151D)).border(1.dp, StudioBorder)
                            .pointerInput(totalDurationMs, timelineWidthPx) {
                                detectTapGestures { offset ->
                                    val progress = (offset.x / timelineWidthPx).coerceIn(0f, 1f)
                                    val seekMs = (progress * totalDurationMs).toLong()
                                    currentPlayheadMs = seekMs
                                    exoPlayer.seekTo(seekMs)
                                }
                            }
                            .pointerInput(totalDurationMs, timelineWidthPx) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    val progress = (change.position.x / timelineWidthPx).coerceIn(0f, 1f)
                                    val seekMs = (progress * totalDurationMs).toLong()
                                    currentPlayheadMs = seekMs
                                    exoPlayer.seekTo(seekMs)
                                }
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            listOf("00:00", formatTimecode(totalDurationMs / 3), formatTimecode(totalDurationMs * 2 / 3), formatTimecode(totalDurationMs)).forEach {
                                Text(it, color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
// --- PART 4 END ---
// --- PART 5 START ---
                    // Tracks Container
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Track V1
                        item {
                            Row(modifier = Modifier.fillMaxWidth().height(68.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.width(44.dp).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(StudioSurfaceElevated).border(1.dp, StudioBorder, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                                    Text("V1", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                if (clips.isNotEmpty()) {
                                    Row(modifier = Modifier.weight(1f).fillMaxHeight(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        clips.forEach { clip ->
                                            val isSelected = clip.id == selectedClipId
                                            val clipWeight = clip.durationMs.toFloat().coerceAtLeast(100f)

                                            Box(
                                                modifier = Modifier.weight(clipWeight).fillMaxHeight().clip(RoundedCornerShape(6.dp))
                                                    .background(if (isSelected) Color(0xFF004450) else Color(0xFF003038))
                                                    .border(if (isSelected) 2.dp else 1.dp, if (isSelected) PremiereCyan else Color(0xFF006B7D), RoundedCornerShape(6.dp))
                                                    .clickable { selectedClipId = clip.id }
                                            ) {
                                                // Clip Body Info
                                                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Icon(Icons.Default.Movie, contentDescription = null, tint = PremiereCyan, modifier = Modifier.size(16.dp))
                                                    Column {
                                                        Text(clip.name, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text("${formatTimecode(clip.startMs)} - ${formatTimecode(clip.endMs)}", color = PremiereCyan, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                                    }
                                                }

                                                // Left Trim Handle (Draggable)
                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier.align(Alignment.CenterStart).width(12.dp).fillMaxHeight().background(PremiereAmber, RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                                                            .pointerInput(clip.id) {
                                                                detectDragGestures { change, dragAmount ->
                                                                    change.consume()
                                                                    val deltaMs = (dragAmount.x * 60).toLong()
                                                                    clips = clips.map { c ->
                                                                        if (c.id == clip.id) {
                                                                            val newStart = (c.startMs + deltaMs).coerceIn(0L, c.endMs - 300L)
                                                                            c.copy(startMs = newStart)
                                                                        } else c
                                                                    }
                                                                    exoPlayer.seekTo(clip.startMs)
                                                                }
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Box(modifier = Modifier.width(2.dp).height(20.dp).background(Color.Black))
                                                    }

                                                    // Right Trim Handle (Draggable)
                                                    Box(
                                                        modifier = Modifier.align(Alignment.CenterEnd).width(12.dp).fillMaxHeight().background(PremiereAmber, RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                                                            .pointerInput(clip.id) {
                                                                detectDragGestures { change, dragAmount ->
                                                                    change.consume()
                                                                    val deltaMs = (dragAmount.x * 60).toLong()
                                                                    clips = clips.map { c ->
                                                                        if (c.id == clip.id) {
                                                                            val newEnd = (c.endMs + deltaMs).coerceIn(c.startMs + 300L, c.sourceDurationMs)
                                                                            c.copy(endMs = newEnd)
                                                                        } else c
                                                                    }
                                                                    exoPlayer.seekTo(clip.endMs)
                                                                }
                                                            },
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Box(modifier = Modifier.width(2.dp).height(20.dp).background(Color.Black))
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(StudioSurfaceElevated).border(1.dp, StudioBorder, RoundedCornerShape(6.dp)).clickable {
                                            videoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
                                        }.padding(horizontal = 12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Icon(Icons.Default.Add, contentDescription = null, tint = PremiereCyan, modifier = Modifier.size(16.dp))
                                            Text("+ Tap to Add Video Clip to Track V1", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }

                        // Track A1
                        item {
                            Row(modifier = Modifier.fillMaxWidth().height(44.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.width(44.dp).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(StudioSurfaceElevated).border(1.dp, StudioBorder, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                                    Text("A1", color = PremiereEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(6.dp)).background(Color(0xFF0C2419)).border(1.dp, PremiereEmerald, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp), contentAlignment = Alignment.CenterStart) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Icon(Icons.Default.Audiotrack, contentDescription = null, tint = PremiereEmerald, modifier = Modifier.size(16.dp))
                                        Text(if (clips.isNotEmpty()) "Audio Master (${clips.size} clip synchronized)" else "Audio Track A1 (Empty)", color = Color(0xFF6EE7B7), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive Needle Playhead across Timeline
                if (clips.isNotEmpty()) {
                    val playheadFraction = (currentPlayheadMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
                    val needleX = (playheadFraction * timelineWidthPx).toInt()

                    Box(
                        modifier = Modifier.offset { IntOffset(x = needleX - 6, y = 0) }.width(12.dp).fillMaxHeight()
                            .pointerInput(totalDurationMs, timelineWidthPx) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    val progress = (change.position.x / timelineWidthPx).coerceIn(0f, 1f)
                                    val seekMs = (progress * totalDurationMs).toLong()
                                    currentPlayheadMs = seekMs
                                    exoPlayer.seekTo(seekMs)
                                }
                            }
                    ) {
                        // Playhead Diamond/Head at Top
                        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(PremiereCyan).border(1.dp, Color.White, CircleShape).align(Alignment.TopCenter))
                        // Playhead Vertical Line
                        Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(PremiereCyan).align(Alignment.TopCenter))
                    }
                }
            }
        }
    }
}
// --- PART 5 END ---
