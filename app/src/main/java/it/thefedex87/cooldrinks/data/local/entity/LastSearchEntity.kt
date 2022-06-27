package it.thefedex87.cooldrinks.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LastSearchEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val image: String,
    val isFavorite: Boolean
)
