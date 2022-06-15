package it.thefedex87.cooldrinks.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteDrinkEntity(
    @PrimaryKey
    val idDrink: Int,
    val isAlcoholic: Boolean,
    val category: String,
    val name: String,
    val drinkThumb: String,
    val glass: String,
    val ingredients: String,
    val instructions: String,
    val measures: String,
    val addedDayOfMonth: Int,
    val addedMonth: Int,
    val addedYear: Int
)