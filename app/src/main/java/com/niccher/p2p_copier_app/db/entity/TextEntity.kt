package com.niccher.p2p_copier_app.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "text_history")
data class TextEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val textUuid: String,
    val textTitle: String?,
    val textContent: String,
    val textSource: String = "Mobile",
    val sessionId: String,
    val createdAt: Long = System.currentTimeMillis()
)
