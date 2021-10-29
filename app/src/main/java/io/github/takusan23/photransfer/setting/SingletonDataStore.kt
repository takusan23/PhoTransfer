package io.github.takusan23.photransfer.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** SharedPreferenceの後継、DataStoreを使う */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")