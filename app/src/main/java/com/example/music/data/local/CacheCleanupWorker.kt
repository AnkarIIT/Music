package com.example.music.data.local

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CacheCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // Get database instance
            val database = SongDatabase.getInstance(applicationContext)
            val cache = SongCache(database)

            // Clean up entries older than 7 days
            cache.clearOldCache(7 * 24 * 60 * 60 * 1000L)

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("CacheCleanupWorker", "Failed to clean up cache", e)
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "cache_cleanup"
        const val WORK_TAG = "cache_cleanup_tag"
    }
}