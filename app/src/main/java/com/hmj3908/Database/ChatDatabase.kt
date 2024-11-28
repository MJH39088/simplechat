package com.hmj3908.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hmj3908.DataModel.ChatDao
import com.hmj3908.DataModel.ChatMessage
import com.hmj3908.DataModel.ChatRoom

@Database(entities = [ChatRoom::class, ChatMessage::class], version = 1, exportSchema = false)
abstract class ChatDatabase: RoomDatabase() {
    abstract fun chatDao(): ChatDao
}