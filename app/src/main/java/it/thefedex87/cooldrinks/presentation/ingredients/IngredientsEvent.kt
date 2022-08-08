package it.thefedex87.cooldrinks.presentation.ingredients

sealed class IngredientsEvent {
    object HideIngredientsDetails: IngredientsEvent()
    object StoreIngredients: IngredientsEvent()
    data class MultiSelectionStateChanged(val enabled: Boolean): IngredientsEvent()
    data class ShowIngredientsDetails(val ingredient: String): IngredientsEvent()
    object RetryFetchIngredients : IngredientsEvent()
}
