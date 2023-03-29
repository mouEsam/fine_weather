package com.iti.fineweather.features.home.views

import androidx.annotation.VisibleForTesting
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import com.iti.fineweather.core.navigation.*
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.core.utils.setPopUpToFirst
import com.iti.fineweather.features.home.helpers.BarNavigationItem
import timber.log.Timber

object HomeScreen: Screen<HomeScreen.HomeRoute> {

    override val routeInfo: HomeRoute = HomeRoute

    object HomeRoute: RouteInfo {
        override val path: String = "home"
        override val args: List<RouteArgument<*>> = listOf(RouteArgument(
            name = "segment",
            dataType = NavType.StringType,
            argType = RouteArgument.Type.PATH,
            defaultValue = BarNavigationItem.Home.path,
        ))
        override val screen: @Composable () -> Unit = @Composable {
            HomeScreen()
        }

        fun NavController.pageRequest(page: BarNavigationItem): NavRequest {
            return NavRequest(
                uri = toNavUri(args = mapOf(args.first().name to page.path)),
                navOptions = NavOptions.Builder()
                    .setPopUpToFirst(this, inclusive = true)
                    .setLaunchSingleTop(true)
                    .setRestoreState(true)
                    .build()
            )
        }
    }
}

private val items = listOf(
    BarNavigationItem.Home,
    BarNavigationItem.Bookmarks,
)

@Composable
@VisibleForTesting
fun HomeScreen() {
    LocalNavigation.navController
    val currentBackStackEntry = LocalNavigation.backStackEntry
    val initialSegment = HomeScreen.HomeRoute.args.first().get() as String? ?: ""
    var segment by remember { mutableStateOf(initialSegment) }
    val currentSelected by remember {
        derivedStateOf {
            items.firstOrNull { screen -> segment == screen.path }
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigation {
                items.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                        label = { Text(stringResource(screen.resourceId)) },
                        selected = screen == currentSelected,
                        onClick = {
                            currentBackStackEntry.arguments?.putString(
                                HomeScreen.HomeRoute.args.first().name, screen.path
                            )
                            segment = screen.path
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentSelected,
            transitionSpec = {
                (
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                ).using(
                    SizeTransform(clip = false)
                )
            }
        ) { currentSelected ->
            currentSelected?.Content(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun PlaceholderPage(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier,
    )
}
