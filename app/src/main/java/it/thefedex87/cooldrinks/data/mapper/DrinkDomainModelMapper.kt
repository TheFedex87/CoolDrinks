package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.remote.dto.DrinkDetailDto
import it.thefedex87.cooldrinks.data.remote.dto.DrinkDto
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel

fun DrinkDto.toDrinkDomainModel(favorites: List<Int>? = null): DrinkDomainModel? {
    return DrinkDomainModel(
        name = strDrink ?: return null,
        image = strDrinkThumb,
        id = idDrink.toInt(),
        isFavorite = favorites?.any { it == idDrink.toInt() } == true
    )
}

fun DrinkDetailDto.toDrinkDetailDomainModel(availableIngredients: List<IngredientDomainModel>): DrinkDetailDomainModel? {
    return DrinkDetailDomainModel(
        idDrink = idDrink.toInt(),
        isAlcoholic = strAlcoholic == "Alcoholic",
        category = strCategory,
        name = strDrink,
        drinkThumb = strDrinkThumb,
        glass = strGlass,
        ingredients = listOf(
            DrinkIngredientModel(
                name = strIngredient1,
                measure = strMeasure1,
                isAvailable = availableIngredients.any { it.name == strIngredient1 }
            ),
            DrinkIngredientModel(
                name = strIngredient2,
                measure = strMeasure2,
                isAvailable = availableIngredients.any { it.name == strIngredient2 }
            ),
            DrinkIngredientModel(
                name = strIngredient3,
                measure = strMeasure3,
                isAvailable = availableIngredients.any { it.name == strIngredient3 }
            ),
            DrinkIngredientModel(
                name = strIngredient4,
                measure = strMeasure4,
                isAvailable = availableIngredients.any { it.name == strIngredient4 }
            ),
            DrinkIngredientModel(
                name = strIngredient5,
                measure = strMeasure5,
                isAvailable = availableIngredients.any { it.name == strIngredient5 }
            ),
            DrinkIngredientModel(
                name = strIngredient6,
                measure = strMeasure6,
                isAvailable = availableIngredients.any { it.name == strIngredient6 }
            ),
            DrinkIngredientModel(
                name = strIngredient7,
                measure = strMeasure7,
                isAvailable = availableIngredients.any { it.name == strIngredient7 }
            ),
            DrinkIngredientModel(
                name = strIngredient8,
                measure = strMeasure8,
                isAvailable = availableIngredients.any { it.name == strIngredient8 }
            ),
            DrinkIngredientModel(
                name = strIngredient9,
                measure = strMeasure9,
                isAvailable = availableIngredients.any { it.name == strIngredient9 }
            ),
            DrinkIngredientModel(
                name = strIngredient10,
                measure = strMeasure10,
                isAvailable = availableIngredients.any { it.name == strIngredient10 }
            ),
            DrinkIngredientModel(
                name = strIngredient11,
                measure = strMeasure11,
                isAvailable = availableIngredients.any { it.name == strIngredient11 }
            ),
            DrinkIngredientModel(
                name = strIngredient12,
                measure = strMeasure12,
                isAvailable = availableIngredients.any { it.name == strIngredient12 }
            ),
            DrinkIngredientModel(
                name = strIngredient13,
                measure = strMeasure13,
                isAvailable = availableIngredients.any { it.name == strIngredient13 }
            ),
            DrinkIngredientModel(
                name = strIngredient14,
                measure = strMeasure14,
                isAvailable = availableIngredients.any { it.name == strIngredient14 }
            ),
            DrinkIngredientModel(
                name = strIngredient15,
                measure = strMeasure15,
                isAvailable = availableIngredients.any { it.name == strIngredient15 }
            )
        ),
        instructions = strInstructions
    )
}