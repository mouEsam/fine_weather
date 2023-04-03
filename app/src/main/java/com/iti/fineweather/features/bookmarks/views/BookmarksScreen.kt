package com.iti.fineweather.features.bookmarks.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen

object BookmarksScreen: Screen<BookmarksScreen.BookmarksRoute> {

    override val routeInfo: BookmarksRoute = BookmarksRoute

    object BookmarksRoute: RouteInfo {
        override val path: String = "bookmarks"
        override val screen: @Composable () -> Unit = @Composable {
            BookmarksScreen()
        }
    }
}

@Composable
@VisibleForTesting
fun BookmarksScreen() {
    CompositionScaffoldProvider {
        Scaffold(
            scaffoldState = LocalScaffold.current,
        ) { innerPadding ->
            BookmarksPage(
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

