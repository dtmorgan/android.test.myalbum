package com.mdiot.test.myalbums.data.source.remote

import com.mdiot.test.myalbums.data.Track
import retrofit2.http.GET

interface TracksApi {

    @GET("technical-test.json")
    suspend fun getTracks(): List<Track>

}