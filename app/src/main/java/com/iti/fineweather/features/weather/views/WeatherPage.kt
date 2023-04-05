package com.iti.fineweather.features.weather.views

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.navigation.LocalNavigation
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.core.utils.navigate
import com.iti.fineweather.features.alerts.views.AlertsScreen
import com.iti.fineweather.features.bookmarks.views.BookmarksScreen
import com.iti.fineweather.features.common.utils.rememberLocalizedDateTimeFormatter
import com.iti.fineweather.features.settings.views.SettingsScreen
import com.iti.fineweather.features.weather.models.*
import com.iti.fineweather.features.weather.viewmodels.WeatherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*


@Composable
fun WeatherPage(
    modifier: Modifier = Modifier,
    weatherLocation: WeatherLocation? = null,
    weatherViewModel: WeatherViewModel = hiltViewModel(key = weatherLocation?.toString())
) {
    LaunchedEffect(key1 = weatherViewModel) {
        if (weatherViewModel.uiState.value is UiState.Initial) {
            weatherViewModel.getWeatherData(weatherLocation)
        }
    }

    val uiState by weatherViewModel.uiState.collectAsState()
    LaunchedEffect(key1 = uiState) {
        Timber.d(uiState.toString())
    }

    WeatherContent(
        modifier = modifier,
        weatherViewDataState = uiState,
    )
}


@Preview
@Composable
fun PreviewWeatherContent() {
    WeatherContent()
}

