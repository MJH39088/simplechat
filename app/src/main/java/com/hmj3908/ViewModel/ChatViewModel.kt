package com.hmj3908.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmj3908.DataModel.ChatDao
import com.hmj3908.DataModel.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatDao: ChatDao
): ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> get() = _messages

    fun loadMessagesForRoom(roomId: Int) {
        viewModelScope.launch {
            _messages.value = chatDao.getMessagesForRoom(roomId)
        }
    }

//    fun sendMessage(roomId: Int, sender: String, message: String) {
//        viewModelScope.launch {
//            chatDao.insertMessage(ChatMessage(roomId = roomId, sender = sender, message = message, timestamp = System.currentTimeMillis()))
//            loadMessagesForRoom(roomId)
//        }
//    }

    fun saveMessage(chatMessage: ChatMessage) {
        viewModelScope.launch {
            chatDao.insertMessage(chatMessage)
            loadMessagesForRoom(chatMessage.roomId) // 저장 후 메시지 목록 업데이트
        }
    }
}