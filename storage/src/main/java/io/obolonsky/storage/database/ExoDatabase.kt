package io.obolonsky.storage.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "ExoPlayerDownloads")
class ExoDownload(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "stream_keys") val stream_keys: String?,
    @ColumnInfo(name = "custom_cache_key") val custom_cache_key: String?,
    @ColumnInfo(name = "data", typeAffinity = androidx.room.ColumnInfo.BLOB) val data: ByteArray?,
    @ColumnInfo(name = "state") val state: Int?,
    @ColumnInfo(name = "start_time_ms") val start_time_ms: Int?,
    @ColumnInfo(name = "update_time_ms") val update_time_ms: Int?,
    @ColumnInfo(name = "content_length") val content_length: Int?,
    @ColumnInfo(name = "stop_reason") val stop_reason: Int?,
    @ColumnInfo(name = "failure_reason") val failure_reason: Int?,
    @ColumnInfo(name = "percent_downloaded") val percent_downloaded: Double,
    @ColumnInfo(name = "bytes_downloaded") val bytes_downloaded: Int?,
)

@Dao
interface ExoPlayerDao {

    @Query("SELECT * from ExoPlayerDownloads")
    fun getDownloadsFlow(): Flow<List<ExoDownload>>
}

@Database(
    entities = [
        ExoDownload::class,
    ],
    version = 1,
)
abstract class ExoDatabase : RoomDatabase() {

    abstract val exoDao: ExoPlayerDao
}

// exoplayer_internal.db