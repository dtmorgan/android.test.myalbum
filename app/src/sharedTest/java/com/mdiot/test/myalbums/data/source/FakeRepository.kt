package com.mdiot.test.myalbums.data.source

import androidx.annotation.VisibleForTesting
import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import javax.inject.Inject

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository @Inject constructor() : TracksRepository {

    var tracksServiceData: MutableMap<Int, Track> = mutableMapOf()
        private set

    var shouldReturnError = false

    override suspend fun getTracks(forceUpdate: Boolean): Result<List<Track>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(tracksServiceData.values.toList())
    }

    @VisibleForTesting
    fun addTracks(vararg tracks: Track) {
        tracks.forEach { tracksServiceData[it.id] = it }
    }

}