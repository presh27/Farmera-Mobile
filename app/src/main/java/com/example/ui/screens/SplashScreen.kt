package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProduceArtwork
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Minimal white background matching reference screenshot
    LaunchedEffect(Unit) {
        delay(1800)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .clickable { onSplashFinished() }
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Minimalist Brand Produce Icon
            ProduceArtwork(
                iconKey = "plantain",
                modifier = Modifier
                    .size(64.dp),
                backgroundColor = Color(0xFFF0FDF4)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Farmera bold brand wordmark matching "Grabber" in screenshot
            Text(
                text = "Farmera",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = FarmeraGreenPrimary,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline
            Text(
                text = "Closer to the source.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = FarmeraTextSecondary
            )
        }
    }
}
