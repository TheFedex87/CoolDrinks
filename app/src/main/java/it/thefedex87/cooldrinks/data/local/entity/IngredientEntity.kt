package it.thefedex87.cooldrinks.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String,
    val description: String?,
    val imagePath: String?,
    val type: String,
    val alcoholic: Boolean,
    val availableLocal: Boolean
)