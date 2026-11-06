package com.example.music.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.remote.SongResult
import com.example.music.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<SongResult>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private var lastQuery = ""

    fun search(query: String) {
        if (query.trim().isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        if (query == lastQuery) return
        lastQuery = query

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.searchSongs(query)
                .onSuccess { _searchResults.value = it }
                .onFailure {
                    _error.value = it.message ?: "Search failed"
                }
            _isLoading.value = false
        }
    }

    fun clearSearch() {
        _searchResults.value = emptyList()
        _error.value = null
        lastQuery = ""
    }
}