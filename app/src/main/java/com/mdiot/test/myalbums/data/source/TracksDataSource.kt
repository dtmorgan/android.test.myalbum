package com.mdiot.test.myalbums.data.source

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track

interface TracksDataSource {

    suspend fun getTracks(): Result<List<Track>>

    suspend fun saveTrack(track: Track)

    suspend fun deleteAllTracks()

}