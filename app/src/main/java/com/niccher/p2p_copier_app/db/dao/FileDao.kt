package com.niccher.p2p_copier_app.db.dao

import androidx.room.*
import com.niccher.p2p_copier_app.db.entity.FileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {

    @Query("SELECT * FROM file_history ORDER BY id DESC")
    fun getAllFilesFlow(): Flow<List<FileEntity>>

    @Query("SELECT * FROM file_history ORDER BY id DESC")
    fun getAllFilesSync(): List<FileEntity>

    @Query("SELECT * FROM file_history WHERE sessionId = :sessionId ORDER BY id DESC")
    fun getFilesBySession(sessionId: String): List<FileEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFileSync(file: FileEntity): Long

    @Delete
    suspend fun deleteFile(file: FileEntity)

    @Query("DELETE FROM file_history WHERE fileUuid = :fileUuid")
    suspend fun deleteByUuid(fileUuid: String)

    @Query("DELETE FROM file_history")
    suspend fun clearAll()
}
