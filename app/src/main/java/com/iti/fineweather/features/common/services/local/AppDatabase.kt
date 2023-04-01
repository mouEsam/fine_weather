package com.iti.fineweather.features.common.services.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import com.iti.fineweather.features.alerts.helpers.convertors.RepetitionTypeConvertor
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsDAO
import com.iti.fineweather.features.common.helpers.convertors.DateConvertor
import com.iti.fineweather.features.common.helpers.convertors.LocalDateConvertor
import com.iti.fineweather.features.common.helpers.convertors.LocalDateTimeConvertor
import com.iti.fineweather.features.common.helpers.convertors.LocalTimeConvertor

@Database(entities = [
    UserWeatherAlert::class,
], exportSchema = false, version = 1)
@TypeConverters(value = [
    LocalDateConvertor::class,
    LocalTimeConvertor::class,
    LocalDateTimeConvertor::class,
    DateConvertor::class,
    RepetitionTypeConvertor::class,
])
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherAlertsDao(): WeatherAlertsDAO

}