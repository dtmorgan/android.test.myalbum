package com.mdiot.test.myalbums.data.source

import androidx.test.filters.MediumTest
import com.mdiot.test.myalbums.MainCoroutineRule
import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.succeeded
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of the in-memory repository with cache.
 */
@ExperimentalCoroutinesApi
@MediumTest
class DefaultTracksRepositoryTest {

    private val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
    private val track2 = Track(4786, 3655, "title2", "url2", "thumbnailUrl2")
    private val track3 = Track(4787, 3656, "title3", "url3", "thumbnailUrl3")

    private val remoteTracks = listOf(track, track2).sortedBy { it.id }
    private val localTracks = listOf(track3).sortedBy { it.id }
    private val newTracks = listOf(track3).sortedBy { it.id }

    private lateinit var tracksRemoteDataSource: FakeDataSource
    private lateinit var tracksLocalDataSource: FakeDataSource

    // Class under test
    private lateinit var tracksRepository: DefaultTracksRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @ExperimentalCoroutinesApi
    @Before
    fun createRepository() {
        tracksRemoteDataSource = FakeDataSource(remoteTracks.toMutableList())
        tracksLocalDataSource = FakeDataSource(localTracks.toMutableList())
        // Get a reference to the class under test
        tracksRepository = DefaultTracksRepository(
            tracksRemoteDataSource, tracksLocalDataSource
        )
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getTracks_emptyRepositoryAndUninitializedCache() = mainCoroutineRule.runBlockingTest {
        // GIVEN - A repository with 2 empty sources
        val emptySource = FakeDataSource()
        val repository = DefaultTracksRepository(
            emptySource, emptySource
        )

        // WHEN - Get tracks result
        val result = repository.getTracks()

        // THEN - No tracks are available
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
    }

    @Test
    fun getTracks_requestsAllTracksFromRemoteDataSource() = mainCoroutineRule.runBlockingTest {
        // GIVEN - A repository to load data, which loads from remote and caches
        val tracks = tracksRepository.getTracks(true) as Result.Success

        // THEN - No tracks are available
        MatcherAssert.assertThat(tracks.data, CoreMatchers.`is`(remoteTracks))
    }

    @Test
    fun getTracks_repositoryCachesAfterFirstApiCall() = mainCoroutineRule.runBlockingTest {
        // GIVEN - A repository to load data, which loads from remote and caches
        val initial = tracksRepository.getTracks() as Result.Success

        // WHEN - Modify tracks from remote
        tracksRemoteDataSource.tracks = newTracks.toMutableList()
        val second = tracksRepository.getTracks() as Result.Success

        // THEN - Initial and second should match because we didn't force a refresh
        MatcherAssert.assertThat(initial.data, CoreMatchers.`is`(second.data))
    }

    @Test
    fun getTracks_WithDirtyCache_tracksAreRetrievedFromRemote() = mainCoroutineRule.runBlockingTest {
        // GIVEN - A repository to load data, which loads from remote and caches
        val tracks = tracksRepository.getTracks() as Result.Success

        // WHEN - Set a different list of tracks in REMOTE
        tracksRemoteDataSource.tracks = newTracks.toMutableList()

        // But if tracks are cached, subsequent calls load from cache
        val cachedTracks = tracksRepository.getTracks() as Result.Success
        MatcherAssert.assertThat(cachedTracks.data, CoreMatchers.`is`(tracks.data))

        // Now force remote loading
        val refreshedTracks = tracksRepository.getTracks(true) as Result.Success

        // THEN - tracks must be the recently updated in REMOTE
        MatcherAssert.assertThat(refreshedTracks.data, CoreMatchers.`is`(newTracks))
    }

    @Test
    fun getTracks_WithRemoteDataSourceUnavailable_tracksAreRetrievedFromLocal() = mainCoroutineRule.runBlockingTest {
        // GIVEN - A repository to load data, which loads from remote and caches
        // with remote data source unavailable
        tracksRemoteDataSource.tracks = null

        // WHEN - Get tracks result
        val result = tracksRepository.getTracks()

        // THEN - The repository fetches from the local source
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        MatcherAssert.assertThat((result as Result.Success).data, CoreMatchers.`is`(localTracks))
    }

    @Test
    fun getTracks_WithBothDataSourcesUnavailable_returnsError() = mainCoroutineRule.runBlockingTest {
        // GIVEN - Both sources unavailable
        tracksRemoteDataSource.tracks = null
        tracksLocalDataSource.tracks = null

        // WHEN - Get tracks result
        val result = tracksRepository.getTracks()

        // THEN - The repository returns an error
        MatcherAssert.assertThat(result, CoreMatchers.instanceOf(Result.Error::class.java))
    }

}