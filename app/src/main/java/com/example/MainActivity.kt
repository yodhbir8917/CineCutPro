package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Cinema Dark Color Palette (100% Free Forever Studio Theme)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF00E5FF),          // Cyan accent
    onPrimary = Color(0xFF002026),
    primaryContainer = Color(0xFF004D59),
    onPrimaryContainer = Color(0xFF8CEEFF),
    secondary = Color(0xFFFF4081),        // Magenta cut accent
    onSecondary = Color(0xFF3E001D),
    background = Color(0xFF0D0F14),       // Deep studio black
    onBackground = Color(0xFFF1F3F9),
    surface = Color(0xFF161922),          // Track slate
    onSurface = Color(0xFFE2E4EC),
    surfaceVariant = Color(0xFF222633),
    outline = Color(0xFF33384A)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme(colorScheme = DarkColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CineCutFoundationScreen()
                }
            }
        }
    }
}

@Composable
fun CineCutFoundationScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = "CineCut Pro Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "CineCut Pro",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Free Forever Pro Video Editor • Offline Native Engine",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { /* Media picker / New project action */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.VideoLibrary,
                    contentDescription = null
                )
                Text(
                    text = "  Open Media Studio",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
