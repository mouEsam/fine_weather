package com.iti.fineweather.features.common.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.iti.fineweather.R

@Composable
fun Background(
    painter: Painter? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Box {
        Image(
            painter = painter ?: painterResource(R.mipmap.idle),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        content?.invoke()
    }
}