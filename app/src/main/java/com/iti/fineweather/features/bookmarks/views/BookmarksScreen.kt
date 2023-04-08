package com.iti.fineweather.features.bookmarks.views

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.CompositionScaffoldProvider
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.navigation.RouteInfo
import com.iti.fineweather.core.navigation.Screen
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.features.common.views.BackButton
import com.iti.fineweather.features.common.views.BackButtonBalancer
import com.iti.fineweather.features.common.views.Background
import com.iti.fineweather.features.weather.helpers.Constants

object BookmarksScreen : Screen<BookmarksScreen.BookmarksRoute> {

    override val routeInfo: BookmarksRoute = BookmarksRoute

    object BookmarksRoute : RouteInfo {
        override val path: String = "bookmarks"
        override val screen: @Composable () -> Unit = @Composable {
            BookmarksScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@VisibleForTesting
fun BookmarksScreen() {
    val color = LocalTheme.colors.main.copy(alpha = Constants.BACKGROUND_COLOR_ALPHA)
    Background {
        CompositionScaffoldProvider {
            Scaffold(
                snackbarHost = { SnackbarHost(LocalScaffold.snackbarHost) },
                containerColor = color,
                contentColor = LocalTheme.colors.mainContent,
                contentWindowInsets = WindowInsets(top = 0.dp),
                topBar = {
                    Surface(
                        color = color,
                        contentColor = LocalTheme.colors.mainContent,
                        modifier = Modifier
                            .background(color)
                            .statusBarsPadding()
                    ) {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = color,
                                titleContentColor = LocalTheme.colors.mainContent,
                                navigationIconContentColor = LocalTheme.colors.mainContent,
                            ),
                            navigationIcon = {
                                BackButton()
                            },
                            actions = { BackButtonBalancer() },
                            title = {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = stringResource(R.string.bookmarks_title),
                                    style = LocalTheme.typography.title,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        )
                    }
                },
            ) { innerPadding ->
                Surface(
                    color = color,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = color),
                    contentColor = LocalTheme.colors.mainContent,
                ) {
                    BookmarksPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

