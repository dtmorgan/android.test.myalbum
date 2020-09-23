package com.mdiot.test.myalbums.data.source

import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track

interface TracksRepository {

    suspend fun getTracks(forceUpdate: Boolean = false): Result<List<Track>>

}