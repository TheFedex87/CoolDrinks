package it.thefedex87.cooldrinks.presentation.add_ingredient

import android.net.Uri
import android.os.Parcelable
import it.thefedex87.cooldrinks.presentation.util.UiText
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddIngredientState(
    val ingredientName: String = "",
    val ingredientNameError: UiText? = null,

    val ingredientIsAlcoholic: Boolean = false,
    val ingredientDescription: String = "",
    val ingredientAvailable: Boolean = true,

    val selectedPicture: Uri? = null,
    val title: UiText = UiText.Empty,

    /*val id: String? = null,
    val name: String? = null,
    val prevImagePath: String? = null,
    val loaded: Boolean = false,*/

): Parcelable