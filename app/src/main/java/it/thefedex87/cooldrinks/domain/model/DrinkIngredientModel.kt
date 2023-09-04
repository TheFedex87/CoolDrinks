package it.thefedex87.cooldrinks.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DrinkIngredientModel(
    val name: String?,
    val measure: String?,
    val isAvailable: Boolean?
): Parcelable