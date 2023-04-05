package com.iti.fineweather.features.bookmarks.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.utils.getResult
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.bookmarks.entities.PlaceBookmark
import com.iti.fineweather.features.bookmarks.viewmodels.PlaceBookmarksViewModel
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.map.views.MapScreen
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun BookmarksPage(
    modifier: Modifier = Modifier,
    bookmarksViewModel: PlaceBookmarksViewModel = hiltViewModel<PlaceBookmarksViewModel>()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentStackEntry = LocalNavigation.backStackEntry;
    val navController = LocalNavigation.navController;
    val state by bookmarksViewModel.uiState.collectAsState()
    Column(
        modifier = modifier,
    ) {

        Text(text = state.toString())
        Button(onClick = {
            navController.navigate(MapScreen.routeInfo.toNavRequest())
            lifecycleOwner.lifecycleScope.launch {
                val locationResult = currentStackEntry.getResult<MapPlaceResult>(MapScreen.RESULT_KEY)
                if (locationResult != null) {
                    Timber.d("GOT $locationResult")
                    bookmarksViewModel.addBookmark(PlaceBookmark(
                        name = locationResult.name,
                        city = locationResult.city,
                        latitude = locationResult.location.latitude,
                        longitude = locationResult.location.longitude,
                    ))
                }
            }
        }) {
            Text("Add place")
        }
    }

}

