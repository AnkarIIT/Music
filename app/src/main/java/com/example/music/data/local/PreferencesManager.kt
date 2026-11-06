package com.example.music.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {

    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            PREFS_NAME,
            masterKeyAlias,
            context.applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // Flow-backed properties for reactive UI
    private val _lastPlayedSongId = MutableStateFlow<String?>(encryptedPrefs.getString("last_played_song_id", null))
    val lastPlayedSongId: StateFlow<String?> = _lastPlayedSongId

    private val _lastPlayedPosition = MutableStateFlow<Long>(encryptedPrefs.getLong("last_played_position", 0L))
    val lastPlayedPosition: StateFlow<Long> = _lastPlayedPosition

    private val _repeatMode = MutableStateFlow<Int>(encryptedPrefs.getInt("repeat_mode", 0))
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _shuffleEnabled = MutableStateFlow<Boolean>(encryptedPrefs.getBoolean("shuffle_enabled", false))
    val shuffleEnabled: StateFlow<Boolean> = _shuffleEnabled

    private val _volumeLevel = MutableStateFlow<Float>(encryptedPrefs.getFloat("volume_level", 1.0f))
    val volumeLevel: StateFlow<Float> = _volumeLevel

    // Lyrics search consent (GDPR/privacy)
    private val _lyricsConsent = MutableStateFlow<Boolean>(encryptedPrefs.getBoolean("lyrics_consent", false))
    val lyricsConsent: StateFlow<Boolean> = _lyricsConsent

    suspend fun setLyricsConsent(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean("lyrics_consent", enabled).apply()
        _lyricsConsent.value = enabled
    }

    suspend fun setLastPlayedSong(songId: String) {
        encryptedPrefs.edit().putString("last_played_song_id", songId).apply()
        _lastPlayedSongId.value = songId
    }

    suspend fun setLastPlayedPosition(position: Long) {
        encryptedPrefs.edit().putLong("last_played_position", position).apply()
        _lastPlayedPosition.value = position
    }

    suspend fun setRepeatMode(mode: Int) {
        encryptedPrefs.edit().putInt("repeat_mode", mode).apply()
        _repeatMode.value = mode
    }

    suspend fun setShuffleEnabled(enabled: Boolean) {
        encryptedPrefs.edit().putBoolean("shuffle_enabled", enabled).apply()
        _shuffleEnabled.value = enabled
    }

    suspend fun setVolumeLevel(volume: Float) {
        encryptedPrefs.edit().putFloat("volume_level", volume).apply()
        _volumeLevel.value = volume
    }

    companion object {
        private const val PREFS_NAME = "music_prefs_encrypted"
    }
}