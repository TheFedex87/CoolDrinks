package it.thefedex87.cooldrinks.presentation.drink_details

import android.graphics.drawable.Drawable

sealed class DrinkDetailEvent {
    object FavoriteClicked : DrinkDetailEvent()
    object RemoveFromFavoriteConfirmed: DrinkDetailEvent()
    object RemoveFromFavoriteCanceled: DrinkDetailEvent()
    object GetRandomCocktail: DrinkDetailEvent()
    data class DrawableLoaded(val drawable: Drawable): DrinkDetailEvent()
}