package com.mdiot.test.myalbums.di

import com.mdiot.test.myalbums.data.source.FakeRepository
import com.mdiot.test.myalbums.data.source.TracksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * TracksRepository binding to use in tests.
 *
 * Hilt will inject a [FakeRepository] instead of a [DefaultTracksRepository].
 */
@Module
@InstallIn(ApplicationComponent::class)
abstract class TestTracksRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRepository(repo: FakeRepository): TracksRepository
}
