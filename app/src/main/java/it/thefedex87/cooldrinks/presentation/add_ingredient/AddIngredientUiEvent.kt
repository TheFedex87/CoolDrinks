package it.thefedex87.cooldrinks.presentation.add_ingredient

import java.util.*

sealed class AddIngredientUiEvent {
    data class SaveBitmapLocal(val path: String) : AddIngredientUiEvent()
}