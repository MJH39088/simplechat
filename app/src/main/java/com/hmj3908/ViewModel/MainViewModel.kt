package com.hmj3908.ViewModel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hmj3908.DataModel.ChatDao
import com.hmj3908.DataModel.ChatRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val chatDao: ChatDao,
    private val dataStore: DataStore<Preferences>
): ViewModel() {

    private val _rooms = MutableLiveData<List<ChatRoom>>(emptyList())
    val rooms: LiveData<List<ChatRoom>> get() = _rooms

    init {
        viewModelScope.launch {
            loadAllRooms()
        }
    }

    private suspend fun loadAllRooms() {
        _rooms.value = chatDao.getAllRooms()
    }

    fun deleteRoom(room: ChatRoom) {
        viewModelScope.launch {
            chatDao.deleteRoom(room)
            loadAllRooms() // 삭제 후 목록 업데이트
        }
    }

    fun createRoom(roomName: String, onRoomCreated: (Int) -> Unit) {
        viewModelScope.launch {
//            chatDao.insertRoom(ChatRoom(roomName = roomName))
            val room = ChatRoom(roomName = roomName)
            val roomId = chatDao.insertRoom(room).toInt() // 새로 생성된 방 ID
            loadAllRooms()
            onRoomCreated(roomId) // 방 ID를 콜백으로 반환
        }
    }
}