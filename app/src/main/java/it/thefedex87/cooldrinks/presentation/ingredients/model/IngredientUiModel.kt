package it.thefedex87.cooldrinks.presentation.ingredients.model

import androidx.compose.runtime.MutableState

data class IngredientUiModel(
    val name: String,
    val thumbnail: String,
    val isSelected: MutableState<Boolean>
)
