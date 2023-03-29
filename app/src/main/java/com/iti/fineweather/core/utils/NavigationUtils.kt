package com.iti.fineweather.core.utils

import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import com.iti.fineweather.core.navigation.NavRequest

fun NavController.navigate(request: NavRequest) {
    val routeLink = NavDeepLinkRequest.Builder.fromUri(request.uri).build()
    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(
            id,
            args = request.additionalArgs,
            navOptions = request.navOptions,
            navigatorExtras = request.navigatorExtras
        )
    } else {
        navigate(
            request.uri.toString(),
            navOptions = request.navOptions,
            navigatorExtras = request.navigatorExtras
        )
    }
}

fun NavOptions.Builder.setPopUpToFirst(
    navController: NavController,
    inclusive: Boolean,
    saveState: Boolean = false
): NavOptions.Builder {
    return setPopUpTo(
        destinationId = navController.backQueue.first().destination.id,
        inclusive = inclusive,
        saveState = saveState,
    )
}
