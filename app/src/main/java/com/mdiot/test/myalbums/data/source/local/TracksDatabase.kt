package com.mdiot.test.myalbums.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mdiot.test.myalbums.data.Track

/**
 * The Room Database that contains the Tracks table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [Track::class], version = 1, exportSchema = false)
abstract class TracksDatabase : RoomDatabase() {

    abstract fun tracksDao(): TracksDao

}