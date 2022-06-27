package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel

data class FavoriteDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList(),

    val alcoholMenuExpanded: Boolean = false,
    val alcoholFilter: AlcoholFilter = AlcoholFilter.NONE
)

enum class AlcoholFilter{
    NONE,
    ALCOHOLIC,
    NOT_ALCOHOLIC;

    override fun toString(): String {
        return when(this) {
            NONE -> "Alcohol"
            ALCOHOLIC -> "Alcoholic"
            NOT_ALCOHOLIC -> "Non Alcoholic"
        }
    }
}