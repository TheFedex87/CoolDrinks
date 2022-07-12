package it.thefedex87.cooldrinks.domain.model

data class IngredientDetailsDomainModel(
    val id: Int,
    val name: String,
    val description: String?,
    val type: String,
    val alcoholic: Boolean
)
