package it.thefedex87.cooldrinks.presentation.ingredients

import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import it.thefedex87.cooldrinks.presentation.ingredients.model.IngredientUiModel

data class IngredientsState(
    val isLoading: Boolean = false,
    val showRetryButton: Boolean = false,
    val ingredients: List<IngredientUiModel> = emptyList(),
    val showDetailOfIngredient: String? = null,
    val isLoadingIngredientInfo: Boolean = false,
    val ingredientInfo: IngredientDetailsDomainModel? = null,
    val getIngredientInfoError: String? = null,
    val isMultiSelectionEnabled: Boolean = false,
    val searchQuery: String = "",
    val showSearchHint: Boolean = true,

)
