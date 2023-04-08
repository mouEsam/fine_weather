package com.iti.fineweather.features.alerts.views

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.iti.fineweather.R
import com.iti.fineweather.core.helpers.LocalScaffold
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.theme.LocalTheme
import com.iti.fineweather.core.utils.error
import com.iti.fineweather.features.alerts.entities.RepetitionType
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.models.WeatherAlertTemplate
import com.iti.fineweather.features.alerts.viewmodels.NewWeatherAlertViewModel
import com.iti.fineweather.features.alerts.viewmodels.WeatherAlertsViewModel
import com.iti.fineweather.features.common.utils.horizontalGradientDirectional
import com.iti.fineweather.features.common.utils.rememberLocalizedDateTimeFormatter
import com.iti.fineweather.features.common.views.AppRadioButton
import com.iti.fineweather.features.common.views.ManualActionLock
import com.iti.fineweather.features.common.views.showErrorSnackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.*


@Composable
fun AlertsPage(
    modifier: Modifier = Modifier,
    alertsViewModel: WeatherAlertsViewModel = hiltViewModel(),
    newWeatherAlertViewModel: NewWeatherAlertViewModel = hiltViewModel(),
) {
    val uiState by alertsViewModel.uiState.collectAsState()
    showErrorSnackbar(uiState)
    newAlertContent(alertsViewModel)

    AlertsContent(
        modifier = modifier,
        alertsViewModel = alertsViewModel,
        newWeatherAlertViewModel = newWeatherAlertViewModel,
        alertsState = uiState,
    )
}

@Composable
fun newAlertContent(
    alertsViewModel: WeatherAlertsViewModel,
) {
    val uiState by alertsViewModel.operationState.collectAsState(UiState.Initial())
    showErrorSnackbar(uiState)
}

