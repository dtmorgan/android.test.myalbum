package com.mdiot.test.myalbums.tracks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.mdiot.test.myalbums.*
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for the implementation of [TracksViewModel]
 */
@ExperimentalCoroutinesApi
class TracksViewModelTest {

    // Subject under test
    private lateinit var tracksViewModel: TracksViewModel

    // Use a fake repository to be injected into the viewmodel
    private lateinit var tracksRepository: FakeRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Executes each track synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        // We initialise the tracks to 3, with one active and two completed
        tracksRepository = FakeRepository()
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        val track2 = Track(4786, 3655, "title2", "url2", "thumbnailUrl2")
        val track3 = Track(4787, 3656, "title3", "url3", "thumbnailUrl3")
        tracksRepository.addTracks(track, track2, track3)

        tracksViewModel = TracksViewModel(tracksRepository)
    }

    @Test
    fun loadAllTracksFromRepository_loadingTogglesAndDataLoaded() {
        // Pause dispatcher so we can verify initial values
        mainCoroutineRule.pauseDispatcher()

        // GIVEN - A repository of track

        // WHEN - Add new track & request refresh data from remote
        tracksRepository.addTracks(
            Track(4788, 3659, "title4", "url4", "thumbnailUrl4")
        )
        tracksViewModel.forceRefresh()

        // THEN - User is notify when loading & tracks update
        tracksViewModel.items.observeForTesting {
            MatcherAssert.assertThat(tracksViewModel.dataLoading.getOrAwaitValue(), CoreMatchers.`is`(true))

            // Execute pending coroutines actions
            mainCoroutineRule.resumeDispatcher()

            // Then progress indicator is hidden
            MatcherAssert.assertThat(tracksViewModel.dataLoading.getOrAwaitValue(), CoreMatchers.`is`(false))

            // And data correctly loaded
            MatcherAssert.assertThat(tracksViewModel.items.getOrAwaitValue().size, CoreMatchers.`is`(4))
        }
    }

    @Test
    fun loadTracks_error() {
        // GIVEN - Make the repository return errors
        tracksRepository.shouldReturnError = true

        // WHEN - Load tracks
        tracksViewModel.forceRefresh()

        // THEN - Error feedback is returned
        tracksViewModel.items.observeForTesting {
            // Then progress indicator is hidden
            MatcherAssert.assertThat(tracksViewModel.dataLoading.getOrAwaitValue(), CoreMatchers.`is`(false))

            // And the list of items is empty
            MatcherAssert.assertThat(tracksViewModel.items.getOrAwaitValue().isEmpty(), CoreMatchers.`is`(true))

            // And the snackbar updated
            assertSnackbarMessage(tracksViewModel.snackbarText, R.string.loading_tracks_error)
        }
    }

}