package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- CINECUT PRO STUDIO COLOR PALETTE (100% Free Forever) ---
val StudioDark = Color(0xFF0D0F14)             // Root deep background
val StudioSurface = Color(0xFF161922)          // Top bar & navigation bars
val StudioSurfaceElevated = Color(0xFF1E2230)  // Cards & floating containers
val StudioBorder = Color(0xFF2B3144)           // Subtle structural borders
val PremiereCyan = Color(0xFF00E5FF)           // Signature Cyan accent
val PremiereAmber = Color(0xFFFFB300)          // Preset accent
val PremiereEmerald = Color(0xFF10B981)        // Free Forever emerald
val TextPrimary = Color(0xFFF1F3F9)            // Crisp white
val TextSecondary = Color(0xFF9DA7BE)          // Slate gray subtext

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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme(colorScheme = CineCutColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CineCutHomeScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineCutHomeScreen() {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: PROJECTS, 1: PRESETS, 2: EXPORTS, 3: SETTINGS

    // Native Android Photo Picker for Video Selection
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            Toast.makeText(
                context,
                "Video Selected:\n$uri",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                context,
                "No video was selected.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PremiereCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CINECUT",
                                    color = TextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = " PRO",
                                    color = PremiereCyan,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "100% Free Forever • No Watermark • Offline",
                                color = PremiereCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Project",
                            tint = PremiereCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = StudioSurface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    containerColor = PremiereCyan,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NEW SEQUENCE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        containerColor = StudioDark
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- TABS SECTION: PROJECTS (0), PRESETS, EXPORTS, SETTINGS ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioSurface,
                contentColor = PremiereCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PremiereCyan
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "PROJECTS (0)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "PRESETS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = "EXPORTS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Text(
                            text = "SETTINGS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            // --- TAB CONTENT ---
            when (selectedTab) {
                0 -> ProjectsEmptyState(
                    onStartNewProject = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    }
                )
                1 -> PresetsTab()
                2 -> ExportsTab()
                3 -> SettingsTab()
            }
        }
    }
}

// --- TAB 0: PROJECTS (EMPTY STATE) ---
@Composable
fun ProjectsEmptyState(
    onStartNewProject: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Central Icon Badge
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(StudioSurfaceElevated)
                    .border(1.dp, StudioBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null,
                    tint = PremiereCyan,
                    modifier = Modifier.size(34.dp)
                )
            }

            // Empty State Title
            Text(
                text = "NO EDITING SEQUENCES YET",
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            // Diagnostic Notice Card
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E2E)),
                border = BorderStroke(1.dp, PremiereCyan.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PremiereCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "DEVICE VIDEO IMPORT READY",
                            color = PremiereCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap the button below to browse all accessible videos on your physical phone gallery using the native Android Photo Picker.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Cyan Action Button: "+ START NEW PROJECT"
            Button(
                onClick = onStartNewProject,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PremiereCyan,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .height(46.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "START NEW PROJECT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// --- TAB 1: PRESETS ---
@Composable
fun PresetsTab() {
    val presets = listOf(
        Triple("YouTube & 4K Cinema", "16:9 Landscape • 3840x2160 • 60 FPS", "LANDSCAPE"),
        Triple("TikTok & Instagram Reels", "9:16 Vertical • 1080x1920 • 30 FPS", "VERTICAL"),
        Triple("Instagram Square", "1:1 Square • 1080x1080 • 30 FPS", "SQUARE"),
        Triple("Anamorphic UltraWide", "21:9 Scope • 2560x1080 • 24 FPS", "CINEMA")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "SEQUENCE PRESETS (100% UNLOCKED)",
                color = PremiereCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        items(presets.size) { index ->
            val preset = presets[index]
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceElevated),
                border = BorderStroke(1.dp, StudioBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioSurface)
                                .border(1.dp, StudioBorder, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AspectRatio,
                                contentDescription = null,
                                tint = PremiereCyan
                            )
                        }
                        Column {
                            Text(
                                text = preset.first,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = preset.second,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Text(
                        text = preset.third,
                        color = PremiereCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

// --- TAB 2: EXPORTS ---
@Composable
fun ExportsTab() {
    Bo
