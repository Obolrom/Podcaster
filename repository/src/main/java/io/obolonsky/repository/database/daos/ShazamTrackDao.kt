package io.obolonsky.repository.database.daos

import androidx.room.*
import io.obolonsky.repository.database.entities.ShazamTrack
import kotlinx.coroutines.flow.Flow

@Dao
interface ShazamTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<ShazamTrack>)

    @Query("SELECT * FROM shazam_tracks")
    fun getShazamTracksFlow(): Flow<List<ShazamTrack>>

    @Query("DELETE FROM shazam_tracks")
    suspend fun clear()
}