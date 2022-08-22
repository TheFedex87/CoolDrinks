package it.thefedex87.cooldrinks.presentation.mapper

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.favorite_drink.models.FavoriteDrinkUiModel

fun DrinkDetailDomainModel.toFavoriteDrinkUiModel(): FavoriteDrinkUiModel {
    return FavoriteDrinkUiModel(
        idDrink = idDrink,
        isAlcoholic = isAlcoholic,
        category = category,
        name = name,
        drinkThumb = drinkThumb,
        glass = glass,
        ingredients = ingredients,
        instructions = instructions,
        addedDate = addedDate,
        dominantColor = dominantColor
    )
}