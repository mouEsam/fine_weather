package com.iti.fineweather.core.utils

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.*
import com.iti.fineweather.core.navigation.NavRequest
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
    val resultObservable = savedStateHandle.getLiveData<T?>(key)
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

val NavType.Companion.DoubleType: NavType<Double>
    get() = object : NavType<Double>(true) {
            override val name: String
                get() = "float"

            override fun put(bundle: Bundle, key: String, value: Double) {
                bundle.putDouble(key, value)
            }

            @Suppress("DEPRECATION")
            override fun get(bundle: Bundle, key: String): Double {
                return bundle[key] as Double
            }

            override fun parseValue(value: String): Double {
                return value.toDouble()
            }
        }
