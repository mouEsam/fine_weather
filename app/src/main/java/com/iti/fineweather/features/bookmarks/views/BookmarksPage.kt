package com.iti.fineweather.features.bookmarks.views

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.core.utils.getResult
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.bookmarks.entities.PlaceBookmark
import com.iti.fineweather.features.bookmarks.viewmodels.PlaceBookmarksViewModel
import com.iti.fineweather.features.bookmarks.viewmodels.PlaceTimezoneViewModel
import com.iti.fineweather.features.common.utils.rememberLocalizedDateTimeFormatter
import com.iti.fineweather.features.map.models.MapPlaceResult
import com.iti.fineweather.features.map.views.MapScreen
import com.iti.fineweather.features.weather.views.WeatherScreen
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun BookmarksPage(
    modifier: Modifier = Modifier,
    bookmarksViewModel: PlaceBookmarksViewModel = hiltViewModel()
) {
    val state: UiState<List<PlaceBookmark>> by bookmarksViewModel.uiState.collectAsState()

    BookmarksContent(
        modifier = modifier,
        bookmarksViewModel = bookmarksViewModel,
        bookmarksState = state,
    )
}

@Composable
fun BookmarksContent(
    modifier: Modifier = Modifier,
    bookmarksViewModel: PlaceBookmarksViewModel,
    bookmarksState: UiState<List<PlaceBookmark>> = UiState.Initial(),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (bookmarksState) {
            is UiState.Loaded -> {
                BookmarksList(
                    bookmarks = bookmarksState.data,
                    bookmarksViewModel = bookmarksViewModel,
                )
            }

            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            else -> NoBookmarks(
                bookmarksViewModel = bookmarksViewModel,
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BookmarksList(
    bookmarks: List<PlaceBookmark>,
    bookmarksViewModel: PlaceBookmarksViewModel,
) {
    val navController = LocalNavigation.navController
    val coroutineScope = rememberCoroutineScope()
    val showEmptyView = bookmarks.isEmpty()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (showEmptyView) {
            NoBookmarks(
                bookmarksViewModel = bookmarksViewModel,
            )
        } else {
            fun getCurrentDateTime(): OffsetDateTime {
                return LocalDateTime.now()
                    .atZone(ZoneId.systemDefault())
                    .toOffsetDateTime()
            }

            val timeFlow = remember {
                flow {
                    while (currentCoroutineContext().isActive) {
                        delay(1000)
                        emit(getCurrentDateTime())
                    }
                }.stateIn(coroutineScope, started = SharingStarted.Lazily, initialValue = getCurrentDateTime())
            }
            val dateFormatter = rememberLocalizedDateTimeFormatter("yyyy-MM-dd")
            val timeFormatter = rememberLocalizedDateTimeFormatter("hh:mm:ss a")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = LocalTheme.spaces.large,
                    vertical = LocalTheme.spaces.xLarge,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = LocalTheme.spaces.medium,
                    alignment = Alignment.Top,
                )
            ) {
                item {
                    AddPlace(
                        bookmarksViewModel = bookmarksViewModel,
                    )
                }

                items(bookmarks, PlaceBookmark::id) { item ->
                    val bookmark by rememberUpdatedState(item)
                    var showDeleteBookmark by remember { mutableStateOf(false) }
                    val dismissState = rememberDismissState(
                        confirmValueChange = {
                            showDeleteBookmark = true
                            true
                        }
                    )
                    if (showDeleteBookmark) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = {
                                Text(
                                    text = stringResource(R.string.bookmarks_delete_prompt_title),
                                    color = LocalTheme.colors.main,
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.bookmarks_delete_prompt_message),
                                    color = LocalTheme.colors.main,
                                )
                            },
                            dismissButton = {
                                ElevatedButton(
                                    onClick = {
                                        showDeleteBookmark = false
                                        coroutineScope.launch(Dispatchers.Main) {
                                            dismissState.reset()
                                        }
                                    },
                                ) {
                                    Text(
                                        text = stringResource(id = android.R.string.cancel),
                                        color = LocalTheme.colors.main,
                                    )
                                }
                            },
                            confirmButton = {
                                ElevatedButton(
                                    onClick = {
                                        showDeleteBookmark = false
                                        bookmarksViewModel.deleteBookmark(bookmark)
                                    },
                                ) {
                                    Text(
                                        text = stringResource(id = android.R.string.ok),
                                        color = LocalTheme.colors.main,
                                    )
                                }
                            },
                        )
                    }
                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier
                            .animateItemPlacement(),
                        background = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                if (dismissState.dismissDirection != null) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete_forever),
                                        contentDescription = null,
                                        tint = LocalTheme.colors.mainContent,
                                        modifier = Modifier.fillMaxHeight()
                                    )
                                }
                            }
                        },
                        directions = setOf(DismissDirection.StartToEnd),
                        dismissContent = {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.spacedBy(
                                    space = LocalTheme.spaces.medium,
                                    alignment = Alignment.Top,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(
                                        shape = LocalTheme.shapes.largeRoundedCornerShape,
                                    ).background(
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                Color.Unspecified,
                                                LocalTheme.colors.mainContent,
                                            ),
                                            startX = -100f,
                                        )
                                    ).clickable {
                                        navController.navigate(WeatherScreen.routeInfo.toNavReq(bookmark))
                                    }.padding(
                                        vertical = LocalTheme.spaces.medium,
                                        horizontal = LocalTheme.spaces.large,
                                    ),
                            ) {
                                val timeZoneViewModel: PlaceTimezoneViewModel =
                                    hiltViewModel(key = bookmark.id.toString())
                                LaunchedEffect(key1 = bookmark) {
                                    timeZoneViewModel.setLocation(LatLng(bookmark.latitude, bookmark.longitude))
                                }

                                Text(
                                    text = bookmark.city,
                                    color = LocalTheme.colors.mainContent,
                                    style = LocalTheme.typography.bodyBold,
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        space = LocalTheme.spaces.medium,
                                        alignment = Alignment.End,
                                    ),
                                ) {
                                    PlaceTime(
                                        timeFormatter = timeFormatter,
                                        timeFlow = timeFlow,
                                        timeZoneViewModel = timeZoneViewModel,
                                    )
                                    ConfigurationValue(
                                        label = dateFormatter.format(timeFlow.value),
                                    )
                                }
                            }
                        }
                    )
                }
                item {
                    Box(modifier = Modifier.navigationBarsPadding())
                }
            }

        }
    }
}

