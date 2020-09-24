package com.mdiot.test.myalbums.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mdiot.test.myalbums.data.Track

/**
 * Data Access Object for the tracks table.
 */
@Dao
interface TracksDao {

    @Query("SELECT * FROM tracks")
    suspend fun getTracks(): List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: Track)

    @Query("DELETE FROM tracks")
    fun deleteAllTracks()

}