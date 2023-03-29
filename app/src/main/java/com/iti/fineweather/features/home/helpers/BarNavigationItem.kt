package com.iti.fineweather.features.home.helpers

import com.iti.fineweather.features.home.views.PlaceholderPage
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.R

sealed class BarNavigationItem(
    val path: String,
    @StringRes val resourceId: Int,
) {
    object Home : BarNavigationItem("base", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            PlaceholderPage(
                title = "First",
                modifier = modifier
            )
        }
    }
    object Bookmarks : BarNavigationItem("bookmarks", R.string.placeholder) {
        @Composable
        override fun Content(modifier: Modifier) {
            PlaceholderPage(
                title = "Second",
                modifier = modifier
            )
        }
    }

    @Composable
    abstract fun Content(modifier: Modifier)
}