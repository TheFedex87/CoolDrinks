package it.thefedex87.cooldrinks.presentation.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel

fun calcDominantColor(
    drawable: Drawable,
    drink: MutableState<DrinkUiModel>?,
    onFinish: (Color) -> Unit
) {
    val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
    Palette.from(bmp).generate { palette ->
        palette?.dominantSwatch?.rgb?.let { colorValue ->
            drink?.let {
                drink.value = drink.value.copy(dominantColor = colorValue)
            }
            onFinish(Color(colorValue))
        }
    }
}