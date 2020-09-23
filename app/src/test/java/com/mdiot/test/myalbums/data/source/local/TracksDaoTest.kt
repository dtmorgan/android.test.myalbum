package com.mdiot.test.myalbums.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.mdiot.test.myalbums.MainCoroutineRule
import com.mdiot.test.myalbums.data.Track
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TracksDaoTest {

    private lateinit var database: TracksDatabase

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each track synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        // using an in-memory database because the information stored here disappears when the
        // process is killed
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TracksDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertTrackAndGetTracks() = runBlockingTest {
        // GIVEN - insert a track
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        database.tracksDao().insertTrack(track)

        // WHEN - Get tracks from the database
        val tracks = database.tracksDao().getTracks()

        // THEN - There is only 1 track in the database, and contains the expected values
        MatcherAssert.assertThat(tracks.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(tracks[0].id, `is`(track.id))
        MatcherAssert.assertThat(tracks[0].albumId, `is`(track.albumId))
        MatcherAssert.assertThat(tracks[0].title, `is`(track.title))
        MatcherAssert.assertThat(tracks[0].url, `is`(track.url))
        MatcherAssert.assertThat(tracks[0].thumbnailUrl, `is`(track.thumbnailUrl))
    }

    @Test
    fun insertTrackReplacesOnConflict() = runBlockingTest {
        // Given that a tracks is inserted
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        database.tracksDao().insertTrack(track)

        // When a track with the same id is inserted
        val newTrack = Track(track.id, track.albumId, "title2", "url2", "thumbnailUrl2")
        database.tracksDao().insertTrack(newTrack)

        // THEN - The loaded data contains the expected values
        val tracks = database.tracksDao().getTracks()
        MatcherAssert.assertThat(tracks.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(tracks[0].id, `is`(newTrack.id))
        MatcherAssert.assertThat(tracks[0].albumId, `is`(newTrack.albumId))
        MatcherAssert.assertThat(tracks[0].title, `is`(newTrack.title))
        MatcherAssert.assertThat(tracks[0].url, `is`(newTrack.url))
        MatcherAssert.assertThat(tracks[0].thumbnailUrl, `is`(newTrack.thumbnailUrl))
    }

    @Test
    fun deleteTracksAndGettingTracks() = runBlockingTest {
        // Given a tracks inserted
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        database.tracksDao().insertTrack(track)

        // When deleting all tracks
        database.tracksDao().deleteAllTracks()

        // THEN - The list is empty
        val tracks = database.tracksDao().getTracks()
        MatcherAssert.assertThat(tracks.isEmpty(), `is`(true))
    }

}