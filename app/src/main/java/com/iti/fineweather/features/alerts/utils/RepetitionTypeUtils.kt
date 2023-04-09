package com.iti.fineweather.features.alerts.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.iti.fineweather.R
import com.iti.fineweather.features.alerts.entities.RepetitionType

@Composable
fun RepetitionType.toLocalizedName(): String {
    return when (this) {
        RepetitionType.SINGLE -> stringResource(R.string.alerts_repetition_type_once)
        RepetitionType.DAILY -> stringResource(R.string.alerts_repetition_type_daily)
    }
}
