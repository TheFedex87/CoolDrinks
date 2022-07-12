package it.thefedex87.cooldrinks.presentation.ingredients

import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel

data class IngredientsState(
    val isLoading: Boolean = false,
    val ingredients: List<IngredientDomainModel> = emptyList(),
    val showDetailOfIngredient: String? = null,
    val isLoadingIngredientInfo: Boolean = false,
    val ingredientInfo: IngredientDetailsDomainModel? = null,
    val getIngredientInfoError: String? = null
)
