package it.thefedex87.cooldrinks.presentation.ingredients

sealed class IngredientsEvent {
    object HideIngredientsDetails: IngredientsEvent()
    data class ShowIngredientsDetails(val ingredient: String): IngredientsEvent()
}
