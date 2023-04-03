package com.iti.fineweather.features.bookmarks.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.core.helpers.UiState
import com.iti.fineweather.core.utils.wrap
import com.iti.fineweather.features.bookmarks.entities.PlaceBookmark
import com.iti.fineweather.features.bookmarks.repositories.PlaceBookmarksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaceBookmarksViewModel @Inject constructor(private val bookmarksRepository: PlaceBookmarksRepository): ViewModel() {

    val uiState: StateFlow<UiState<List<PlaceBookmark>>> by lazy {
        bookmarksRepository.placeBookmarks.map { result ->
            when (result) {
                is Resource.Success -> UiState.Loaded(result.data)
                is Resource.Error ->  UiState.Error(result.error)
            }
        }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = UiState.Loading())
    }

    private val _operationState = MutableSharedFlow<UiState<Unit>>()
    val operationState = _operationState.asSharedFlow()

    fun addBookmark(bookmark: PlaceBookmark) {
        viewModelScope.launch {
            _operationState.wrap {
                bookmarksRepository.addBookmark(bookmark)
            }
        }
    }

    fun deleteBookmark(bookmark: PlaceBookmark) {
        viewModelScope.launch {
            _operationState.wrap {
                bookmarksRepository.removeBookmark(bookmark)
            }
        }
    }
}
