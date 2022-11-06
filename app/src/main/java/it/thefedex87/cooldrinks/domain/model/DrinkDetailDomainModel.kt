package it.thefedex87.cooldrinks.domain.model

import java.time.LocalDate

data class DrinkDetailDomainModel(
    val idDrink: Int,
    val isAlcoholic: Boolean,
    val category: String,
    val name: String,
    val drinkThumb: String,
    val glass: String,
    val ingredients: List<DrinkIngredientModel>,
    val instructions: String,
    val isCustomCocktail: Boolean,
    val isFavorite: Boolean,
    val addedDate: LocalDate? = null,
    val dominantColor: Int? = null
)