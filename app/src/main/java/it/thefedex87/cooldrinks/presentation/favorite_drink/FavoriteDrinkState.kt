package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel

data class FavoriteDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList(),
    val glasses: List<GlassUiModel> = emptyList(),
    val categories: List<String> = emptyList(),

    val showFilterChips: Boolean = false,

    val showConfirmRemoveFavoriteDialog: Boolean = false,
    val drinkToRemove: DrinkDetailDomainModel? = null,

    val alcoholMenuExpanded: Boolean = false,
    val alcoholFilter: AlcoholFilter = AlcoholFilter.NONE,

    val categoryMenuExpanded: Boolean = false,
    val categoryFilter: CategoryFilter = CategoryFilter.NONE,

    val glassMenuExpanded: Boolean = false,
    val glassUiModel: GlassUiModel = GlassUiModel.NONE
)

enum class AlcoholFilter {
    NONE,
    ALCOHOLIC,
    NOT_ALCOHOLIC;

    override fun toString(): String {
        return when (this) {
            NONE -> "Alcohol"
            ALCOHOLIC -> "Alcoholic"
            NOT_ALCOHOLIC -> "Non Alcoholic"
        }
    }
}

enum class CategoryFilter {
    NONE,
    ORDINARY_DRINK,
    COCKTAIL,
    SHAKE,
    OTHER_UNKNOWN,
    COCOA,
    SHOT,
    COFFEE_TEA,
    HOMEMADE_LIQUEUR,
    PUNCH_PARTY_DRINK,
    BEER,
    SOFT_DRINK;

    companion object {
        fun toEnum(value: String): CategoryFilter {
            return when (value) {
                "Category" -> NONE
                "Ordinary Drink" -> ORDINARY_DRINK
                "Cocktail" -> COCKTAIL
                "Shake" -> SHAKE
                "Other/Unknown" -> OTHER_UNKNOWN
                "Cocoa" -> COCOA
                "Shot" -> SHOT
                "Coffee / Tea" -> COFFEE_TEA
                "Homemade Liqueur" -> HOMEMADE_LIQUEUR
                "Punch / Party Drink" -> PUNCH_PARTY_DRINK
                "Beer" -> BEER
                "Soft Drink" -> SOFT_DRINK
                else -> NONE
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            NONE -> "Category"
            ORDINARY_DRINK -> "Ordinary Drink"
            COCKTAIL -> "Cocktail"
            SHAKE -> "Shake"
            OTHER_UNKNOWN -> "Other/Unknown"
            COCOA -> "Cocoa"
            SHOT -> "Shot"
            COFFEE_TEA -> "Coffee / Tea"
            HOMEMADE_LIQUEUR -> "Homemade Liqueur"
            PUNCH_PARTY_DRINK -> "Punch / Party Drink"
            BEER -> "Beer"
            SOFT_DRINK -> "Soft Drink"
        }
    }
}