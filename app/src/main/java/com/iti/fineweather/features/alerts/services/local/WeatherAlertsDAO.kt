package com.iti.fineweather.features.alerts.services.local

import androidx.room.*
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface WeatherAlertsDAO {
    @Query("SELECT * FROM UserWeatherAlert")
    fun getAll(): Flow<List<UserWeatherAlert>>

    @Query("SELECT * FROM UserWeatherAlert where deletedAt IS NULL AND exhausted == 0")
    fun getAllActive(): Flow<List<UserWeatherAlert>>

    @Query("SELECT * FROM UserWeatherAlert WHERE id == :id LIMIT 1")
    fun getById(id: UUID): Flow<UserWeatherAlert>

    @Insert
    suspend fun insertAll(vararg alerts: UserWeatherAlert)

    @Insert
    suspend fun insertAll(alerts: List<UserWeatherAlert>)

    @Update
    suspend fun updateAll(vararg alerts: UserWeatherAlert)

    @Update
    suspend fun updateAll(alerts: List<UserWeatherAlert>)

    @Delete
    suspend fun delete(alerts: UserWeatherAlert)
}