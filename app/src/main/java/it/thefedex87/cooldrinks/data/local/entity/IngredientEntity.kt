package it.thefedex87.cooldrinks.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class IngredientEntity(
    val id: Int?,
    @PrimaryKey
    val name: String,
    val description: String?,
    val imagePath: String?,
    val type: String,
    val alcoholic: Boolean,
    val availableLocal: Boolean,
    @ColumnInfo(defaultValue = "false") val isPersonalIngredient: Boolean // If true rapresent a custom ingredient added by user
)