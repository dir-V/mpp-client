
package com.example.macropp.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

// Create the DataStore instance as a top-level property
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveUserId(userId: UUID) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId.toString()
        }
    }

    suspend fun getUserId(): UUID? {
        // 1. Get the stored value as a String?
        val userIdString = dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.first()

        // 2. Safely convert the String to a UUID only if it's not null
        return try {
            userIdString?.let { UUID.fromString(it) }
        } catch (e: IllegalArgumentException) {
            // This is important: If the stored string is not a valid UUID,
            // this will prevent a crash and return null.
            null
        }
    }
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}

private fun DataStore<Preferences>.edit(transform: suspend (MutablePreferences) -> Unit) {}
