package com.iti.fineweather.core.utils

import androidx.lifecycle.Observer
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavOptions
import com.iti.fineweather.core.navigation.NavRequest
import com.iti.fineweather.features.map.views.MapScreen
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

suspend fun <T> NavBackStackEntry.getResult(key: String): T? = suspendCancellableCoroutine { continuation ->
    val resultObservable = savedStateHandle.getLiveData<T?>(MapScreen.RESULT_KEY)
    val observer = object : Observer<T?> {
        override fun onChanged(value: T?) {
            resultObservable.removeObserver(this)
            continuation.resume(value)
        }
    }
    resultObservable.observeForever(observer)
    continuation.invokeOnCancellation {
        resultObservable.removeObserver(observer)
    }
}
