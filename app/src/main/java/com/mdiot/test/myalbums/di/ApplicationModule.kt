package com.mdiot.test.myalbums.di

import android.content.Context
import android.os.Build
import androidx.room.Room
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mdiot.test.myalbums.BuildConfig
import com.mdiot.test.myalbums.data.source.DefaultTracksRepository
import com.mdiot.test.myalbums.data.source.TracksDataSource
import com.mdiot.test.myalbums.data.source.TracksRepository
import com.mdiot.test.myalbums.data.source.local.TracksDatabase
import com.mdiot.test.myalbums.data.source.local.TracksLocalDataSource
import com.mdiot.test.myalbums.data.source.remote.TracksApi
import com.mdiot.test.myalbums.data.source.remote.TracksRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */
@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    private val USER_AGENT =
        "myalbums:${BuildConfig.VERSION_CODE}:${BuildConfig.BUILD_TYPE}:${Build.MANUFACTURER}:${Build.VERSION.SDK_INT}"

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class RemoteTracksDataSource

    @Qualifier
    @Retention(AnnotationRetention.RUNTIME)
    annotation class LocalTracksDataSource

    @Singleton
    @RemoteTracksDataSource
    @Provides
    fun provideTracksRemoteDataSource(
        tracksApi: TracksApi,
        ioDispatcher: CoroutineDispatcher
    ): TracksDataSource {
        return TracksRemoteDataSource(
            tracksApi,
            ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideUaHeaderInterceptor() =
        object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("User-Agent", USER_AGENT)
                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }

    @Singleton
    @Provides
    fun provideJacksonConverterFactory(): JacksonConverterFactory {
        val jacksonMapper = ObjectMapper().registerKotlinModule()
        return JacksonConverterFactory.create(jacksonMapper)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        uaInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ) =
        OkHttpClient.Builder()
            .addInterceptor(uaInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun provideTracksApi(
        client: OkHttpClient,
        jacksonConverterFactory: JacksonConverterFactory
    ): TracksApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.ALBUM_ENDPOINT)
            .client(client)
            .addConverterFactory(jacksonConverterFactory)
            .build()

        return retrofit.create(TracksApi::class.java)
    }

    @Singleton
    @LocalTracksDataSource
    @Provides
    fun provideTracksLocalDataSource(
        database: TracksDatabase,
        ioDispatcher: CoroutineDispatcher
    ): TracksDataSource {
        return TracksLocalDataSource(
            database.tracksDao(),
            ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): TracksDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TracksDatabase::class.java,
            "Tracks.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO
}

/**
 * The binding for TracksRepository is on its own module so that we can replace it easily in tests.
 */
@Module
@InstallIn(ApplicationComponent::class)
object TracksRepositoryModule {

    @Singleton
    @Provides
    fun provideTracksRepository(
        @ApplicationModule.RemoteTracksDataSource remoteTracksDataSource: TracksDataSource,
        @ApplicationModule.LocalTracksDataSource localTracksDataSource: TracksDataSource
    ): TracksRepository {
        return DefaultTracksRepository(
            remoteTracksDataSource, localTracksDataSource
        )
    }
}
