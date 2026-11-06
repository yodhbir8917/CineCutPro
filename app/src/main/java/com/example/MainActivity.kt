// --- PART 1 START ---
package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- CINECUT PRO THEME COLORS (100% Free Forever) ---
val StudioDark = Color(0xFF0D0F14)
val StudioSurface = Color(0xFF161922)
val StudioSurfaceElevated = Color(0xFF1E2230)
val StudioBorder = Color(0xFF2B3144)
val PremiereCyan = Color(0xFF00E5FF)
val PremiereAmber = Color(0xFFFFB300)
val PremiereEmerald = Color(0xFF10B981)
val TextPrimary = Color(0xFFF1F3F9)
val TextSecondary = Color(0xFF9DA7BE)

private val CineCutColorScheme = darkColorScheme(
    primary = PremiereCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = Color(0xFF8CEEFF),
    secondary = PremiereAmber,
    onSecondary = Color.Black,
    background = StudioDark,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceElevated,
    outline = StudioBorder
)

data class SequenceConfig(
    val videoUri: Uri? = null,
    val name: String = "Sequence 1",
    val aspectRatio: String = "16:9",
    val resolution: String = "1080p",
    val frameRate: String = "30 FPS"
)

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
    var currentConfig by remember { mutableStateOf(SequenceConfig()) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            CineCutHomeScreen(
                onVideoSelected = { uri ->
                    currentConfig = currentConfig.copy(videoUri = uri, name = "Sequence_${System.currentTimeMillis() % 10000}")
                    navController.navigate("sequence_settings")
                }
            )
        }
        composable("sequence_settings") {
            SequenceSettingsScreen(
                initialConfig = currentConfig,
                onBack = { navController.popBackStack() },
                onCreateSequence = { finalizedConfig ->
                    currentConfig = finalizedConfig
                    navController.navigate("editing_workspace")
                }
            )
        }
        composable("editing_workspace") {
            EditingWorkspaceScreen(
                config = currentConfig,
                onBack = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineCutHomeScreen(onVideoSelected: (Uri) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onVideoSelected(uri)
    }
    val openPicker = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(PremiereCyan), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row {
                                Text("CINECUT", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
                                Text(" PRO", color = PremiereCyan, fontSize = 17.sp, fontWeight = FontWeight.Black)
                            }
                            Text("100% Free Forever • No Watermark • Offline", color = PremiereCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = openPicker) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "New Project", tint = PremiereCyan, modifier = Modifier.size(28.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioSurface)
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = openPicker, containerColor = PremiereCyan, contentColor = Color.Black, shape = RoundedCornerShape(16.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("NEW SEQUENCE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioSurface,
                contentColor = PremiereCyan,
                indicator = { tabPositions -> TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]), color = PremiereCyan) }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("PROJECTS (0)", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("PRESETS", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("EXPORTS", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("SETTINGS", fontWeight = FontWeight.Bold, fontSize = 12.sp) })
            }
            when (selectedTab) {
                0 -> ProjectsEmptyState(onStartNewProject = openPicker)
                1 -> PresetsTab()
                2 -> ExportsTab()
                3 -> SettingsTab()
            }
        }
    }
}

