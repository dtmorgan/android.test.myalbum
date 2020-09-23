package com.mdiot.test.myalbums

import androidx.lifecycle.LiveData
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert

fun assertSnackbarMessage(snackbarLiveData: LiveData<Event<Int>>, messageId: Int) {
    val value: Event<Int> = snackbarLiveData.getOrAwaitValue()
    MatcherAssert.assertThat(value.getContentIfNotHandled(), CoreMatchers.`is`(messageId))
}
