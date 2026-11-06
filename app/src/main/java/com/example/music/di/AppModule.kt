package com.example.music.di

import android.content.Context
import com.example.music.data.local.PreferencesManager
import com.example.music.data.local.SongCache
import com.example.music.data.local.SongDatabase
import com.example.music.data.remote.AppleMusicCatalogProvider
import com.example.music.data.remote.LyricsDataSource
import com.example.music.data.remote.YoutubeDataSource
import com.example.music.data.repository.MusicRepository
import com.example.music.data.repository.MusicRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        val isDebug = context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0
        loggingInterceptor.level = if (isDebug) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE

        val certificatePinner = CertificatePinner.Builder()
            .add("lrclib.net", "sha256/YOUR_LRCLIB_PIN_1")
            .add("lrclib.net", "sha256/YOUR_LRCLIB_PIN_2")
            .add("api.music.apple.com", "sha256/YOUR_APPLE_PIN_1")
            .add("api.music.apple.com", "sha256/YOUR_APPLE_PIN_2")
            .add("amp-api.music.apple.com", "sha256/YOUR_APPLE_PIN_1")
            .add("amp-api.music.apple.com", "sha256/YOUR_APPLE_PIN_2")
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .certificatePinner(certificatePinner)
            .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideYoutubeDataSource(): YoutubeDataSource {
        return YoutubeDataSource()
    }

    @Provides
    @Singleton
    fun provideAppleMusicCatalogProvider(okHttpClient: OkHttpClient): AppleMusicCatalogProvider {
        return AppleMusicCatalogProvider(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideLyricsDataSource(okHttpClient: OkHttpClient): LyricsDataSource {
        return LyricsDataSource(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideSongDatabase(@ApplicationContext context: Context): SongDatabase {
        return SongDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideSongCache(database: SongDatabase): SongCache {
        return SongCache(database)
    }

    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideMusicRepository(
        youtubeDataSource: YoutubeDataSource,
        appleMusicProvider: AppleMusicCatalogProvider,
        lyricsDataSource: LyricsDataSource,
        cache: SongCache
    ): MusicRepository {
        return MusicRepositoryImpl(youtubeDataSource, appleMusicProvider, lyricsDataSource, cache)
    }
}