package it.thefedex87.cooldrinks.presentation.bar

import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel

data class BarState(
    val ingredients: List<IngredientDetailsDomainModel> = emptyList(),
    val selectedOption: Int = 0,
    val selectedIngredient: IngredientDetailsDomainModel? = null
)
