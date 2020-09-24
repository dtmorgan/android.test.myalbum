package com.mdiot.test.myalbums.data.source.remote

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.source.TracksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TracksRemoteDataSource internal constructor(
    private val tracksApi: TracksApi,
    private val ioDispatcher: CoroutineDispatcher
) : TracksDataSource {

    override suspend fun getTracks(): Result<List<Track>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(tracksApi.getTracks())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveTrack(track: Track) {
        //No write access on remote API
    }

    override suspend fun deleteAllTracks() {
        //No write access on remote API
    }

}