package it.thefedex87.cooldrinks.presentation.drink_details

import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.presentation.util.UiText

data class DrinkDetailState(
    val isLoading: Boolean = false,
    val isFavorite: Boolean? = null,
    val drinkDominantColor: Int? = null,
    val drinkImagePath: String? = null,
    val drinkIngredients: List<DrinkIngredientModel>? = null,
    val drinkInstructions: String? = null,
    val drinkName: String? = null,
    val drinkAlcoholic: UiText? = null,
    val drinkGlass: String? = null,
    val drinkCategory: String? = null,
    val showConfirmRemoveFavoriteDialog: Boolean = false
)