@Composable
fun PlaceTime(
    timeFormatter: DateTimeFormatter,
    timeFlow: StateFlow<OffsetDateTime>,
    timeZoneViewModel: PlaceTimezoneViewModel,
) {
    val timezoneState by timeZoneViewModel.timezoneState.collectAsState()
    val time by timeFlow.collectAsState()
    ConfigurationValue(
        label = timezoneState.data?.let { timezone ->
            timeFormatter.format(time.atZoneSameInstant(timezone))
        } ?: stringResource(R.string.bookmarks_loading_timezone),
    )
}

@Composable
fun NoBookmarks(
    bookmarksViewModel: PlaceBookmarksViewModel,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.medium,
            alignment = Alignment.CenterVertically,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(LocalTheme.spaces.xxLarge),
    ) {
        Text(
            text = stringResource(R.string.bookmarks_empty),
            style = LocalTheme.typography.labelBold,
            textAlign = TextAlign.Center,
        )
        AddPlace(
            bookmarksViewModel = bookmarksViewModel,
        )
    }
}

@Composable
private fun ConfigurationValue(label: String) {
    Text(
        text = label,
        color = LocalTheme.colors.main,
        style = LocalTheme.typography.bodyBold,
    )
}

@Composable
fun AddPlace(
    bookmarksViewModel: PlaceBookmarksViewModel,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentStackEntry = LocalNavigation.backStackEntry
    val navController = LocalNavigation.navController

    ElevatedButton(
        onClick = {
            navController.navigate(MapScreen.routeInfo.toNavRequest())
            lifecycleOwner.lifecycleScope.launch {
                val locationResult = currentStackEntry.getResult<MapPlaceResult>(MapScreen.RESULT_KEY)
                if (locationResult != null) {
                    Timber.d("GOT $locationResult")
                    bookmarksViewModel.addBookmark(
                        PlaceBookmark(
                            name = locationResult.name,
                            city = locationResult.city,
                            latitude = locationResult.location.latitude,
                            longitude = locationResult.location.longitude,
                        )
                    )
                }
            }
        }
    ) {
        Text(
            text = stringResource(R.string.bookmarks_add_bookmark),
            style = LocalTheme.typography.action,
            color = LocalTheme.colors.main,
        )
    }
}