@Composable
fun WeatherContent(
    modifier: Modifier = Modifier,
    weatherViewDataState: UiState<WeatherViewData> = UiState.Initial(),
) {
    val navController = LocalNavigation.navController
    val weatherViewData = weatherViewDataState.data
    val color = weatherViewDataState.data?.now?.weatherState?.color ?: LocalTheme.colors.main
    val colorPalette = getColorPalette(color)
    val backgroundColor = Color(colorPalette.dominantSwatch?.rgb!!)
    val foregroundColor = Color(colorPalette.dominantSwatch?.bodyTextColor!!)
    Surface(
        color = backgroundColor,
        contentColor = foregroundColor,
        modifier = Modifier.background(color = backgroundColor).then(modifier),
    ) {
        val dateFormatter = rememberLocalizedDateTimeFormatter("yyyy-MM-dd")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(
                    vertical = LocalTheme.spaces.medium,
                ).padding(
                    start = LocalTheme.spaces.large,
                    end = LocalTheme.spaces.small,
                ),
                verticalAlignment = Alignment.Top,
            ) {
                Row(
                    modifier = Modifier.weight(1.0f).padding(top = LocalTheme.spaces.medium),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn, contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(LocalTheme.spaces.medium))
                    Text(
                        text = weatherViewData?.location?.city ?: stringResource(R.string.placeholder_location),
                        modifier = Modifier.weight(1.0f),
                        style = LocalTheme.typography.title,
                    )
                    if (weatherViewDataState is UiState.Loading) {
                        CircularProgressIndicator()
                    } else if (weatherViewDataState is UiState.Error) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline, contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.width(LocalTheme.spaces.medium))
                IconButton(onClick = {
                    navController.navigate(SettingsScreen.routeInfo.toNavRequest())
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Settings, contentDescription = null
                    )
                }
                IconButton(onClick = {
                    navController.navigate(BookmarksScreen.routeInfo.toNavRequest())
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Bookmark, contentDescription = null
                    )
                }
                IconButton(onClick = {
                    navController.navigate(AlertsScreen.routeInfo.toNavRequest())
                }) {
                    Icon(
                        imageVector = Icons.Outlined.CrisisAlert, contentDescription = null
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.Start).padding(start = LocalTheme.spaces.large)
            ) {
                Text(
                    text = weatherViewData?.let { getCurrentDate(it.timezone) }
                        ?: stringResource(R.string.placeholder_time),
                    style = LocalTheme.typography.bodyBold,
                )
                Text(
                    text = weatherViewData?.let { getCurrentTimeText(it.timezone) }
                        ?: stringResource(R.string.placeholder_time),
                    style = LocalTheme.typography.bodyBold,
                )
            }

            val painter: Painter = if (weatherViewData != null) {
                val context = LocalContext.current
                val weatherState = weatherViewData.now.weatherState
                rememberAsyncImagePainter(
                    model = ImageRequest.Builder(context).decoderFactory(SvgDecoder.Factory())
                        .data("android.resource://${context.applicationContext.packageName}/${weatherState.icon}")
                        .size(Size.ORIGINAL).build()
                )
            } else {
                painterResource(R.drawable.not_available)
            }

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.6f),
            )

            Text(
                text = weatherViewData?.now?.weatherState?.main ?: stringResource(R.string.placeholder_weather),
                style = LocalTheme.typography.subtitle,
            )
            Text(
                text = if (weatherViewData != null) "${weatherViewData.now.temperature}°"
                else stringResource(R.string.placeholder_temperature),
                style = LocalTheme.typography.headerLarge,
            )

            WeatherParamRow(
                weatherData = weatherViewData?.now,
                weatherUnitData = weatherViewData?.units,
                modifier = Modifier.fillMaxWidth().padding(horizontal = LocalTheme.spaces.xxLarge),
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = LocalTheme.spaces.medium),
                verticalArrangement = Arrangement.spacedBy(LocalTheme.spaces.medium),
                horizontalAlignment = Alignment.Start,
            ) {
                var selectedTab by rememberSaveable { mutableStateOf(0) }
                val items = listOf(
                    stringResource(R.string.home_tab_today, dateFormatter.format(LocalDate.now())),
                    stringResource(R.string.home_tab_this_week),
                    stringResource(R.string.home_tab_summery)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = LocalTheme.spaces.large),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = LocalTheme.spaces.xLarge,
                        alignment = Alignment.Start,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items(count = items.size) { index ->
                        val selected = index == selectedTab
                        Text(
                            text = items[index],
                            modifier = Modifier.clickable(enabled = !selected) {
                                selectedTab = index
                            }.padding(LocalTheme.spaces.small),
                            fontWeight = if (selected) FontWeight.W600 else FontWeight.W300,
                            style = LocalTheme.typography.body,
                        )
                    }
                }
                AnimatedContent(
                    targetState = selectedTab, transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { width -> width } + fadeIn() with slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            slideInHorizontally { width -> -width } + fadeIn() with slideOutHorizontally { width -> width } + fadeOut()
                        }.using(
                            SizeTransform(clip = false)
                        )
                    }, modifier = Modifier.padding(bottom = LocalTheme.spaces.large)
                ) { currentSelected ->
                    when (currentSelected) {
                        0 -> HourlyWeather(
                            weatherUnitData = weatherViewData?.units,
                            weatherData = weatherViewData?.hourly,
                        )

                        1 -> DailyWeather(
                            weatherUnitData = weatherViewData?.units,
                            weatherData = weatherViewData?.daily,
                        )

                        else -> WeatherDescription(
                            weatherData = weatherViewData,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun getColorPalette(color: Color): Palette {
    val color by rememberUpdatedState(color)
    val bitmap by remember {
        derivedStateOf {
            val bmp = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            val canvas = Canvas(bmp);
            canvas.drawColor(color.toArgb())
            bmp
        }
    }
    val palette by remember {
        derivedStateOf {
            Palette.from(bitmap).generate()
        }
    }
    return palette
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherParamRow(
    weatherData: WeatherData?,
    weatherUnitData: WeatherUnitData?,
    maxInRow: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.large,
            alignment = Alignment.CenterHorizontally,
        ),
        maxItemsInEachRow = maxInRow,
    ) {
        WeatherParam(
            value = if (weatherData != null) "${weatherData.windSpeed} ${
                stringResource(weatherUnitData!!.speed)
            }" else stringResource(R.string.placeholder_wind),
            image = Icons.Outlined.Air,
        )
        WeatherParam(
            value = if (weatherData != null) "${weatherData.clouds} %"
            else stringResource(R.string.placeholder_cloud),
            image = Icons.Outlined.Cloud,
        )
        WeatherParam(
            value = if (weatherData != null) "${weatherData.humidity} %"
            else stringResource(R.string.placeholder_humidity),
            image = Icons.Outlined.WaterDrop,
        )
        WeatherParam(
            value = if (weatherData != null) "${weatherData.pressure} ${
                stringResource(weatherUnitData!!.pressure)
            }" else stringResource(R.string.placeholder_pressure),
            image = Icons.Outlined.Compress,
        )
    }
}

@Composable
fun WeatherParam(
    value: String,
    image: ImageVector,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.small,
            alignment = Alignment.CenterHorizontally,
        )
    ) {
        Icon(
            imageVector = image, contentDescription = null
        )
        Text(
            text = value,
            style = LocalTheme.typography.label,
        )
    }
}

@Composable
fun WeatherDescription(
    weatherData: WeatherViewData?
) {
    val dayFormatter = rememberLocalizedDateTimeFormatter("EEEE")

    val now = LocalDateTime.now()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(horizontal = LocalTheme.spaces.large).background(
            color = Color.White.copy(alpha = 0.4f), shape = LocalTheme.shapes.mediumRoundedCornerShape
        ).padding(
            vertical = LocalTheme.spaces.medium,
            horizontal = LocalTheme.spaces.large,
        ),
    ) {
        Text(
            text = weatherData?.let { dayFormatter.format(now) } ?: stringResource(R.string.placeholder_day),
            style = LocalTheme.typography.label,
        )
        Text(
            text = weatherData?.let { getCurrentTimeText(it.timezone) } ?: stringResource(R.string.placeholder_time),
            style = LocalTheme.typography.label,
        )
        WeatherIcon(
            icon = weatherData?.now?.weatherState?.iconUrlX2
        )
        TemperatureView(
            temp = weatherData?.now?.tempObj,
            units = weatherData?.units,
        )
        Text(
            text = weatherData?.now?.weatherState?.description ?: stringResource(R.string.placeholder_description),
            style = LocalTheme.typography.bodyBold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun getCurrentDate(timeZone: TimeZone): String {
    val dateFormatter = rememberLocalizedDateTimeFormatter("yyyy-MM-dd, EEEE")
    val now by rememberUpdatedState(LocalDate.now(timeZone.toZoneId()))
    val formatted by remember { derivedStateOf { dateFormatter.format(now) } }
    return formatted
}

@Composable
fun getCurrentTimeText(timeZone: TimeZone): String {
    val timeFormatter = rememberLocalizedDateTimeFormatter("HH:mm:ss a")
    val lifecycleOwner = LocalLifecycleOwner.current
    var now by remember { mutableStateOf(ZonedDateTime.now(timeZone.toZoneId())) }
    LaunchedEffect(key1 = timeZone) {
        lifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(1000)
                now = ZonedDateTime.now(timeZone.toZoneId())
            }
        }
    }
    val formatted by remember { derivedStateOf { timeFormatter.format(now) } }
    return formatted
}

