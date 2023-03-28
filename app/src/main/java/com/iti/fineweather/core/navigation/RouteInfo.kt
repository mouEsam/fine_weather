package com.iti.fineweather.core.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType

interface RouteInfo {
    val path: String

    val args: List<RouteArgument<*>>

    val screen: @Composable () -> Unit

    fun toRouteUri(): Uri {
        var uriBuilder = Uri.parse(path).buildUpon()
        for (arg in args) {
            uriBuilder = when (arg.argType) {
                RouteArgument.Type.PATH -> {
                    uriBuilder.appendPath("{${arg.name}}")
                }
                RouteArgument.Type.QUERY -> {
                    uriBuilder.appendQueryParameter(arg.name, "{${arg.name}}")
                }
            }
        }
        return uriBuilder.build()
    }

    fun toNavUri(args: Map<String, Any?> = mapOf()): Uri {
        var uriBuilder = Uri.parse(path).buildUpon()
        for (arg in this.args) {
            val value = args[arg.name]
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

class MissingArgumentException(val arg: RouteArgument<*>): Exception()

class InvalidNullableArgumentException(val arg: RouteArgument<*>): Exception()
