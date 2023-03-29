package com.iti.fineweather.core.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.Navigator

interface RouteInfo {
    val path: String

    val args: List<RouteArgument<*>>
        get() = listOf()

    val screen: @Composable () -> Unit

    fun toRoute(): String {
        val paths = mutableListOf(path)
        var uriBuilder = Uri.parse("").buildUpon()
        for (arg in args) {
           when (arg.argType) {
                RouteArgument.Type.PATH -> {
                    paths += "{${arg.name}}"
                }
                RouteArgument.Type.QUERY -> {
                    uriBuilder = uriBuilder.appendQueryParameter(arg.name, "{${arg.name}}")
                }
            }
        }
        return paths.joinToString(separator = "/") + uriBuilder.build()
    }

    fun toNavUri(args: Map<String, Any?> = mapOf()): Uri {
        var uriBuilder = Uri.parse(path).buildUpon()
        for (arg in this.args) {
            val value = args[arg.name] ?: arg.defaultValue
            if (value == null && (!arg.nullable || arg.argType == RouteArgument.Type.PATH)) {
                throw MissingArgumentException(arg)
            }
            uriBuilder = when (arg.argType) {
                RouteArgument.Type.PATH -> {
                    uriBuilder.appendPath("$value")
                }
                RouteArgument.Type.QUERY -> {
                    if (value != null) {
                        uriBuilder = uriBuilder.appendQueryParameter(arg.name, "$value")
                    }
                    uriBuilder
                }
            }
        }
        return uriBuilder.build()
    }

    fun toNavRequest(
        args: Map<String, Any?> = mapOf(),
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null
    ): NavRequest {
        return NavRequest(
            uri = toNavUri(args),
            navOptions = navOptions,
            navigatorExtras = navigatorExtras,
        )
    }
}

class SimpleRouteInfo(
    override val path: String,
    override val args: List<RouteArgument<*>> = listOf(),
    override val screen: @Composable () -> Unit,
): RouteInfo

data class RouteArgument<T>(
    val name: String,
    val dataType: NavType<T>,
    val defaultValue: T? = null,
    val nullable: Boolean = dataType.isNullableAllowed,
    val argType: Type = Type.QUERY,
) {

    init {
        if (nullable && !dataType.isNullableAllowed) {
            throw InvalidNullableArgumentException(this)
        }
        if (defaultValue == null && !dataType.isNullableAllowed) {
            throw InvalidNullableArgumentException(this)
        }
    }

    enum class Type {
        PATH,
        QUERY
    }

    @Composable
    fun get(): T? {
        return LocalNavigation.backStackEntry.arguments?.let { bundle ->
            dataType[bundle, name]
        }
    }
}

data class NavRequest(
    val uri: Uri,
    val additionalArgs: Bundle? = null,
    val navOptions: NavOptions? = null,
    val navigatorExtras: Navigator.Extras? = null
)

class MissingArgumentException(val arg: RouteArgument<*>): Exception()

class InvalidNullableArgumentException(val arg: RouteArgument<*>): Exception()

interface Screen<R: RouteInfo> {
    val routeInfo: R
}
