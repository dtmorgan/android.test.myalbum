package com.mdiot.test.myalbums.tracks

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.mdiot.test.myalbums.R
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.source.FakeRepository
import com.mdiot.test.myalbums.data.source.TracksRepository
import com.mdiot.test.myalbums.di.TracksRepositoryModule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import androidx.test.espresso.matcher.ViewMatchers.withText

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
@UninstallModules(TracksRepositoryModule::class)
@HiltAndroidTest
class TracksFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: TracksRepository

    private val fakeRepository
        get() = repository as FakeRepository

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
    }

    @Test
    fun displayTracks_whenRepositoryHasData() {
        val track = Track(4785, 3654, "title", "url", "thumbnailUrl")
        fakeRepository.addTracks(track)

        launchActivity()

        Espresso.onView(withText("title")).check(ViewAssertions.matches(isDisplayed()))
    }

    private fun launchActivity(): ActivityScenario<TracksActivity>? {
        val activityScenario = launch(TracksActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animations in RecyclerView
            (activity.findViewById(R.id.rvItemList) as RecyclerView).itemAnimator = null
        }
        return activityScenario
    }

}