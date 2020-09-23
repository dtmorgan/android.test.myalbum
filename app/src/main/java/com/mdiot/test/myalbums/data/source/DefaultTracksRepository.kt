package com.mdiot.test.myalbums.data.source

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import com.mdiot.test.myalbums.utils.wrapEspressoIdlingResource

class DefaultTracksRepository(
    private val tracksRemoteDataSource: TracksDataSource,
    private val tracksLocalDataSource: TracksDataSource
) : TracksRepository {

    override suspend fun getTracks(forceUpdate: Boolean): Result<List<Track>> {
        wrapEspressoIdlingResource {
            return if (forceUpdate) {
                updateTracksFromRemoteDataSource()
            } else {
                tracksLocalDataSource.getTracks()
            }
        }
    }

    private suspend fun updateTracksFromRemoteDataSource(): Result<List<Track>> {
        val remoteTracks = tracksRemoteDataSource.getTracks()
        if (remoteTracks is Result.Success) {
            tracksLocalDataSource.deleteAllTracks()
            remoteTracks.data.forEach { tracksLocalDataSource.saveTrack(it) }
        }
        return remoteTracks
    }

}