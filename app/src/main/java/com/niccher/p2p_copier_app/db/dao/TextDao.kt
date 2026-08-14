package com.niccher.p2p_copier_app.db.dao

import androidx.room.*
import com.niccher.p2p_copier_app.db.entity.TextEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TextDao {

    @Query("SELECT * FROM text_history ORDER BY id DESC")
    fun getAllTextsFlow(): Flow<List<TextEntity>>

    @Query("SELECT * FROM text_history ORDER BY id DESC")
    fun getAllTextsSync(): List<TextEntity>

    @Query("SELECT * FROM text_history WHERE sessionId = :sessionId ORDER BY id DESC")
    fun getTextsBySession(sessionId: String): List<TextEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertText(text: TextEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTextSync(text: TextEntity): Long

    @Delete
    suspend fun deleteText(text: TextEntity)

    @Query("DELETE FROM text_history WHERE textUuid = :textUuid")
    suspend fun deleteByUuid(textUuid: String)

    @Query("DELETE FROM text_history")
    suspend fun clearAll()
}
