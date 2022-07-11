package it.thefedex87.cooldrinks.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IngredientEntity(
    @PrimaryKey
    val name: String
)