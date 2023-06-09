package com.iti.fineweather.core.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object LocalNavigation {
    val navController: NavController
        @Composable
        @ReadOnlyComposable
        get() = LocalNavController.current
    val routeInfo: RouteInfo
        @Composable
        @ReadOnlyComposable
        get() = LocalRouteInfo.current
    val backStackEntry: NavBackStackEntry
        @Composable
        @ReadOnlyComposable
        get() = LocalBackStackEntry.current
}

private val LocalNavController: ProvidableCompositionLocal<NavController> =
    staticCompositionLocalOf { error("not provided") }

private val LocalRouteInfo: ProvidableCompositionLocal<RouteInfo> = staticCompositionLocalOf { error("not provided") }

private val LocalBackStackEntry: ProvidableCompositionLocal<NavBackStackEntry> =
    staticCompositionLocalOf { error("not provided") }

@Composable
fun AppNavigation(
    routes: List<RouteInfo>,
    modifier: Modifier = Modifier
) {
    val routesState by rememberUpdatedState(routes)
    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController,
    ) {
        NavHost(
            navController = navController,
            startDestination = routesState.first().toRoute(),
            modifier = modifier
        ) {
            for (route in routesState) {
                composable(
                    route = route.toRoute(),
                    arguments = route.args.map { arg ->
                        navArgument(arg.name) {
                            type = arg.dataType
                            nullable = arg.nullable
                            defaultValue = arg.defaultValue
                        }
                    }
                ) { backStackEntry ->
                    CompositionLocalProvider(
                        LocalRouteInfo provides route,
                        LocalBackStackEntry provides backStackEntry,
                    ) {
                        route.screen()
                    }
                }
            }
        }
    }
}