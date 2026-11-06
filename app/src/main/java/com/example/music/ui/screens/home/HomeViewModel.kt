package com.example.music.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.local.SongEntity
import com.example.music.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _recentSongs = MutableStateFlow<List<SongEntity>>(emptyList())
    val recentSongs = _recentSongs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadRecentSongs()
    }

    fun loadRecentSongs() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _recentSongs.value = repository.getRecentSongs(20)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadRecentSongs()
    }
}