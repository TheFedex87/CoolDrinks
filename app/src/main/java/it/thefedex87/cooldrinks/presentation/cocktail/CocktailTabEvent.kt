package it.thefedex87.cooldrinks.presentation.cocktail

sealed class CocktailTabEvent {
    data class OnTabClicked(val index: Int) : CocktailTabEvent()
    data class OnPagerScrolled(val index: Int) : CocktailTabEvent()
}