package it.thefedex87.cooldrinks.presentation.my_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel

sealed class MyDrinkEvent {
    data class OnChangeFavoriteStateClicked(val drink: DrinkDetailDomainModel) : MyDrinkEvent()
}