@Composable
fun AlertsContent(
    modifier: Modifier = Modifier,
    alertsViewModel: WeatherAlertsViewModel,
    newWeatherAlertViewModel: NewWeatherAlertViewModel,
    alertsState: UiState<List<UserWeatherAlert>> = UiState.Initial(),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (alertsState) {
            is UiState.Loaded -> {
                AlertsList(
                    alerts = alertsState.data,
                    alertsViewModel = alertsViewModel,
                    newWeatherAlertViewModel = newWeatherAlertViewModel,
                )
            }

            is UiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )

            else -> NoAlerts(
                newWeatherAlertViewModel = newWeatherAlertViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlertsList(
    alerts: List<UserWeatherAlert>,
    alertsViewModel: WeatherAlertsViewModel,
    newWeatherAlertViewModel: NewWeatherAlertViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val newAlertState by newWeatherAlertViewModel.alert.collectAsState()
    val showEmptyView = newAlertState == null && alerts.isEmpty()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (showEmptyView) {
            NoAlerts(
                newWeatherAlertViewModel = newWeatherAlertViewModel,
            )
        } else {
            val dateFormatter = rememberLocalizedDateTimeFormatter("yyyy-MM-dd")
            val timeFormatter = rememberLocalizedDateTimeFormatter("hh:mm a")
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
                if (newAlertState == null) {
                    item {
                        ElevatedButton(
                            onClick = {
                                newWeatherAlertViewModel.newAlert()
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.alerts_add_alert),
                                style = LocalTheme.typography.action,
                                color = LocalTheme.colors.main,
                            )
                        }
                    }
                }
                item {
                    newAlertState?.let {
                        NewAlertForm(
                            newWeatherAlertViewModel = newWeatherAlertViewModel,
                            newAlertTemplate = it
                        )
                    }
                }
                items(alerts, UserWeatherAlert::id) { item ->
                    val alert by rememberUpdatedState(item)
                    var showDeleteAlert by remember { mutableStateOf(false) }
                    val dismissState = rememberDismissState(
                        confirmValueChange = {
                            showDeleteAlert = true
                            true
                        }
                    )
                    if (showDeleteAlert) {
                        AlertDialog(
                            onDismissRequest = {},
                            title = {
                                Text(
                                    text = stringResource(R.string.alerts_delete_prompt_title),
                                    color = LocalTheme.colors.main,
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.alerts_delete_prompt_message),
                                    color = LocalTheme.colors.main,
                                )
                            },
                            dismissButton = {
                                ElevatedButton(
                                    onClick = {
                                        showDeleteAlert = false
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
                                        showDeleteAlert = false
                                        alertsViewModel.deleteAlert(alert)
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
                                modifier = Modifier.fillMaxWidth().background(
                                    brush = Brush.horizontalGradientDirectional(
                                        listOf(
                                            Color.Unspecified,
                                            LocalTheme.colors.mainContent,
                                        ),
                                        startX = -100f,
                                    ),
                                    shape = LocalTheme.shapes.largeRoundedCornerShape
                                ).padding(
                                    vertical = LocalTheme.spaces.medium,
                                    horizontal = LocalTheme.spaces.large,
                                ),
                            ) {
                                Configuration(
                                    label = stringResource(R.string.alerts_alert_type),
                                ) {
                                    ConfigurationValue(
                                        label = stringResource(
                                            if (alert.alarmEnabled)
                                                R.string.alerts_alert_type_alarm
                                            else
                                                R.string.alerts_alert_type_notification
                                        ),
                                    )
                                }
                                Configuration(
                                    label = stringResource(R.string.alerts_repetition_type),
                                ) {
                                    ConfigurationValue(
                                        label = alert.repetitionType.toLocalizedName(),
                                    )
                                }
                                Configuration(
                                    label = stringResource(R.string.alerts_start_date),
                                ) {
                                    ConfigurationValue(
                                        label = dateFormatter.format(alert.startDate),
                                    )
                                }
                                Configuration(
                                    label = stringResource(R.string.alerts_time),
                                ) {
                                    ConfigurationValue(
                                        label = timeFormatter.format(alert.time),
                                    )
                                }
                                if (alert.repetitionType == RepetitionType.DAILY) {
                                    alert.endDate?.let { endDate ->
                                        Configuration(
                                            label = stringResource(R.string.alerts_end_date),
                                        ) {
                                            ConfigurationValue(
                                                label = dateFormatter.format(endDate),
                                            )
                                        }
                                    }
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

@OptIn(ExperimentalPermissionsApi::class, ExperimentalLayoutApi::class)
@Composable
fun NewAlertForm(
    newWeatherAlertViewModel: NewWeatherAlertViewModel,
    newAlertTemplate: WeatherAlertTemplate
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarState = LocalScaffold.snackbarHost
    val noNotificationsError = stringResource(R.string.error_notifications_permission)
    val noOverlayError = stringResource(R.string.error_overlay_permission)

    fun onAlarmGranted() {
        newWeatherAlertViewModel.submit()
    }

    fun onNotificationGranted() {
        newWeatherAlertViewModel.submit()
    }

    val alarmPermission = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(context)) {
            onAlarmGranted()
        } else {
            coroutineScope.launch {
                snackbarState.showSnackbar(noOverlayError)
            }
        }
    }

    fun requestAlarmPermission() {
        if (!Settings.canDrawOverlays(context)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
            alarmPermission.launch(intent)
        } else {
            onAlarmGranted()
        }
    }

    val notificationPermissions = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS) { granted ->
        if (granted) {
            onNotificationGranted()
        } else {
            coroutineScope.launch {
                snackbarState.showSnackbar(noNotificationsError)
            }
        }
    }

    fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissions.launchPermissionRequest()
        } else {
            onNotificationGranted()
        }
    }

    val dateFormatter = rememberLocalizedDateTimeFormatter("yyyy-MM-dd")
    val timeFormatter = rememberLocalizedDateTimeFormatter("hh:mm a")

    ManualActionLock { lock ->
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(
                space = LocalTheme.spaces.small,
                alignment = Alignment.Top,
            ),
            modifier = Modifier.fillMaxWidth().background(
                color = Color.White.copy(alpha = 0.4f),
                shape = LocalTheme.shapes.largeRoundedCornerShape
            ).padding(
                vertical = LocalTheme.spaces.medium,
                horizontal = LocalTheme.spaces.large,
            ),
        ) {
            Configuration(
                label = stringResource(R.string.alerts_alert_type),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = LocalTheme.spaces.medium,
                        alignment = Alignment.Start,
                    )
                ) {
                    listOf(true, false).forEach { isAlarm ->
                        AppRadioButton(
                            stringResource(
                                if (isAlarm)
                                    R.string.alerts_alert_type_alarm
                                else
                                    R.string.alerts_alert_type_notification
                            ),
                            selected = newAlertTemplate.alarmEnabled == isAlarm,
                            onSelected = {
                                newWeatherAlertViewModel.updateAlarmEnabled(isAlarm)
                            }
                        )
                    }
                }
            }
            Configuration(
                label = stringResource(R.string.alerts_repetition_type),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        space = LocalTheme.spaces.medium,
                        alignment = Alignment.Start,
                    )
                ) {
                    RepetitionType.values().forEach { type ->
                        AppRadioButton(
                            type.toLocalizedName(),
                            selected = newAlertTemplate.repetitionType == type,
                            onSelected = {
                                newWeatherAlertViewModel.updateRepetitionType(type)
                            }
                        )
                    }
                }
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(
                    space = LocalTheme.spaces.medium,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalArrangement = Arrangement.Center,
            ) {
                Box {
                    var showDatePicker by rememberSaveable { mutableStateOf(false) }
                    if (showDatePicker) {
                        AlertDatePickerDialog(
                            dismiss = { showDatePicker = false },
                            onDate = newWeatherAlertViewModel::updateStartDate,
                        )
                    }
                    ClickableConfiguration(
                        onClick = {
                            showDatePicker = true
                        }
                    ) {
                        VerticalConfiguration(
                            label = stringResource(R.string.alerts_start_date)
                        ) {
                            ConfigurationValue(
                                label = newAlertTemplate.startDate?.let(dateFormatter::format)
                                    ?: stringResource(R.string.alerts_alert_unspecified),
                            )
                        }
                    }
                }
                Box {
                    var showTimePicker by rememberSaveable { mutableStateOf(false) }
                    if (showTimePicker) {
                        AlertTimePickerDialog(
                            dismiss = { showTimePicker = false },
                            onTime = newWeatherAlertViewModel::updateTime,
                        )
                    }
                    ClickableConfiguration(
                        onClick = {
                            showTimePicker = true
                        }
                    ) {
                        VerticalConfiguration(
                            label = stringResource(R.string.alerts_time)
                        ) {
                            ConfigurationValue(
                                label = newAlertTemplate.time?.let(timeFormatter::format)
                                    ?: stringResource(R.string.alerts_alert_unspecified),
                            )
                        }
                    }
                }
                if (newAlertTemplate.repetitionType == RepetitionType.DAILY) {
                    Box {
                        var showDatePicker by rememberSaveable { mutableStateOf(false) }
                        if (showDatePicker) {
                            AlertDatePickerDialog(
                                dismiss = { showDatePicker = false },
                                onDate = newWeatherAlertViewModel::updateEndDate,
                            )
                        }
                        ClickableConfiguration(
                            onClick = {
                                showDatePicker = true
                            }
                        ) {
                            VerticalConfiguration(
                                label = stringResource(R.string.alerts_end_date)
                            ) {
                                ConfigurationValue(
                                    label = newAlertTemplate.endDate?.let(dateFormatter::format)
                                        ?: stringResource(R.string.alerts_alert_unspecified),
                                )
                            }
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                val opState by newWeatherAlertViewModel.operationState.collectAsState(UiState.Initial())
                val error = opState.error
                if (error != null) {
                    Text(
                        text = error.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1.0f),
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1.0f))
                }
                ElevatedButton(
                    onClick = {
                        if (newWeatherAlertViewModel.validate()) {
                            if (newAlertTemplate.alarmEnabled == true) {
                                requestAlarmPermission()
                            } else {
                                requestNotificationPermission()
                            }
                        }
                    }
                ) {
                    LaunchedEffect(key1 = opState) {
                        if (opState is UiState.Loading) {
                            lock.lock()
                        } else {
                            lock.unLock()
                        }
                    }
                    if (lock.isLocked) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = stringResource(R.string.alerts_add_submit),
                            color = LocalTheme.colors.main,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDatePickerDialog(
    dismiss: () -> Unit,
    onDate: (LocalDate) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        modifier = Modifier.padding(LocalTheme.spaces.large),
        confirmButton = {
            Button(onClick = {
                dismiss()
                datePickerState.selectedDateMillis?.let { timestamp ->
                    val date = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault(),
                    ).toLocalDate()
                    onDate(date)
                }
            }) {
                Text(
                    text = stringResource(android.R.string.ok),
                    color = LocalTheme.colors.mainContent,
                )
            }
        },
        onDismissRequest = dismiss,
    ) {
        val today = remember { LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() }
        DatePicker(
            state = datePickerState,
            dateValidator = { date -> date >= today }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertTimePickerDialog(
    dismiss: () -> Unit,
    onTime: (LocalTime) -> Unit,
) {
    val timePickerState = rememberTimePickerState()
    DatePickerDialog(
        confirmButton = {
            Button(onClick = {
                dismiss()
                val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                onTime(time)
            }) {
                Text(
                    text = stringResource(android.R.string.ok)
                )
            }
        },
        onDismissRequest = dismiss,
    ) {
        TimePicker(
            state = timePickerState,
            modifier = Modifier.padding(LocalTheme.spaces.large),
        )
    }
}

@Composable
private fun Configuration(
    label: String,
    expand: Boolean = true,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = LocalTheme.typography.body,
            modifier = if (expand) Modifier.weight(1.0f) else Modifier,
        )
        content()
    }
}

@Composable
private fun VerticalConfiguration(
    label: String,
    content: @Composable () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LocalTheme.spaces.medium)
    ) {
        Text(
            text = label,
            color = LocalTheme.colors.main,
        )
        content()
    }
}


@Composable
private fun ClickableConfiguration(
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    ElevatedButton(
        shape = LocalTheme.shapes.mediumRoundedCornerShape,
        contentPadding = PaddingValues(LocalTheme.spaces.medium),
        onClick = onClick,
    ) {
        content()
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
fun NoAlerts(
    newWeatherAlertViewModel: NewWeatherAlertViewModel,
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
            text = stringResource(R.string.alerts_empty),
            style = LocalTheme.typography.action,
            textAlign = TextAlign.Center,
        )
        ElevatedButton(
            onClick = {
                newWeatherAlertViewModel.newAlert()
            }
        ) {
            Text(
                text = stringResource(R.string.alerts_add_alert),
                style = LocalTheme.typography.action,
                color = LocalTheme.colors.main,
            )
        }
    }
}