@Composable
fun ProjectsEmptyState(onStartNewProject: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Box(modifier = Modifier.size(68.dp).clip(CircleShape).background(StudioSurfaceElevated).border(1.dp, StudioBorder, CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.VideoLibrary, contentDescription = null, tint = PremiereCyan, modifier = Modifier.size(34.dp))
            }
            Text("NO EDITING SEQUENCES YET", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Card(
                modifier = Modifier.fillMaxWidth(0.92f).padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E2E)),
                border = BorderStroke(1.dp, PremiereCyan.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PremiereCyan, modifier = Modifier.size(20.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("DEVICE VIDEO IMPORT READY", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("Tap the button below to browse all accessible videos on your physical phone gallery using the native Android Photo Picker.", color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onStartNewProject,
                colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(0.88f).height(46.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("START NEW PROJECT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
// --- PART 2 END ---
// --- PART 3 START ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SequenceSettingsScreen(initialConfig: SequenceConfig, onBack: () -> Unit, onCreateSequence: (SequenceConfig) -> Unit) {
    var sequenceName by remember { mutableStateOf(initialConfig.name) }
    var selectedAspectRatio by remember { mutableStateOf(initialConfig.aspectRatio) }
    var selectedResolution by remember { mutableStateOf(initialConfig.resolution) }
    var selectedFrameRate by remember { mutableStateOf(initialConfig.frameRate) }

    val aspectRatios = listOf(Triple("9:16", "Vertical (Reels/TikTok)", "9:16"), Triple("16:9", "Landscape (YouTube/Cinema)", "16:9"), Triple("1:1", "Square (Instagram)", "1:1"))
    val resolutions = listOf("720p", "1080p", "2K", "4K")
    val frameRates = listOf("24 FPS", "25 FPS", "30 FPS", "50 FPS", "60 FPS")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NEW SEQUENCE SETTINGS", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiereCyan) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioSurface)
            )
        },
        bottomBar = {
            Surface(color = StudioSurface, border = BorderStroke(1.dp, StudioBorder), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = { onCreateSequence(SequenceConfig(initialConfig.videoUri, sequenceName.ifBlank { "Sequence 1" }, selectedAspectRatio, selectedResolution, selectedFrameRate)) },
                        colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Movie, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CREATE SEQUENCE", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
                    }
                }
            }
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            initialConfig.videoUri?.let { uri ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2537)), border = BorderStroke(1.dp, PremiereCyan.copy(alpha = 0.5f)), shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(PremiereCyan.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                            Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = PremiereCyan)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SOURCE VIDEO LOADED", color = PremiereCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = uri.toString(), color = TextSecondary, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("SEQUENCE NAME", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                OutlinedTextField(
                    value = sequenceName,
                    onValueChange = { sequenceName = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PremiereCyan, unfocusedBorderColor = StudioBorder, focusedTextColor = TextPrimary, unfocusedTextColor = TextPrimary, focusedContainerColor = StudioSurfaceElevated, unfocusedContainerColor = StudioSurfaceElevated),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("ASPECT RATIO", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    aspectRatios.forEach { (ratio, _, value) ->
                        val isSelected = selectedAspectRatio == value
                        Card(
                            modifier = Modifier.weight(1f).clickable { selectedAspectRatio = value },
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF003842) else StudioSurfaceElevated),
                            border = BorderStroke(1.5.dp, if (isSelected) PremiereCyan else StudioBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = ratio, color = if (isSelected) PremiereCyan else TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text(text = if (value == "9:16") "Reels" else if (value == "16:9") "YouTube" else "Square", color = if (isSelected) PremiereCyan else TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("RESOLUTION", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    resolutions.forEach { res ->
                        val isSelected = selectedResolution == res
                        Card(
                            modifier = Modifier.weight(1f).clickable { selectedResolution = res },
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF003842) else StudioSurfaceElevated),
                            border = BorderStroke(1.5.dp, if (isSelected) PremiereCyan else StudioBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                Text(text = res, color = if (isSelected) PremiereCyan else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("FRAME RATE", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    frameRates.forEach { fps ->
                        val isSelected = selectedFrameRate == fps
                        Card(
                            modifier = Modifier.weight(1f).clickable { selectedFrameRate = fps },
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF003842) else StudioSurfaceElevated),
                            border = BorderStroke(1.5.dp, if (isSelected) PremiereCyan else StudioBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                                Text(text = fps.substringBefore(" "), color = if (isSelected) PremiereCyan else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2419)), border = BorderStroke(1.dp, PremiereEmerald), shape = RoundedCornerShape(8.dp)) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = PremiereEmerald, modifier = Modifier.size(18.dp))
                    Text("100% Free Forever • All 4K / 60 FPS Settings Unlocked", color = Color(0xFF6EE7B7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
    var isPlaying by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text(config.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${config.resolution} • ${config.frameRate} • ${config.aspectRatio}", color = PremiereCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) } },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiereCyan) } },
                actions = { Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = PremiereCyan, contentColor = Color.Black), shape = RoundedCornerShape(6.dp), modifier = Modifier.height(34.dp).padding(end = 8.dp)) { Text("EXPORT", fontWeight = FontWeight.Bold, fontSize = 11.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = StudioSurface)
            )
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Video Player Monitor Viewport
            Box(modifier = Modifier.fillMaxWidth().weight(1.1f).background(Color.Black).padding(10.dp), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)).background(Color(0xFF07080B)).border(1.dp, StudioBorder, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(StudioSurfaceElevated).border(1.dp, PremiereCyan, CircleShape).clickable { isPlaying = !isPlaying }, contentAlignment = Alignment.Center) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play", tint = PremiereCyan, modifier = Modifier.size(32.dp))
                        }
                        Text(if (isPlaying) "PLAYING PREVIEW" else "VIDEO MONITOR", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        config.videoUri?.let { Text("Source: ${it.lastPathSegment ?: "Imported Video"}", color = TextSecondary, fontSize = 10.sp) }
                    }
                    Box(modifier = Modifier.align(Alignment.TopStart).padding(8.dp).background(Color(0xCC000000), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) { Text("${config.aspectRatio} | ${config.resolution}", color = PremiereCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).background(Color(0xCC000000), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 3.dp)) { Text("00:00:00:00 / 00:00:15:00", color = Color.White, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) }
                }
            }

            // Playback and Editing Controls Strip
            Row(modifier = Modifier.fillMaxWidth().background(StudioSurface).border(BorderStroke(1.dp, StudioBorder)).padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {}) { Icon(Icons.Default.ContentCut, contentDescription = "Split", tint = PremiereCyan) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Audiotrack, contentDescription = "Audio", tint = TextPrimary) }
                }
                IconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.size(42.dp).clip(CircleShape).background(PremiereCyan)) { Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = "Play/Pause", tint = Color.Black) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = {}) { Icon(Icons.Default.AspectRatio, contentDescription = "Canvas", tint = TextPrimary) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary) }
                }
            }

            // Multi-track Timeline Area
            Column(modifier = Modifier.fillMaxWidth().weight(1.0f).background(StudioDark)) {
                Box(modifier = Modifier.fillMaxWidth().height(26.dp).background(Color(0xFF12151D)).border(1.dp, StudioBorder), contentAlignment = Alignment.CenterStart) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("00:00", "00:05", "00:10", "00:15").forEach { Text(it, color = TextSecondary, fontSize = 9.sp, fontFamily = FontFamily.Monospace) }
                    }
                }
                LazyColumn(modifier = Modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth().height(60.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.width(46.dp).fillMaxSize().clip(RoundedCornerShape(6.dp)).background(StudioSurfaceElevated).border(1.dp, StudioBorder, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                                Text("V1", color = PremiereCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(6.dp)).background(Color(0xFF003842)).border(1.5.dp, PremiereCyan, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp), contentAlignment = Alignment.CenterStart) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Movie, contentDescription = null, tint = PremiereCyan, modifier = Modifier.size(18.dp))
                                    Column {
                                        Text(config.videoUri?.lastPathSegment ?: "Primary Video Clip", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("00:00:00 - 00:15:00", color = PremiereCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth().height(48.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.width(46.dp).fillMaxSize().clip(RoundedCornerShape(6.dp)).background(StudioSurfaceElevated).border(1.dp, StudioBorder, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                                Text("A1", color = PremiereEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(6.dp)).background(Color(0xFF0C2419)).border(1.dp, PremiereEmerald, RoundedCornerShape(6.dp)).padding(horizontal = 10.dp), contentAlignment = Alignment.CenterStart) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Audiotrack, contentDescription = null, tint = PremiereEmerald, modifier = Modifier.size(16.dp))
                                    Text("Audio Master (Embedded Track)", color = Color(0xFF6EE7B7), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
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
// --- PART 4 END ---
