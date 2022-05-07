package it.thefedex87.networkresponsestateeventtest.data.mapper

import it.thefedex87.networkresponsestateeventtest.data.remote.dto.Drink
import it.thefedex87.networkresponsestateeventtest.domain.model.DrinkDomainModel

fun Drink.toDrinkDomainModel(): DrinkDomainModel? {
    return DrinkDomainModel(
        name = strDrink ?: return null,
        image = strDrinkThumb,
        id = idDrink.toInt()
    )
}