package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FarmeraSurfaceNeutral

@Composable
fun ProduceArtwork(
    iconKey: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = FarmeraSurfaceNeutral
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val w = size.width
            val h = size.height

            when (iconKey.lowercase()) {
                "yam" -> {
                    // Benue Puna Yam tuber
                    val tuberBrush = Brush.linearGradient(
                        colors = listOf(Color(0xFF785438), Color(0xFF533722), Color(0xFF3E2412)),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                    // Main tuber body
                    drawRoundRect(
                        brush = tuberBrush,
                        topLeft = Offset(w * 0.15f, h * 0.28f),
                        size = Size(w * 0.70f, h * 0.44f),
                        cornerRadius = CornerRadius(w * 0.18f, h * 0.18f)
                    )
                    // Yam texture rings
                    val ringColor = Color(0x33FFFFFF)
                    drawLine(ringColor, Offset(w * 0.32f, h * 0.32f), Offset(w * 0.32f, h * 0.68f), strokeWidth = 3f)
                    drawLine(ringColor, Offset(w * 0.50f, h * 0.30f), Offset(w * 0.50f, h * 0.70f), strokeWidth = 3f)
                    drawLine(ringColor, Offset(w * 0.68f, h * 0.32f), Offset(w * 0.68f, h * 0.68f), strokeWidth = 3f)

                    // Fresh cut white slice
                    drawCircle(
                        color = Color(0xFFFBF8EE),
                        radius = w * 0.19f,
                        center = Offset(w * 0.76f, h * 0.50f)
                    )
                    drawCircle(
                        color = Color(0xFF5D3E27),
                        radius = w * 0.19f,
                        center = Offset(w * 0.76f, h * 0.50f),
                        style = Stroke(width = 4f)
                    )
                }

                "plantain" -> {
                    // Bunch of ripe plantains
                    val path1 = Path().apply {
                        moveTo(w * 0.2f, h * 0.75f)
                        cubicTo(w * 0.3f, h * 0.35f, w * 0.65f, h * 0.3f, w * 0.85f, h * 0.35f)
                        cubicTo(w * 0.75f, h * 0.55f, w * 0.45f, h * 0.85f, w * 0.2f, h * 0.75f)
                        close()
                    }
                    val path2 = Path().apply {
                        moveTo(w * 0.15f, h * 0.65f)
                        cubicTo(w * 0.25f, h * 0.25f, w * 0.60f, h * 0.2f, w * 0.80f, h * 0.25f)
                        cubicTo(w * 0.70f, h * 0.45f, w * 0.40f, h * 0.75f, w * 0.15f, h * 0.65f)
                        close()
                    }
                    // Back plantain
                    drawPath(path2, Brush.linearGradient(listOf(Color(0xFFEAB308), Color(0xFFCA8A04))))
                    // Front plantain
                    drawPath(path1, Brush.linearGradient(listOf(Color(0xFFFACC15), Color(0xFFEAB308))))
                    // Green tips & stem crown
                    drawCircle(Color(0xFF65A30D), radius = w * 0.07f, center = Offset(w * 0.83f, h * 0.30f))
                    drawCircle(Color(0xFF4D7C0F), radius = w * 0.05f, center = Offset(w * 0.18f, h * 0.70f))
                }

                "tomatoes" -> {
                    // Plump red Roma tomato
                    val tomatoRed = Brush.radialGradient(
                        colors = listOf(Color(0xFFEF4444), Color(0xFFDC2626), Color(0xFF991B1B)),
                        center = Offset(w * 0.45f, h * 0.45f),
                        radius = w * 0.4f
                    )
                    drawCircle(brush = tomatoRed, radius = w * 0.36f, center = Offset(w * 0.50f, h * 0.54f))
                    // Highlight gloss
                    drawCircle(
                        color = Color(0x66FFFFFF),
                        radius = w * 0.10f,
                        center = Offset(w * 0.38f, h * 0.42f)
                    )
                    // Green calyx/stem on top
                    val stemPath = Path().apply {
                        moveTo(w * 0.50f, h * 0.16f)
                        lineTo(w * 0.48f, h * 0.25f)
                        lineTo(w * 0.35f, h * 0.26f)
                        lineTo(w * 0.46f, h * 0.30f)
                        lineTo(w * 0.40f, h * 0.38f)
                        lineTo(w * 0.50f, h * 0.32f)
                        lineTo(w * 0.60f, h * 0.38f)
                        lineTo(w * 0.54f, h * 0.30f)
                        lineTo(w * 0.65f, h * 0.26f)
                        lineTo(w * 0.52f, h * 0.25f)
                        close()
                    }
                    drawPath(stemPath, Color(0xFF15803D))
                }

                "pepper" -> {
                    // Scotch bonnet / Atarodo
                    val pepperPath = Path().apply {
                        moveTo(w * 0.48f, h * 0.28f)
                        cubicTo(w * 0.25f, h * 0.32f, w * 0.20f, h * 0.62f, w * 0.38f, h * 0.78f)
                        cubicTo(w * 0.48f, h * 0.86f, w * 0.55f, h * 0.86f, w * 0.64f, h * 0.78f)
                        cubicTo(w * 0.82f, h * 0.62f, w * 0.78f, h * 0.32f, w * 0.52f, h * 0.28f)
                        close()
                    }
                    drawPath(
                        pepperPath,
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFF97316), Color(0xFFEA580C), Color(0xFFC2410C)),
                            center = Offset(w * 0.45f, h * 0.45f),
                            radius = w * 0.4f
                        )
                    )
                    // Green stem
                    val stem = Path().apply {
                        moveTo(w * 0.50f, h * 0.30f)
                        cubicTo(w * 0.52f, h * 0.18f, w * 0.60f, h * 0.14f, w * 0.64f, h * 0.12f)
                        lineTo(w * 0.60f, h * 0.10f)
                        cubicTo(w * 0.50f, h * 0.14f, w * 0.46f, h * 0.22f, w * 0.47f, h * 0.30f)
                        close()
                    }
                    drawPath(stem, Color(0xFF16A34A))
                }

                "onions" -> {
                    // Violet red onion
                    val onionBrush = Brush.radialGradient(
                        colors = listOf(Color(0xFFC084FC), Color(0xFF9333EA), Color(0xFF6B21A8)),
                        center = Offset(w * 0.42f, h * 0.42f),
                        radius = w * 0.38f
                    )
                    drawCircle(onionBrush, radius = w * 0.34f, center = Offset(w * 0.50f, h * 0.55f))
                    // Sprout neck
                    val neck = Path().apply {
                        moveTo(w * 0.46f, h * 0.24f)
                        lineTo(w * 0.50f, h * 0.14f)
                        lineTo(w * 0.54f, h * 0.24f)
                        close()
                    }
                    drawPath(neck, Color(0xFF6B21A8))
                    // Vertical natural ridges
                    val ridgeColor = Color(0x33FFFFFF)
                    drawArc(
                        color = ridgeColor,
                        startAngle = 100f,
                        sweepAngle = 160f,
                        useCenter = false,
                        topLeft = Offset(w * 0.26f, h * 0.24f),
                        size = Size(w * 0.48f, h * 0.62f),
                        style = Stroke(width = 2.5f)
                    )
                }

                "palmoil" -> {
                    // Rich red palm oil canister/jug
                    val jugBrush = Brush.linearGradient(
                        colors = listOf(Color(0xFFEA580C), Color(0xFFC2410C), Color(0xFF9A3412)),
                        start = Offset(0f, 0f),
                        end = Offset(w, h)
                    )
                    drawRoundRect(
                        brush = jugBrush,
                        topLeft = Offset(w * 0.28f, h * 0.32f),
                        size = Size(w * 0.44f, h * 0.52f),
                        cornerRadius = CornerRadius(w * 0.08f, h * 0.08f)
                    )
                    // Cap / spout
                    drawRect(Color(0xFFEAB308), Offset(w * 0.42f, h * 0.22f), Size(w * 0.16f, h * 0.10f))
                    // Handle
                    val handle = Path().apply {
                        moveTo(w * 0.72f, h * 0.38f)
                        cubicTo(w * 0.88f, h * 0.44f, w * 0.88f, h * 0.62f, w * 0.72f, h * 0.68f)
                    }
                    drawPath(handle, Color(0xFF9A3412), style = Stroke(width = 7f))
                    // Pure droplet glow
                    drawCircle(Color(0xFFFED7AA), radius = w * 0.05f, center = Offset(w * 0.40f, h * 0.48f))
                }

                "garri" -> {
                    // Golden yellow bowl of Garri
                    val bowlColor = Color(0xFF78350F)
                    val garriYellow = Brush.radialGradient(
                        colors = listOf(Color(0xFFFEF08A), Color(0xFFFDE047), Color(0xFFEAB308)),
                        center = Offset(w * 0.50f, h * 0.44f),
                        radius = w * 0.35f
                    )
                    // Heaping mound
                    drawCircle(garriYellow, radius = w * 0.30f, center = Offset(w * 0.50f, h * 0.46f))
                    // Calabash bowl
                    val bowl = Path().apply {
                        moveTo(w * 0.16f, h * 0.50f)
                        cubicTo(w * 0.20f, h * 0.84f, w * 0.80f, h * 0.84f, w * 0.84f, h * 0.50f)
                        close()
                    }
                    drawPath(bowl, bowlColor)
                    // Grain speckles
                    drawCircle(Color(0xFFCA8A04), radius = 2.5f, center = Offset(w * 0.45f, h * 0.40f))
                    drawCircle(Color(0xFFCA8A04), radius = 2.5f, center = Offset(w * 0.55f, h * 0.36f))
                    drawCircle(Color(0xFFCA8A04), radius = 2.5f, center = Offset(w * 0.62f, h * 0.44f))
                }

                "beans" -> {
                    // Honey beans / Oloyin sack & grains
                    val sackBrush = Brush.linearGradient(listOf(Color(0xFFD97706), Color(0xFF92400E)))
                    drawRoundRect(
                        sackBrush,
                        Offset(w * 0.24f, h * 0.32f),
                        Size(w * 0.52f, h * 0.52f),
                        CornerRadius(w * 0.12f, h * 0.12f)
                    )
                    // Fold rim
                    drawRoundRect(
                        Color(0xFF78350F),
                        Offset(w * 0.20f, h * 0.28f),
                        Size(w * 0.60f, h * 0.12f),
                        CornerRadius(w * 0.06f, h * 0.06f)
                    )
                    // Beans grains
                    drawOval(Color(0xFFB45309), Offset(w * 0.36f, h * 0.46f), Size(w * 0.12f, h * 0.08f))
                    drawOval(Color(0xFF78350F), Offset(w * 0.52f, h * 0.52f), Size(w * 0.12f, h * 0.08f))
                }

                "maize" -> {
                    // Sweet corn / maize cob
                    val cobBrush = Brush.linearGradient(listOf(Color(0xFFFDE047), Color(0xFFEAB308)))
                    val cob = Path().apply {
                        moveTo(w * 0.32f, h * 0.70f)
                        cubicTo(w * 0.36f, h * 0.25f, w * 0.64f, h * 0.25f, w * 0.68f, h * 0.70f)
                        close()
                    }
                    drawPath(cob, cobBrush)
                    // Husk leaves
                    val husk = Path().apply {
                        moveTo(w * 0.22f, h * 0.82f)
                        cubicTo(w * 0.28f, h * 0.55f, w * 0.36f, h * 0.40f, w * 0.38f, h * 0.45f)
                        cubicTo(w * 0.36f, h * 0.65f, w * 0.38f, h * 0.85f, w * 0.50f, h * 0.88f)
                        close()
                    }
                    drawPath(husk, Color(0xFF65A30D))
                }

                "orange" -> {
                    // Juicy orange with green leaf
                    val orangeBrush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFB923C), Color(0xFFF97316), Color(0xFFEA580C)),
                        center = Offset(w * 0.46f, h * 0.52f),
                        radius = w * 0.36f
                    )
                    drawCircle(orangeBrush, radius = w * 0.34f, center = Offset(w * 0.50f, h * 0.54f))
                    // Small leaf & stem
                    val leaf = Path().apply {
                        moveTo(w * 0.50f, h * 0.20f)
                        cubicTo(w * 0.65f, h * 0.12f, w * 0.75f, h * 0.20f, w * 0.68f, h * 0.28f)
                        cubicTo(w * 0.58f, h * 0.30f, w * 0.52f, h * 0.24f, w * 0.50f, h * 0.20f)
                        close()
                    }
                    drawPath(leaf, Color(0xFF16A34A))
                }

                "avocado" -> {
                    // Avocado half with seed
                    val avoBody = Path().apply {
                        moveTo(w * 0.50f, h * 0.16f)
                        cubicTo(w * 0.32f, h * 0.24f, w * 0.22f, h * 0.54f, w * 0.30f, h * 0.76f)
                        cubicTo(w * 0.38f, h * 0.88f, w * 0.62f, h * 0.88f, w * 0.70f, h * 0.76f)
                        cubicTo(w * 0.78f, h * 0.54f, w * 0.68f, h * 0.24f, w * 0.50f, h * 0.16f)
                        close()
                    }
                    // Dark skin
                    drawPath(avoBody, Color(0xFF14532D))
                    // Creamy butter flesh
                    val inner = Path().apply {
                        moveTo(w * 0.50f, h * 0.22f)
                        cubicTo(w * 0.36f, h * 0.28f, w * 0.28f, h * 0.54f, w * 0.34f, h * 0.72f)
                        cubicTo(w * 0.40f, h * 0.82f, w * 0.60f, h * 0.82f, w * 0.66f, h * 0.72f)
                        cubicTo(w * 0.72f, h * 0.54f, w * 0.64f, h * 0.28f, w * 0.50f, h * 0.22f)
                        close()
                    }
                    drawPath(inner, Brush.radialGradient(listOf(Color(0xFFFEF08A), Color(0xFFA3E635))))
                    // Seed
                    drawCircle(Color(0xFF78350F), radius = w * 0.14f, center = Offset(w * 0.50f, h * 0.62f))
                }

                else -> {
                    // Default fresh leaf emblem
                    drawCircle(Color(0xFFDCFCE7), radius = w * 0.35f, center = Offset(w * 0.5f, h * 0.5f))
                    val leaf = Path().apply {
                        moveTo(w * 0.5f, h * 0.25f)
                        cubicTo(w * 0.7f, h * 0.35f, w * 0.75f, h * 0.65f, w * 0.5f, h * 0.75f)
                        cubicTo(w * 0.25f, h * 0.65f, w * 0.3f, h * 0.35f, w * 0.5f, h * 0.25f)
                        close()
                    }
                    drawPath(leaf, Color(0xFF16A34A))
                }
            }
        }
    }
}

@Composable
fun CircularProduceAvatar(
    iconKey: String,
    size: Dp = 38.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        ProduceArtwork(
            iconKey = iconKey,
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.White
        )
    }
}
