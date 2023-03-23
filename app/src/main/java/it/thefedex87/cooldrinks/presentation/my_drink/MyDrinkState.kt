package it.thefedex87.cooldrinks.presentation.my_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.VisualizationType

data class MyDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList(),
    val isLoading: Boolean = false,

    val showConfirmRemoveDrinkDialog: Boolean = false,
    val drinkToRemove: DrinkDetailDomainModel? = null
)
