package com.hmj3908.DataModel

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ChatDao {

    @Insert
    suspend fun insertRoom(chatRoom: ChatRoom): Long

    @Query("SELECT * FROM ChatRoom")
    suspend fun getAllRooms(): List<ChatRoom>

    @Insert
    suspend fun insertMessage(chatMessage: ChatMessage)

    @Delete
    suspend fun deleteRoom(chatRoom: ChatRoom)

    @Query("SELECT * FROM ChatMessage WHERE roomId = :roomId ORDER BY timestamp DESC")
    suspend fun getMessagesForRoom(roomId: Int): List<ChatMessage>
}