package com.hmj3908.Util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences

// DataStore의 이름 지정
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "chat_preferences")