@Composable
fun HourlyWeather(
    weatherData: SortedMap<LocalTime, WeatherData>?,
    weatherUnitData: WeatherUnitData?,
) {
    val timeFormatter = rememberLocalizedDateTimeFormatter("HH:mm a")
    val items = remember(key1 = weatherData) { weatherData?.entries?.toList() }
    LazyRow(
        contentPadding = PaddingValues(horizontal = LocalTheme.spaces.large),
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.large,
            alignment = Alignment.Start,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {

        items(count = items?.size ?: 7) { index ->
            val entry = items?.getOrNull(index)
            WeatherSummery(
                showParams = true,
                label = entry?.key?.let(timeFormatter::format) ?: stringResource(R.string.placeholder_day),
                weatherData = entry?.value,
                weatherUnitData = weatherUnitData,
            )
        }
    }
}

@Composable
fun DailyWeather(
    weatherData: SortedMap<LocalDate, WeatherData>?,
    weatherUnitData: WeatherUnitData?,
) {
    val dayFormatter = rememberLocalizedDateTimeFormatter("EEEE")
    val items = remember(key1 = weatherData) { weatherData?.entries?.toList() }
    LazyRow(
        contentPadding = PaddingValues(horizontal = LocalTheme.spaces.large),
        horizontalArrangement = Arrangement.spacedBy(
            space = LocalTheme.spaces.large,
            alignment = Alignment.Start,
        ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(count = items?.size ?: 7) { index ->
            val entry = items?.getOrNull(index)
            WeatherSummery(
                showParams = true,
                label = entry?.key?.let(dayFormatter::format) ?: stringResource(R.string.placeholder_day),
                weatherData = entry?.value,
                weatherUnitData = weatherUnitData,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeatherSummery(
    showParams: Boolean,
    label: String,
    weatherData: WeatherData?,
    weatherUnitData: WeatherUnitData?,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.background(
            color = Color.White.copy(alpha = 0.4f), shape = LocalTheme.shapes.mediumRoundedCornerShape
        ).padding(
            vertical = LocalTheme.spaces.medium,
            horizontal = LocalTheme.spaces.large,
        ),
    ) {
        Text(
            text = label,
            style = LocalTheme.typography.label,
        )
        WeatherIcon(
            icon = weatherData?.weatherState?.iconUrlX2
        )
        TemperatureView(
            temp = weatherData?.tempObj,
            units = weatherUnitData,
        )
        if (showParams) {
            Spacer(modifier = Modifier.height(LocalTheme.spaces.medium))
            WeatherParamRow(
                weatherData = weatherData,
                weatherUnitData = weatherUnitData,
                maxInRow = 2,
            )
        }
    }
}

@Composable
fun TemperatureView(
    temp: Temperature?,
    units: WeatherUnitData?
) {
    when (temp) {
        is Temperature.Average -> {
            Text(
                text = "${temp.temp}°",
                style = LocalTheme.typography.title,
            )
        }

        is Temperature.DaySummery -> {
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    space = LocalTheme.spaces.medium,
                    alignment = Alignment.CenterHorizontally,
                ),
            ) {
                Text(
                    text = "${temp.day}°",
                    color = Color.Yellow.compositeOver(Color.Red),
                    style = LocalTheme.typography.title,
                )
                Text(
                    text = "${temp.night}°",
                    color = Color.Black,
                    style = LocalTheme.typography.title,
                )
            }
        }

        null -> {
            Text(
                text = stringResource(R.string.placeholder_temperature),
                style = LocalTheme.typography.title,
            )
        }
    }
}

@Composable
fun WeatherIcon(
    icon: String?,
    modifier: Modifier = Modifier,
) {
    if (icon != null) {
        AsyncImage(
            model = icon,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = modifier,
        )
    } else {
        Image(
            painter = painterResource(R.drawable.not_available),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(LocalTheme.spaces.xxLarge).then(modifier),
        )
    }
}
