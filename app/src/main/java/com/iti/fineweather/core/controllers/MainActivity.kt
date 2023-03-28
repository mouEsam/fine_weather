package com.iti.fineweather.core.controllers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import com.iti.fineweather.core.navigation.*
import com.iti.fineweather.core.theme.FineWeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private val routes = listOf(
    SimpleRouteInfo(
        path = "test",
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Greeting("Android")
        }
    },
    SimpleRouteInfo(
        path = "test1",
        args = listOf(RouteArgument("i", dataType = NavType.IntType, defaultValue = 0))
    ) {
        SecondGreeting()
    }
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FineWeatherTheme {
                // A surface container using the 'background' color from the theme
                AppNavigation(
                    routes = routes
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val navController = LocalNavigation.navController
    Column {
        Text(text = "Hello $name!")
        Button(onClick = {
            Timber.d(routes.last().toNavUri(mapOf("i" to 1)).toString())
            navController.navigate(routes.last().toNavUri(mapOf("i" to 1)).toString())
        }) {
            Text("Navigate")
        }
    }
}

@Composable
fun SecondGreeting() {
    Text(text = "Hello! ${routes.last().args.first().get()}")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FineWeatherTheme {
        Greeting("Android")
    }
}