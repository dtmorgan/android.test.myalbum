package com.mdiot.test.myalbums.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mdiot.test.myalbums.data.Result
import com.mdiot.test.myalbums.data.Track
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the tracks table.
 */
@Dao
interface TracksDao {

    /**
     * Observes list of tracks.
     *
     * @return all tracks.
     */
    @Query("SELECT * FROM tracks")
    fun observeTracks(): Flow<List<Track>>

    @Query("SELECT * FROM tracks")
    suspend fun getTracks(): List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrack(track: Track)

    @Query("DELETE FROM tracks")
    fun deleteAllTracks()

}