package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.model.Product
import com.example.model.ProductImageResolver
import com.example.ui.theme.FarmeraSurfaceNeutral

@Composable
fun ProductAsyncImage(
    product: Product,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    artworkPadding: Dp = 14.dp,
    backgroundColor: Color = FarmeraSurfaceNeutral
) {
    val imageModel = ProductImageResolver.resolveImage(product.slug)

    SubcomposeAsyncImage(
        model = imageModel,
        contentDescription = product.name,
        contentScale = contentScale,
        modifier = modifier.fillMaxSize(),
        loading = {
            ProduceArtwork(
                iconKey = product.iconKey,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(artworkPadding),
                backgroundColor = backgroundColor
            )
        },
        error = {
            ProduceArtwork(
                iconKey = product.iconKey,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(artworkPadding),
                backgroundColor = backgroundColor
            )
        }
    )
}
