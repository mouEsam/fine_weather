package com.iti.fineweather.features.alerts.models

import com.iti.fineweather.features.alerts.entities.RepetitionType
import java.time.LocalDate
import java.time.LocalTime


data class WeatherAlertTemplate(
    val alarmEnabled: Boolean? = null,
    val repetitionType: RepetitionType? = null,
    val time: LocalTime? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
)
