package com.example.music

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.music.data.local.CacheCleanupWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class MusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        scheduleCacheCleanup()
    }

    private fun scheduleCacheCleanup() {
        val cleanupWork = PeriodicWorkRequest.Builder(CacheCleanupWorker::class.java, 24, TimeUnit.HOURS)
            .addTag(CacheCleanupWorker.WORK_TAG)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CacheCleanupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWork
        )
    }
}