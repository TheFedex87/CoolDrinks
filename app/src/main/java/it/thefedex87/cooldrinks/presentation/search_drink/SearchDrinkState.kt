package it.thefedex87.cooldrinks.presentation.search_drink

import androidx.compose.runtime.MutableState
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel

data class SearchDrinkState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showSearchHint: Boolean = true,
    val showNoDrinkFound: Boolean = false,
    val foundDrinks: MutableList<MutableState<DrinkUiModel>> = mutableListOf(),
)

