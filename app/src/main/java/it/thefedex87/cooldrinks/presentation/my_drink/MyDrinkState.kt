package it.thefedex87.cooldrinks.presentation.my_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel

data class MyDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList()
)
