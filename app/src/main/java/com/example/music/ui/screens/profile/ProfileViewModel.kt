package com.example.music.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.music.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    // Profile settings, theme preferences, etc.
}