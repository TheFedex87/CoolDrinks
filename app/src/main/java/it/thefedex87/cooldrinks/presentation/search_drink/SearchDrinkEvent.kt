package it.thefedex87.cooldrinks.presentation.search_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel

sealed class SearchDrinkEvent {
    data class OnSearchQueryChange(val query: String) : SearchDrinkEvent()
    object OnSearchClick : SearchDrinkEvent()
    data class OnSearchFocusChange(val isFocused: Boolean) : SearchDrinkEvent()
    data class OnDrinkClick(val drink: DrinkDomainModel) : SearchDrinkEvent()
    data class OnFavoriteClick(val drink: DrinkUiModel) : SearchDrinkEvent()

    object ExpandeAlcoholMenu : SearchDrinkEvent()
    object CollapseAlcoholMenu : SearchDrinkEvent()
    data class AlcoholFilterValueChanged(val filter: AlcoholFilter = AlcoholFilter.NONE) : SearchDrinkEvent()
}
