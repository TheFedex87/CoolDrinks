package it.thefedex87.networkresponsestateeventtest.presentation.search_drink

import it.thefedex87.networkresponsestateeventtest.domain.model.DrinkDomainModel

data class SearchDrinkState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showSearchHint: Boolean = true,
    val showNoDrinkFound: Boolean = false,
    val foundDrinks: List<DrinkDomainModel> = emptyList()
)
