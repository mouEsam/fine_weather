package com.iti.fineweather.features.weather.helpers

import com.iti.fineweather.R
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.weather.models.*
import com.iti.fineweather.features.weather.utils.convert
import com.iti.fineweather.features.weather.utils.unitRes
import java.time.*
import java.util.*
import javax.inject.Inject

class WeatherDataMapper @Inject constructor() {
    fun mapRemoteToView(
        location: WeatherLocation,
        data: RemoteWeatherResponse,
        preferences: UserPreferences
    ): WeatherViewData {
        val timeZone = TimeZone.getTimeZone(data.timezone)
        return WeatherViewData(
            location = WeatherLocation(
                name = location.name,
                city = location.city,
                latitude = data.lat,
                longitude = data.lon,
            ),
            timezone = timeZone,
            units = mapPreferencesToUnits(preferences),
            now = mapWeatherDataToViewData(data.currentWeather, preferences),
            daily = mapDaily(data.dailyWeather, preferences),
            hourly = mapHourly(data.hourlyWeather, timeZone, preferences),
            alerts = mapAlerts(data.alerts, timeZone, preferences),
        )
    }

    private fun mapPreferencesToUnits(preferences: UserPreferences): WeatherUnitData {
        return WeatherUnitData(
            temperature = preferences.temperatureUnit.unitRes,
            pressure = R.string.pressure_hpa,
            speed = preferences.windSpeedUnit.unitRes,
        )
    }

    private fun mapDaily(
        daily: List<RemoteWeatherData>,
        preferences: UserPreferences,
    ): SortedMap<LocalDate, WeatherData> {
        val utc = ZoneOffset.ofTotalSeconds(0)
        val weekStartEpoch = Calendar.getInstance().run {
            time = Date()
            this[Calendar.DAY_OF_WEEK] = 1
            LocalDateTime.ofInstant(toInstant(), timeZone.toZoneId()).toLocalDate()
        }.atStartOfDay().toInstant(utc).epochSecond
        val weekEndEpoch = weekStartEpoch + Duration.ofDays(7).seconds

        val result = TreeMap<LocalDate, WeatherData>()
        daily
            .filter { data -> data.timestamp in weekStartEpoch..weekEndEpoch }
            .sortedBy { data -> data.timestamp }
            .forEach { data ->
                val date = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(data.timestamp),
                    utc
                ).toLocalDate()
                result[date] = mapWeatherDataToViewData(data, preferences)
            }
        return result
    }

    private fun mapHourly(
        daily: List<RemoteWeatherData>,
        timeZone: TimeZone,
        preferences: UserPreferences,
    ): SortedMap<LocalTime, WeatherData> {
        val result = TreeMap<LocalTime, WeatherData>()
        daily
            .sortedBy { data -> data.timestamp }
            .forEach { data ->
                val time = ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(data.timestamp),
                    timeZone.toZoneId()
                ).toLocalTime()
                result[time] = mapWeatherDataToViewData(data, preferences)
            }
        return result
    }

    private fun mapWeatherDataToViewData(
        remoteData: RemoteWeatherData,
        preferences: UserPreferences,
    ): WeatherData {
        val state = remoteData.weather.first().run {
            WeatherState(
                weatherType = WeatherType.values().firstOrNull { type ->
                    type.code == id || type.codeRange?.contains(id) == true
                }!!,
                isDay = icon.contains('d'),
                main = main,
                description = description,
                iconUrl = "https://openweathermap.org/img/wn/${icon}@2x.png",
                iconUrlX2 = "https://openweathermap.org/img/wn/${icon}@4x.png",
                dayIcon = mapIconToRawRes(icon, true),
                nightIcon = mapIconToRawRes(icon, false),
                dayBG = mapIconToBG(icon, true),
                nightBG = mapIconToBG(icon, false),
            )
        }
        val tempObj = preferences.temperatureUnit.convert(remoteData.temp)
        return WeatherData(
            tempObj = tempObj,
            temperature = tempObj.getTemp(state.isDay),
            pressure = remoteData.pressure,
            humidity = remoteData.humidity,
            clouds = remoteData.clouds,
            windSpeed = preferences.windSpeedUnit.convert(remoteData.windSpeed),
            weatherState = state,
        )
    }

    /*
    * 01d.png 	01n.png 	clear sky
    * 02d.png 	02n.png 	few clouds
    * 03d.png 	03n.png 	scattered clouds
    * 04d.png 	04n.png 	broken clouds
    * 09d.png 	09n.png 	shower rain
    * 10d.png 	10n.png 	rain
    * 11d.png 	11n.png 	thunderstorm
    * 13d.png 	13n.png 	snow
    * 50d.png 	50n.png 	mist
    * */

    private fun mapIconToRawRes(icon: String, getDay: Boolean): Int {
        return when (icon.take(2)) {
            "01" -> if (getDay) R.raw.weather_01d else R.raw.weather_01n
            "02" -> if (getDay) R.raw.weather_02d else R.raw.weather_02n
            "03" -> if (getDay) R.raw.weather_03d else R.raw.weather_03n
            "04" -> if (getDay) R.raw.weather_04d else R.raw.weather_04n
            "09" -> if (getDay) R.raw.weather_09d else R.raw.weather_09n
            "10" -> if (getDay) R.raw.weather_10d else R.raw.weather_10n
            "11" -> if (getDay) R.raw.weather_11d else R.raw.weather_11n
            "13" -> if (getDay) R.raw.weather_13d else R.raw.weather_13n
            "50" -> if (getDay) R.raw.weather_50d else R.raw.weather_50n
            else -> if (getDay) R.raw.weather_01d else R.raw.weather_01n
        }
    }

    private fun mapIconToBG(icon: String, getDay: Boolean): Int {
        return when (icon.take(2)) {
            "01" -> if (getDay) R.mipmap.morning else R.mipmap.morning_alt
            "02" -> if (getDay) R.mipmap.sunset else R.mipmap.sunrise_alt
            "03" -> if (getDay) R.mipmap.sunset_alt else R.mipmap.sunrise
            "04" -> if (getDay) R.mipmap.sunset_alt_2 else R.mipmap.sunset_dark
            "09" -> if (getDay) R.mipmap.wind else R.mipmap.wind_alt
            "10" -> if (getDay) R.mipmap.rain else R.mipmap.rain
            "11" -> if (getDay) R.mipmap.thunderstorm else R.mipmap.thunder
            "13" -> if (getDay) R.mipmap.snowy else R.mipmap.snowy
            "50" -> if (getDay) R.mipmap.fog else R.mipmap.fog
            else -> if (getDay) R.mipmap.idle else R.mipmap.normal
        }
    }

    private fun mapAlerts(
        alerts: List<WeatherAlert>?,
        timeZone: TimeZone,
        preferences: UserPreferences
    ): List<WeatherAlertView> {
        return alerts?.map { mapAlert(it, timeZone, preferences) } ?: listOf()
    }

    private fun mapAlert(alert: WeatherAlert, timeZone: TimeZone, preferences: UserPreferences): WeatherAlertView {
        val start = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(alert.start),
            timeZone.toZoneId()
        )
        val end = ZonedDateTime.ofInstant(
            Instant.ofEpochSecond(alert.end),
            timeZone.toZoneId()
        )
        return WeatherAlertView(
            senderName = alert.senderName,
            event = alert.event,
            start = start,
            end = end,
            description = alert.description,
            tags = alert.tags,
        )
    }
}