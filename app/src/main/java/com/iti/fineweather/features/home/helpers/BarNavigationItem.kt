package com.iti.fineweather.features.home.helpers

import androidx.annotation.StringRes
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.R
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.weather.views.WeatherPage

sealed class BarNavigationItem(
    val path: String,
    @StringRes val resourceId: Int,
) {
    object Home : BarNavigationItem("base", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            WeatherPage()
        }
    }
    object Bookmarks : BarNavigationItem("bookmarks", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            val navController = LocalNavigation.navController
            Button(onClick = {
                navController.navigate(MapScreen.routeInfo.toNavRequest())
            }) {
                Text("Open map")
            }
        }
    }

    @Composable
    abstract fun Content(modifier: Modifier)
}