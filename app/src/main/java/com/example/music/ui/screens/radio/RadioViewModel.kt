package com.example.music.ui.screens.radio

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
class RadioViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _stations = MutableStateFlow<List<SongResult>>(emptyList())
    val stations = _stations.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadStations()
    }

    fun loadStations() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Placeholder - would use personal stations API in MusicKit
                repository.searchSongs("apple music radio").onSuccess { _stations.value = it }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadStations()
    }
}