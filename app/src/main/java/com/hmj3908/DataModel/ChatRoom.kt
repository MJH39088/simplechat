package com.hmj3908.DataModel

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomName: String
)

@Entity
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roomId: Int,
    val sender: String,
    val message: String,
    val timestamp: Long
)
