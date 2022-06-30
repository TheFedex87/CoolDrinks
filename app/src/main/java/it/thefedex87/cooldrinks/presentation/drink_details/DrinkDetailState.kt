package it.thefedex87.cooldrinks.presentation.drink_details

data class DrinkDetailState(
    val drinkImagePath: String? = null,
    val drinkIngredients: List<String> = emptyList()
)