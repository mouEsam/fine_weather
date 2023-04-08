package com.iti.fineweather.features.common.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.features.common.utils.autoMirrored

@Composable
fun BackButton() {
    val navController = LocalNavigation.navController

    IconButton(
        onClick = { navController.navigateUp() },
        content = {
              Icon(
                  imageVector = Icons.Rounded.ArrowBack,
                  contentDescription = null,
                  tint = LocalContentColor.current,
                  modifier = Modifier.autoMirrored(),
              )
        },
    )
}

@Composable
fun BackButtonBalancer() {
    Box(
        modifier = Modifier.size(40.0.dp),
    )
}
