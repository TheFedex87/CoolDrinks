package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkEvent

sealed class FavoriteDrinkEvent {
    object ExpandeAlcoholMenu : FavoriteDrinkEvent()
    object CollapseAlcoholMenu : FavoriteDrinkEvent()
    data class AlcoholFilterValueChanged(val filter: AlcoholFilter = AlcoholFilter.NONE) : FavoriteDrinkEvent()

    object ExpandeGlassMenu : FavoriteDrinkEvent()
    object CollapseGlassMenu : FavoriteDrinkEvent()
    data class GlassFilterValueChanged(val filter: GlassFilter = GlassFilter.NONE) : FavoriteDrinkEvent()
}
