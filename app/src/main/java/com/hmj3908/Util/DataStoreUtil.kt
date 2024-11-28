package com.hmj3908.Util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object DataStoreUtil {
    private val LAST_ROOM_ID_KEY = intPreferencesKey("last_room_id")

    suspend fun saveLastRoomId(context: Context, roomId: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_ROOM_ID_KEY] = roomId
        }
    }

    fun getLastRoomId(context: Context): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[LAST_ROOM_ID_KEY]
        }
    }

}