package it.thefedex87.cooldrinks.presentation.favorite_drink.models

import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import java.time.LocalDate

data class FavoriteDrinkUiModel(
    val idDrink: Int,
    val isAlcoholic: Boolean,
    val category: String,
    val name: String,
    val drinkThumb: String,
    val glass: String,
    val ingredients: List<DrinkIngredientModel?>,
    val instructions: String,
    val addedDate: LocalDate? = null,
    val dominantColor: Int? = null,
    val missingIngredientsCount: Int = 0
)