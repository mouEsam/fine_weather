package com.iti.fineweather.features.bookmarks.repositories

import com.iti.fineweather.core.di.IODispatcher
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.bookmarks.entities.PlaceBookmark
import com.iti.fineweather.features.bookmarks.services.local.PlaceBookmarksDAO
import com.iti.fineweather.features.settings.models.UserPreferences.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceBookmarksRepository @Inject constructor(
    private val placeBookmarksDAO: PlaceBookmarksDAO,
    @IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    val placeBookmarks: Flow<Resource<List<PlaceBookmark>>> = placeBookmarksDAO.getAllActive()
        .map<_, Resource<List<PlaceBookmark>>> { bookmarks ->
            Resource.Success.Local(bookmarks)
        }.catch { exception ->
            when (exception) {
                is Exception -> emit(Resource.Error(exception))
                else -> throw exception
            }
        }.flowOn(dispatcher)

    suspend fun addBookmark(bookmark: PlaceBookmark) {
        withContext(dispatcher) {
            placeBookmarksDAO.insertAll(bookmark)
        }
    }

    suspend fun removeBookmark(bookmark: PlaceBookmark) {
        withContext(dispatcher) {
            if (bookmark.deletedAt != null) {
                throw Exception("Can't delete an already deleted alarm") // TODO: localize
            }
            placeBookmarksDAO.updateAll(bookmark.copy(deletedAt = LocalDateTime.now()))
        }
    }

}
