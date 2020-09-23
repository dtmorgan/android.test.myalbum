package com.mdiot.test.myalbums.data.source

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import kotlinx.coroutines.flow.Flow

interface TracksDataSource {

    suspend fun observeTracks(): Flow<Result<List<Track>>>

    suspend fun getTracks(): Result<List<Track>>

    suspend fun saveTrack(track: Track)

    suspend fun deleteAllTracks()

}