package it.thefedex87.cooldrinks.presentation.search_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel

data class SearchDrinkState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showSearchHint: Boolean = true,
    val showNoDrinkFound: Boolean = false,
    val foundDrinks: List<DrinkDomainModel> = emptyList()
)
