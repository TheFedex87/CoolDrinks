package it.thefedex87.cooldrinks.presentation.add_ingredient

import android.graphics.Bitmap

sealed class AddIngredientEvent {
    data class OnIngredientNameChanged(val name: String) : AddIngredientEvent()
    data class OnIngredientDescriptionChanged(val description: String) : AddIngredientEvent()
    data class OnIngredientAlcoholicChanged(val isAlcoholic: Boolean) : AddIngredientEvent()
    data class OnIngredientAvailableChanged(val isAvailable: Boolean) : AddIngredientEvent()
    object OnSaveClicked : AddIngredientEvent()
    data class OnPictureSelected(val bitmap: Bitmap) : AddIngredientEvent()
    data class PictureSaveResult(val success: Boolean, val pathCallback: () -> String) : AddIngredientEvent()
}