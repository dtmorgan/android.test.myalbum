package com.mdiot.test.myalbums.tracks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mdiot.test.myalbums.Event
import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.source.TracksRepository
import kotlinx.coroutines.launch
import com.mdiot.test.myalbums.R
import timber.log.Timber

class TracksViewModel @ViewModelInject constructor(
    private val tracksRepository: TracksRepository
) : ViewModel() {

    private val _items = MutableLiveData<List<Track>>()
    val items: LiveData<List<Track>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    init {
        viewModelScope.launch {
            val result = tracksRepository.getTracks()
            if (result is Result.Error || (result as Result.Success).data.isEmpty()) {
                Timber.d("Refresh tracks")
                refreshTracks()
            } else {
                _items.value = result.data
            }
        }
    }

    fun forceRefresh() {
        refreshTracks()
    }

    fun selectTrack(trackId: Int) {
        Timber.d("User select track with id $trackId")
    }

    private fun refreshTracks() {
        _dataLoading.value = true
        viewModelScope.launch {
            val result = tracksRepository.getTracks(true)
            _dataLoading.value = false
            if (result is Result.Success) {
                _items.value = result.data
            } else {
                _items.value = emptyList()
                showSnackbarMessage(R.string.loading_tracks_error)
            }
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

}