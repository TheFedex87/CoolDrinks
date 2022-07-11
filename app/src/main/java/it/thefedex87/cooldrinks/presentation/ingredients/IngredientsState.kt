package it.thefedex87.cooldrinks.presentation.ingredients

import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel

data class IngredientsState(
    val isLoading: Boolean = false,
    val ingredients: List<IngredientDomainModel> = emptyList(),
    val showIngredientDetails: Boolean = false
)
