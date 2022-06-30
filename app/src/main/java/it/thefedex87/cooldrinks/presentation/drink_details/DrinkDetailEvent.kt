package it.thefedex87.cooldrinks.presentation.drink_details

sealed class DrinkDetailEvent {
    object FavoriteClicked : DrinkDetailEvent()
    object RemoveFromFavoriteConfirmed: DrinkDetailEvent()
    object RemoveFromFavoriteCanceled: DrinkDetailEvent()
}