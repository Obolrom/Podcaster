package io.obolonsky.storage.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.obolonsky.storage.database.entities.ShazamTrack
import io.obolonsky.storage.database.utils.Guid
import kotlinx.coroutines.flow.Flow

@Dao
interface ShazamTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<ShazamTrack>)

    @Query("DELETE FROM shazam_tracks WHERE tag_id = :trackId")
    suspend fun delete(trackId: Guid)

    @Query("SELECT * FROM shazam_tracks")
    fun getShazamTracksFlow(): Flow<List<ShazamTrack>>

    @Query("DELETE FROM shazam_tracks")
    suspend fun clear()
}