package it.thefedex87.cooldrinks.presentation.add_my_drink

import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.presentation.util.UiText

data class AddMyDrinkState(
    val cocktailName: String = "",
    val cocktailNameError: UiText? = null,

    val cocktailGlass: String = "",
    val cocktailCategory: String = "",
    val cocktailIsAlcoholic: Boolean = true,
    val cocktailIngredients: List<DrinkIngredientModel> = emptyList(),

    val cocktailInstructions: String = "",
    val cocktailInstructionsError: UiText? = null
)
