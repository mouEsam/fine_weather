package com.iti.fineweather.features.alerts.services.local

import androidx.room.*
import com.iti.fineweather.features.alerts.entities.UserWeatherAlert
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface WeatherAlertsDAO {
    @Query("SELECT * FROM UserWeatherAlert")
    fun getAll(): Flow<List<UserWeatherAlert>>

    @Query("SELECT * FROM UserWeatherAlert where deleteAt = null")
    fun getAllActive(): Flow<List<UserWeatherAlert>>

    @Query("SELECT * FROM UserWeatherAlert WHERE id == :id LIMIT 1")
    fun getById(id: UUID): Flow<UserWeatherAlert>

    @Insert
    suspend fun insertAll(vararg ingredients: UserWeatherAlert)

    @Insert
    suspend fun insertAll(ingredients: List<UserWeatherAlert>)

    @Update
    suspend fun updateAll(vararg ingredients: UserWeatherAlert)

    @Update
    suspend fun updateAll(ingredients: List<UserWeatherAlert>)

    @Delete
    suspend fun delete(ingredient: UserWeatherAlert)
}