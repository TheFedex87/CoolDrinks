package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.net.Uri
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel

sealed interface AddMyDrinkEvent {
    data class OnMyDrinkNameChanged(val name: String) : AddMyDrinkEvent
    data class OnMyDrinkInstructionsChanged(val instructions: String) : AddMyDrinkEvent

    data class OnMyDrinkGlassChanged(val glass: GlassUiModel) : AddMyDrinkEvent
    object OnMyDrinkGlassesExpandRequested : AddMyDrinkEvent
    object OnMyDrinkGlassesDismissRequested : AddMyDrinkEvent

    data class OnMyDrinkCategoryChanged(val category: CategoryUiModel) : AddMyDrinkEvent
    object OnMyDrinkCategoriesExpandRequested : AddMyDrinkEvent
    object OnMyDrinkCategoriesDismissRequested : AddMyDrinkEvent

    data class OnMyDrinkIsAlcoholicChanged(val isAlcoholic: Boolean) : AddMyDrinkEvent

    object AddDrinkIngredientRequested : AddMyDrinkEvent
    object DismissDrinkIngredientDialogRequested : AddMyDrinkEvent
    object AddDrinkIngredientSaveClicked : AddMyDrinkEvent
    data class OnMyDrinkAddingIngredientNameChanged(val name: String) : AddMyDrinkEvent
    data class OnMyDrinkAddingIngredientNameFocusChanged(val isFocused: Boolean) : AddMyDrinkEvent
    data class OnMyDrinkAddingIngredientMeasureChanged(val value: String) : AddMyDrinkEvent
    data class OnMyDrinkAddingIngredientIsDecorationChanged(val isDecoration: Boolean) :
        AddMyDrinkEvent

    data class OnMyDrinkAddingIngredientIsAvailableChanged(val isAvailable: Boolean) :
        AddMyDrinkEvent

    data class OnFilteredIngredientClicked(val ingredient: DrinkIngredientModel) : AddMyDrinkEvent
    data class OnSearchIngredientOnlineClicked(val ingredient: String) : AddMyDrinkEvent
    data class OnNewLocalIngredientStored(val ingredient: String?) : AddMyDrinkEvent
    data class RemoveAddedIngredient(val ingredient: DrinkIngredientModel) : AddMyDrinkEvent

    data class OnPictureSelected(val imagePath: Uri) : AddMyDrinkEvent
    data class PictureSaveResult(val success: Boolean, val pathCallback: () -> String) :
        AddMyDrinkEvent

    object OnSaveClicked : AddMyDrinkEvent
}
