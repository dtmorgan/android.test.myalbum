package com.mdiot.test.myalbums.data.source.local

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.data.source.TracksDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class TracksLocalDataSource internal constructor(
    private val tracksDao: TracksDao,
    private val ioDispatcher: CoroutineDispatcher
) : TracksDataSource {

    override suspend fun getTracks(): Result<List<Track>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(tracksDao.getTracks())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveTrack(track: Track) = withContext(ioDispatcher) {
        tracksDao.insertTrack(track)
    }

    override suspend fun deleteAllTracks() = withContext(ioDispatcher) {
        tracksDao.deleteAllTracks()
    }

}