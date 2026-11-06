package com.example.music.ui.screens.browse

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
class BrowseViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _newReleases = MutableStateFlow<List<SongResult>>(emptyList())
    val newReleases = _newReleases.asStateFlow()

    private val _charts = MutableStateFlow<List<SongResult>>(emptyList())
    val charts = _charts.asStateFlow()

    private val _genres = MutableStateFlow<List<SongResult>>(emptyList())
    val genres = _genres.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadBrowseData()
    }

    fun loadBrowseData() {
        viewModelScope.launch {
            _isLoading.value = true
            // For now, reuse search with different queries
            // In a real app with MusicKit, these would be dedicated endpoints
            try {
                // These are placeholder - real implementation would use catalog charts/genres
                repository.searchSongs("new releases").onSuccess { _newReleases.value = it }
                repository.searchSongs("top charts").onSuccess { _charts.value = it }
                repository.searchSongs("pop music").onSuccess { _genres.value = it }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadBrowseData()
    }
}