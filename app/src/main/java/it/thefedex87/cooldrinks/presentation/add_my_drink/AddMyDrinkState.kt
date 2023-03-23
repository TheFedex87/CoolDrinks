package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.net.Uri
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel
import it.thefedex87.cooldrinks.presentation.util.UiText

data class AddMyDrinkState(
    val cocktailName: String = "",
    val cocktailNameError: UiText? = null,

    val selectedCocktailGlass: GlassUiModel = GlassUiModel.NONE,
    val cocktailGlassesMenuExpanded: Boolean = false,
    val cocktailGlasses: List<GlassUiModel> = GlassUiModel.values().toList(),

    val selectedCocktailCategory: CategoryUiModel = CategoryUiModel.NONE,
    val cocktailCategoriesMenuExpanded: Boolean = false,
    val cocktailCategories: List<CategoryUiModel> = CategoryUiModel.values().toList(),

    val cocktailIsAlcoholic: Boolean = true,

    val cocktailIngredients: List<DrinkIngredientModel> = emptyList(),
    val cocktailIngredientsError: UiText? = null,

    val cocktailInstructions: String = "",
    val cocktailInstructionsError: UiText? = null,

    val selectedPicturePath: Uri? = null,

    val addingIngredientName: String? = null,
    val addingIngredientMeasure: String? = null,
    val addingIngredientIsDecoration: Boolean = false,
    val addingIngredientIsAvailable: Boolean = false,
    val addingIngredientFilteredIngredients: List<DrinkIngredientModel> = emptyList(),

    val isLoading: Boolean = false
)
