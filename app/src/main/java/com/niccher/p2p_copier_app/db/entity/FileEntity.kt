package com.niccher.p2p_copier_app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_history")
data class FileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileUuid: String,
    val origName: String,
    val sysName: String,
    val fileType: String,
    val fileSize: Long,
    val localFilePath: String? = null,
    val sessionId: String,
    val createdAt: Long = System.currentTimeMillis()
)
