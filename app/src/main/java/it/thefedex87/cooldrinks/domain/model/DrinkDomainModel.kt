package it.thefedex87.cooldrinks.domain.model

data class DrinkDomainModel(
    val name: String,
    val image: String,
    val id: Int,
    val isFavorite: Boolean
)