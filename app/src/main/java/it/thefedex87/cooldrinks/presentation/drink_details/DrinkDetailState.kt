package it.thefedex87.cooldrinks.presentation.drink_details

data class DrinkDetailState(
    val isLoading: Boolean = false,
    val isFavorite: Boolean? = null,
    val drinkImagePath: String? = null,
    val drinkIngredients: List<Pair<String, String>>? = null,
    val drinkInstructions: String? = null,
    val drinkName: String? = null,
    val drinkAlcoholic: String? = null,
    val drinkGlass: String? = null,
    val drinkCategory: String? = null,
    val showConfirmRemoveFavoriteDialog: Boolean = false
)