package com.iti.fineweather.features.home.helpers

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.R
import com.iti.fineweather.features.alerts.views.AlertsPage
import com.iti.fineweather.features.bookmarks.views.BookmarksPage
import com.iti.fineweather.features.settings.views.SettingsPage
import com.iti.fineweather.features.weather.views.WeatherPage

sealed class BarNavigationItem(
    val path: String,
    @StringRes val resourceId: Int,
) {
    object Home : BarNavigationItem("base", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            WeatherPage(modifier = modifier)
        }
    }
    object Bookmarks : BarNavigationItem("bookmarks", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            BookmarksPage(modifier = modifier)
        }
    }

    object Alerts : BarNavigationItem("alerts", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            AlertsPage(modifier = modifier)
        }
    }

    object Settings : BarNavigationItem("settings", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            SettingsPage(modifier = modifier)
        }
    }

    @Suppress("FunctionName")
    @Composable
    abstract fun Content(modifier: Modifier)
}