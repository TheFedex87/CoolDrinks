package it.thefedex87.cooldrinks.presentation.search_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel

sealed class SearchDrinkEvent {
    data class OnSearchQueryChange(val query: String) : SearchDrinkEvent()
    object OnSearchClick : SearchDrinkEvent()
    data class OnSearchFocusChange(val isFocused: Boolean) : SearchDrinkEvent()
    data class OnDrinkClick(val drink: DrinkDomainModel) : SearchDrinkEvent()
}
