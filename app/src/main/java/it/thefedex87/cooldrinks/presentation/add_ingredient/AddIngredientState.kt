package it.thefedex87.cooldrinks.presentation.add_ingredient

import android.graphics.Bitmap
import android.net.Uri
import it.thefedex87.cooldrinks.presentation.util.UiText

data class AddIngredientState(
    val ingredientName: String = "",
    val ingredientNameError: UiText? = null,

    val ingredientIsAlcoholic: Boolean = false,
    val ingredientDescription: String = "",
    val ingredientAvailable: Boolean = true,

    val selectedPicture: Uri? = null,

    val isEditing: Boolean = false
)