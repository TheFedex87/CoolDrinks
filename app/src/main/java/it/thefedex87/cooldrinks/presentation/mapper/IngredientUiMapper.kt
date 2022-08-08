package it.thefedex87.cooldrinks.presentation.mapper

import androidx.compose.runtime.mutableStateOf
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import it.thefedex87.cooldrinks.presentation.ingredients.model.IngredientUiModel

fun IngredientDomainModel.toIngredientUiModel(): IngredientUiModel {
    return IngredientUiModel(name = name, isSelected = mutableStateOf(false))
}

fun IngredientUiModel.toIngredientDomainModel(): IngredientDomainModel {
    return IngredientDomainModel(name = name)
}