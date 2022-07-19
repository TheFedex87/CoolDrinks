package it.thefedex87.cooldrinks.domain.preferences

import androidx.datastore.preferences.core.stringPreferencesKey
import it.thefedex87.cooldrinks.domain.model.AppPreferences
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {
    fun preferencesFlow(): Flow<AppPreferences>

    suspend fun updateVisualizationType(type: VisualizationType)

    companion object {
        val VISUALIZATION_TYPE_KEY = stringPreferencesKey("AUTH_SERVICE_PREFERENCE_KEY")
        const val DEFAULT_VISUALIZATION_TYPE_KEY = "Card"
    }
}