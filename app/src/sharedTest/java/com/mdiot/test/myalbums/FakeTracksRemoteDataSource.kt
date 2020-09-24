package com.mdiot.test.myalbums

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.source.TracksDataSource

object FakeTracksRemoteDataSource : TracksDataSource {

    private var tracksServiceData: MutableMap<Int, Track> = mutableMapOf()

    override suspend fun getTracks(): Result<List<Track>> {
        return Result.Success(tracksServiceData.values.toList())
    }

    override suspend fun saveTrack(track: Track) {
        tracksServiceData[track.id] = track
    }

    override suspend fun deleteAllTracks() {
        tracksServiceData.clear()
    }

}