package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.graphics.Bitmap
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import it.thefedex87.cooldrinks.presentation.util.UiText

data class AddMyDrinkState(
    val cocktailName: String = "",
    val cocktailNameError: UiText? = null,

    val cocktailGlass: String = "",
    val cocktailCategory: String = "",
    val cocktailIsAlcoholic: Boolean = true,

    val cocktailIngredients: List<DrinkIngredientModel> = emptyList(),
    val cocktailIngredientsError: UiText? = null,

    val cocktailInstructions: String = "",
    val cocktailInstructionsError: UiText? = null,

    val selectedPicture: Bitmap? = null,

    val addingIngredientName: String? = null,
    val addingIngredientMeasure: String? = null,
    val addingIngredientIsDecoration: Boolean = false,
    val addingIngredientIsAvailable: Boolean = false,
    val addingIngredientFilteredIngredients: List<DrinkIngredientModel> = emptyList(),

    val isLoading: Boolean = false
)
