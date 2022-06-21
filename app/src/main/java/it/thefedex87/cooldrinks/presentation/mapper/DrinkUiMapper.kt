package it.thefedex87.cooldrinks.presentation.mapper

import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel

fun DrinkDomainModel.toDrinkUiModel(): DrinkUiModel {
    return DrinkUiModel(
        name = name,
        image = image,
        id = id,
        dominantColor = 0,
        isLoadingFavorite = false,
        isFavorite = isFavorite
    )
}

fun DrinkUiModel.toDrinkDomainModel(): DrinkDomainModel {
    return DrinkDomainModel(
        name = name,
        image = image,
        id = id,
        isFavorite = isFavorite
    )
}