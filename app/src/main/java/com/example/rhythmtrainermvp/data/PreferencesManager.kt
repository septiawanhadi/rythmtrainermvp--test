package com.example.rhythmtrainermvp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rhythm_trainer_prefs")

class PreferencesManager(private val context: Context) {

    companion object {
        val HIGHEST_SCORE = intPreferencesKey("highest_score")
        val LEVEL_UNLOCKED = booleanPreferencesKey("level_unlocked")
    }

    val highestScoreFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[HIGHEST_SCORE] ?: 0
    }

    val levelUnlockedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[LEVEL_UNLOCKED] ?: false
    }

    suspend fun saveHighestScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentScore = preferences[HIGHEST_SCORE] ?: 0
            if (score > currentScore) {
                preferences[HIGHEST_SCORE] = score
            }
        }
    }

    suspend fun unlockLevel(unlocked: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[LEVEL_UNLOCKED] = unlocked
        }
    }
}
