package com.mdiot.test.myalbums.data.source

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track

class FakeDataSource(var tracks: MutableList<Track>? = mutableListOf()) : TracksDataSource {

    override suspend fun getTracks(): Result<List<Track>> {
        tracks?.let { return Result.Success(ArrayList(it)) }
        return Result.Error(
            Exception("Tracks not found")
        )
    }

    override suspend fun saveTrack(track: Track) {
        tracks?.add(track)
    }

    override suspend fun deleteAllTracks() {
        tracks?.clear()
    }

}