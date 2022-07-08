package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailEvent
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkEvent

sealed class FavoriteDrinkEvent {
    data class UnfavoriteClicked(val drink: DrinkDetailDomainModel) : FavoriteDrinkEvent()
    data class RemoveFromFavoriteConfirmed(val drink: DrinkDetailDomainModel) : FavoriteDrinkEvent()
    object RemoveFromFavoriteCanceled: FavoriteDrinkEvent()

    object ExpandeAlcoholMenu : FavoriteDrinkEvent()
    object CollapseAlcoholMenu : FavoriteDrinkEvent()
    data class AlcoholFilterValueChanged(val filter: AlcoholFilter = AlcoholFilter.NONE) : FavoriteDrinkEvent()

    object ExpandeGlassMenu : FavoriteDrinkEvent()
    object CollapseGlassMenu : FavoriteDrinkEvent()
    data class GlassFilterValueChanged(val filter: GlassFilter = GlassFilter.NONE) : FavoriteDrinkEvent()

    object ExpandeCategoryMenu : FavoriteDrinkEvent()
    object CollapseCategoryMenu : FavoriteDrinkEvent()
    data class CategoryFilterValueChanged(val filter: CategoryFilter = CategoryFilter.NONE) : FavoriteDrinkEvent()
}
