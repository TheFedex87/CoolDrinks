package it.thefedex87.cooldrinks.presentation.search_drink.model

import android.graphics.drawable.Drawable

data class DrinkUiModel(
    val name: String,
    val image: String,
    val id: Int,
    val dominantColor: Int,
    val isFavorite: Boolean,
    val isLoadingFavorite: Boolean,
    val imageDrawable: Drawable? = null
)
