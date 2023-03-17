package it.thefedex87.cooldrinks.presentation.search_drink

import androidx.compose.runtime.MutableState
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel

data class SearchDrinkState(
    val searchQuery: String = "",
    val searchByIngredientQuery: String = "",
    val isLoading: Boolean = false,
    val showSearchHint: Boolean = true,
    val showNoDrinkFound: Boolean = false,
    val foundDrinks: MutableList<MutableState<DrinkUiModel>> = mutableListOf(),
    val visualizationType: VisualizationType = VisualizationType.Card
)

