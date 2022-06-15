package it.thefedex87.cooldrinks.domain.model

import java.time.LocalDate

data class DrinkDetailDomainModel(
    val idDrink: Int,
    val isAlcoholic: Boolean,
    val category: String,
    val name: String,
    val drinkThumb: String,
    val glass: String,
    val ingredients: List<String?>,
    val instructions: String,
    val measures: List<String?>,
    val addedDate: LocalDate? = null
)