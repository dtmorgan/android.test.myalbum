package com.mdiot.test.myalbums.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mdiot.test.myalbums.MainCoroutineRule
import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.succeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.*
import org.junit.runner.RunWith

/**
 * Integration test for the [TracksDataSource].
 */
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TracksLocalDataSourceTest {

    private lateinit var localDataSource: TracksLocalDataSource
    private lateinit var database: TracksDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each tracks synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // using an in-memory database for testing, since it doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TracksDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource = TracksLocalDataSource(database.tracksDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun saveTrack_retrievesTrack() = runBlockingTest {
        // GIVEN - a new tracks saved in the database
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        localDataSource.saveTrack(track)

        // WHEN - Get tracks result
        val result = localDataSource.getTracks()

        // THEN - The track can be retrieved from the persistent repository and is complete
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        val tracks = result.data
        MatcherAssert.assertThat(tracks[0].id, CoreMatchers.`is`(track.id))
        MatcherAssert.assertThat(tracks[0].albumId, CoreMatchers.`is`(track.albumId))
        MatcherAssert.assertThat(tracks[0].title, CoreMatchers.`is`(track.title))
        MatcherAssert.assertThat(tracks[0].url, CoreMatchers.`is`(track.url))
        MatcherAssert.assertThat(tracks[0].thumbnailUrl, CoreMatchers.`is`(track.thumbnailUrl))
    }

    @Test
    fun getTracks_retrieveSavedTracks() = runBlockingTest {
        // GIVEN - 2 new tracks in the persistent repository
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        val track2 = Track(4786, 3655, "title2", "url2", "thumbnailUrl2")

        localDataSource.saveTrack(track)
        localDataSource.saveTrack(track2)

        // WHEN - Get tracks result
        val result = localDataSource.getTracks()

        // THEN - The track can be retrieved from the persistent repository
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        val tracks = result.data
        Assert.assertThat(tracks.size, CoreMatchers.`is`(2))
    }

    @Test
    fun deleteAllTasks_emptyListOfRetrievedTask() = runBlockingTest {
        // Given a new track in the persistent repository
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        localDataSource.saveTrack(track)

        // When all tracks are deleted
        localDataSource.deleteAllTracks()

        // Then the retrieved tracks is an empty list
        val result = localDataSource.getTracks()
        MatcherAssert.assertThat(result.succeeded, CoreMatchers.`is`(true))
        result as Result.Success
        val tracks = result.data
        Assert.assertThat(tracks.isEmpty(), CoreMatchers.`is`(true))
    }

}