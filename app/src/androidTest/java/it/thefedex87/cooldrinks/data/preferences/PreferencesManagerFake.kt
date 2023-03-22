package it.thefedex87.cooldrinks.data.preferences

import it.thefedex87.cooldrinks.domain.model.AppPreferences
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesManagerFake : PreferencesManager {
    private val appPreferences = MutableStateFlow(AppPreferences(visualizationType = VisualizationType.Card))

    override fun preferencesFlow(): Flow<AppPreferences> = appPreferences.asStateFlow()

    override suspend fun updateVisualizationType(type: VisualizationType) {
        appPreferences.value = AppPreferences(type)
    }
}