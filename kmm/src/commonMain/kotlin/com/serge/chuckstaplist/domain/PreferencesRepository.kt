package com.serge.chuckstaplist.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val TUTORIAL_SHOWN_KEY = booleanPreferencesKey("tutorial_shown")

class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    val shouldShowTutorial: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[TUTORIAL_SHOWN_KEY]?.not() ?: true
    }
    
    suspend fun setTutorialShown(shown: Boolean) {
        dataStore.edit { preferences -> preferences[TUTORIAL_SHOWN_KEY] = shown }
    }
}
