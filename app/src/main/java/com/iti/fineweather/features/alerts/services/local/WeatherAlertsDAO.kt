package com.iti.fineweather.features.alerts.services.local

import androidx.room.*
import com.iti.fineweather.features.alerts.entities.WeatherAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertsDAO {
    @Query("SELECT * FROM WeatherAlert")
    fun getAll(): Flow<List<WeatherAlert>>

    @Query("SELECT * FROM WeatherAlert WHERE id == :id LIMIT 1")
    fun getById(id: String?): Flow<WeatherAlert?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg ingredients: WeatherAlert)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<WeatherAlert>)

    @Delete
    suspend fun delete(ingredient: WeatherAlert)
}