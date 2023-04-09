package com.iti.fineweather.features.alerts.entities

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iti.fineweather.R
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

enum class RepetitionType {
    SINGLE,
    DAILY
}

@Parcelize
@Entity
data class UserWeatherAlert(
    @PrimaryKey
    val id: UUID = UUID.randomUUID(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val deletedAt: LocalDateTime? = null,
    val alarmEnabled: Boolean,
    @ColumnInfo(name = "repetition_type")
    val repetitionType: RepetitionType,
    @ColumnInfo(name = "time")
    val time: LocalTime,
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    val endDate: LocalDate? = null,
    val exhausted: Boolean = false,
) : Parcelable
