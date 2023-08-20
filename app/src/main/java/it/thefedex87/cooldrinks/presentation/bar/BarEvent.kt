package it.thefedex87.cooldrinks.presentation.bar

import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel

sealed interface BarEvent {
    data class SetIngredientAvailability(val ingredient: IngredientDetailsDomainModel, val available: Boolean) : BarEvent
    data class SelectedIngredientChanged(val ingredient: IngredientDetailsDomainModel) : BarEvent
    data class JumpToStoredIngredient(val name: String): BarEvent
}
