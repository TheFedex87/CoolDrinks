package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel

data class FavoriteDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList()
)