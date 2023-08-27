package it.thefedex87.cooldrinks.domain.model

data class IngredientDetailsDomainModel(
    val id: String? = null,
    val name: String,
    val description: String?,
    val type: String?,
    val alcoholic: Boolean,
    val imagePath: String?,
    val availableLocal: Boolean,
    val isPersonalIngredient: Boolean
)
