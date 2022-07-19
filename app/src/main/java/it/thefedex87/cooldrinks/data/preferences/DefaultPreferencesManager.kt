package it.thefedex87.cooldrinks.data.preferences

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import it.thefedex87.cooldrinks.domain.model.AppPreferences
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager.Companion.DEFAULT_VISUALIZATION_TYPE_KEY
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager.Companion.VISUALIZATION_TYPE_KEY
import it.thefedex87.cooldrinks.util.Consts.TAG
import it.thefedex87.cooldrinks.util.Consts.USER_PREFERENCES_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DefaultPreferencesManager constructor(
    @ApplicationContext private val context: Context
) : PreferencesManager {
    private val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    override fun preferencesFlow(): Flow<AppPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            }
            throw exception
        }
        .map { preferences ->
            val visualizationType = VisualizationType.fromString(
                preferences[VISUALIZATION_TYPE_KEY] ?: DEFAULT_VISUALIZATION_TYPE_KEY
            )
            Log.d(TAG,  "Visualization type: $visualizationType")
            AppPreferences(
                visualizationType = visualizationType
            )
        }

    override suspend fun updateVisualizationType(type: VisualizationType) {
        context.dataStore.edit { preferences ->
            preferences[VISUALIZATION_TYPE_KEY] = VisualizationType.toSimpleString(type)
        }
    }

}