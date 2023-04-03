package com.iti.fineweather.features.bookmarks.services.local

import androidx.room.*
import com.iti.fineweather.features.bookmarks.entities.PlaceBookmark
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface PlaceBookmarksDAO {
    @Query("SELECT * FROM PlaceBookmark")
    fun getAll(): Flow<List<PlaceBookmark>>

    @Query("SELECT * FROM PlaceBookmark where deletedAt IS NULL")
    fun getAllActive(): Flow<List<PlaceBookmark>>

    @Query("SELECT * FROM PlaceBookmark WHERE id == :id LIMIT 1")
    fun getById(id: UUID): Flow<PlaceBookmark>

    @Insert
    suspend fun insertAll(vararg bookmark: PlaceBookmark)

    @Insert
    suspend fun insertAll(bookmark: List<PlaceBookmark>)

    @Update
    suspend fun updateAll(vararg bookmark: PlaceBookmark)

    @Update
    suspend fun updateAll(bookmark: List<PlaceBookmark>)

    @Delete
    suspend fun delete(bookmark: PlaceBookmark)
}