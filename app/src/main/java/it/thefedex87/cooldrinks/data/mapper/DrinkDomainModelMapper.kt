package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.remote.dto.DrinkDetailDto
import it.thefedex87.cooldrinks.data.remote.dto.DrinkDto
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel

fun DrinkDto.toDrinkDomainModel(): DrinkDomainModel? {
    return DrinkDomainModel(
        name = strDrink ?: return null,
        image = strDrinkThumb,
        id = idDrink.toInt()
    )
}

fun DrinkDetailDto.toDrinkDetailDomainModel(): DrinkDetailDomainModel? {
    return DrinkDetailDomainModel(
        idDrink = idDrink.toInt(),
        isAlcoholic = strAlcoholic == "Alcoholic",
        category = strCategory,
        name = strDrink,
        drinkThumb = strDrinkThumb,
        glass = strGlass,
        ingredients = listOf(
            strIngredient1,
            strIngredient2,
            strIngredient3,
            strIngredient4,
            strIngredient5,
            strIngredient6,
            strIngredient7,
            strIngredient8,
            strIngredient9,
            strIngredient10,
            strIngredient11,
            strIngredient12,
            strIngredient13,
            strIngredient14,
            strIngredient15,
        ),
        instructions = strInstructions,
        measures = listOf(
            strMeasure1,
            strMeasure2,
            strMeasure3,
            strMeasure4,
            strMeasure5,
            strMeasure6,
            strMeasure7,
            strMeasure8,
            strMeasure9,
            strMeasure10,
            strMeasure11,
            strMeasure12,
            strMeasure13,
            strMeasure14,
            strMeasure15,
        )
    )
}