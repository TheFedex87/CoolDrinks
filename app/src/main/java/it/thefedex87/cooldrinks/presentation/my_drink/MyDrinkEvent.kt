package it.thefedex87.cooldrinks.presentation.my_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel

sealed class MyDrinkEvent {
    data class OnChangeFavoriteStateClicked(val drinkId: Int) : MyDrinkEvent()
    data class OnRemoveDrinkClicked(val drinkId: Int) : MyDrinkEvent()

    data class OnRemoveDrinkConfirmed(val drink: DrinkDetailDomainModel) : MyDrinkEvent()
    object OnRemoveDrinkCanceled : MyDrinkEvent()
}