package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FarmeraNavDestination
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraMintSurface
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@Composable
fun PlaceholderScreen(
    destination: FarmeraNavDestination,
    modifier: Modifier = Modifier
) {
    val title = when (destination) {
        FarmeraNavDestination.ORDERS -> "Orders & Tracking"
        FarmeraNavDestination.PROFILE -> "Farmera Profile"
        else -> destination.title
    }

    val icon: ImageVector = when (destination) {
        FarmeraNavDestination.ORDERS -> Icons.Default.ReceiptLong
        FarmeraNavDestination.PROFILE -> Icons.Default.Person
        else -> Icons.Default.Construction
    }

    val subtitle = when (destination) {
        FarmeraNavDestination.ORDERS -> "Live order tracking, dispatch updates, and farm harvest receipts are coming in the next build."
        FarmeraNavDestination.PROFILE -> "Saved farm delivery addresses, wholesale business profile, and unified Google login are coming in the next build."
        else -> "Coming in the next build."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(32.dp)
            .testTag("placeholder_screen_${destination.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(FarmeraMintSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FarmeraGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = FarmeraTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = FarmeraTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp
            )
        }
    }
}
