package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel

data class FavoriteDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList(),
    val glasses: List<String> = emptyList(),
    val categories: List<String> = emptyList(),

    val alcoholMenuExpanded: Boolean = false,
    val alcoholFilter: AlcoholFilter = AlcoholFilter.NONE,

    val categoryMenuExpanded: Boolean = false,
    val categoryFilter: CategoryFilter = CategoryFilter.NONE,

    val glassMenuExpanded: Boolean = false,
    val glassFilter: GlassFilter = GlassFilter.NONE
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

enum class GlassFilter {
    NONE,
    HIGHBALL_GLASS,
    COCKTAIL_GLASS,
    OLD_FASCHIONED_GLASS,
    COLLIN_GLASS,
    WHISKEY_GLASS,
    POUSSE_CAFE_GLASS,
    CHAMPAGNE_FLUTE,
    WHISKEY_SOUR_GLASS,
    CORDIAL_GLASS,
    BRANDY_SNIFTER,
    WHITE_WINE_GLASS,
    NICK_AND_NORA_GLASS,
    HURRICANE_GLASS,
    COFFEE_MUG,
    SHOT_GLASS,
    JAR,
    IRISH_COFFEE_CUP,
    PUNCH_BOWL,
    PITCHER,
    PINT_GLASS,
    COPPER_MUG,
    WINE_GLASS,
    BEER_MUG,
    MARGARITA_COUPETTE_GLASS,
    BEER_PILSNER,
    BEER_GLASS,
    MASON_JAR,
    MARGARITA_GLASS,
    MARTINI_GLASS,
    BALLOON_GLASS,
    COUPE_GLASS;

    companion object {
        fun toEnum(value: String): GlassFilter {
            return when (value) {
                "Glass" -> NONE
                "Highball glass" -> HIGHBALL_GLASS
                "Cocktail glass" -> COCKTAIL_GLASS
                "Old-fashioned glass" -> OLD_FASCHIONED_GLASS
                "Collins Glass" -> COLLIN_GLASS
                "Whiskey Glass" -> WHISKEY_GLASS
                "Pousse cafe glass" -> POUSSE_CAFE_GLASS
                "Champagne flute" -> CHAMPAGNE_FLUTE
                "Whiskey sour glass" -> WHISKEY_SOUR_GLASS
                "Cordial glass" -> CORDIAL_GLASS
                "Brandy snifter" -> BRANDY_SNIFTER
                "White wine glass" -> WHITE_WINE_GLASS
                "Nick and Nora Glass" -> NICK_AND_NORA_GLASS
                "Hurricane glass" -> HURRICANE_GLASS
                "Coffee mug" -> COFFEE_MUG
                "Shot glass" -> SHOT_GLASS
                "Jar" -> JAR
                "Irish coffee cup" -> IRISH_COFFEE_CUP
                "Punch bowl" -> PUNCH_BOWL
                "Pitcher" -> PITCHER
                "Pint glass" -> PINT_GLASS
                "Copper Mug" -> COPPER_MUG
                "Wine Glass" -> WINE_GLASS
                "Beer mug" -> BEER_MUG
                "Margarita/Coupette glass" -> MARGARITA_COUPETTE_GLASS
                "Beer pilsner" -> BEER_PILSNER
                "Beer Glass" -> BEER_GLASS
                "Mason jar" -> MASON_JAR
                "Margarita glass" -> MARGARITA_GLASS
                "Martini Glass" -> MARTINI_GLASS
                "Balloon Glass" -> BALLOON_GLASS
                "Coupe Glass" -> COUPE_GLASS
                else -> NONE
            }
        }
    }

    override fun toString(): String {
        return when (this) {
            NONE -> "Glass"
            HIGHBALL_GLASS -> "Highball glass"
            COCKTAIL_GLASS -> "Cocktail glass"
            OLD_FASCHIONED_GLASS -> "Old-fashioned glass"
            COLLIN_GLASS -> "Collins glass"
            WHISKEY_GLASS -> "Whiskey Glass"
            POUSSE_CAFE_GLASS -> "Pousse cafe glass"
            CHAMPAGNE_FLUTE -> "Champagne flute"
            WHISKEY_SOUR_GLASS -> "Whiskey sour glass"
            CORDIAL_GLASS -> "Cordial glass"
            BRANDY_SNIFTER -> "Brandy snifter"
            WHITE_WINE_GLASS -> "White wine glass"
            NICK_AND_NORA_GLASS -> "Nick and Nora Glass"
            HURRICANE_GLASS -> "Hurricane glass"
            COFFEE_MUG -> "Coffee mug"
            SHOT_GLASS -> "Shot glass"
            JAR -> "Jar"
            IRISH_COFFEE_CUP -> "Irish coffee cup"
            PUNCH_BOWL -> "Punch bowl"
            PITCHER -> "Pitcher"
            PINT_GLASS -> "Pint glass"
            COPPER_MUG -> "Copper Mug"
            WINE_GLASS -> "Wine Glass"
            BEER_MUG -> "Beer mug"
            MARGARITA_COUPETTE_GLASS -> "Margarita/Coupette glass"
            BEER_PILSNER -> "Beer pilsner"
            BEER_GLASS -> "Beer Glass"
            MASON_JAR -> "Mason jar"
            MARGARITA_GLASS -> "Margarita glass"
            MARTINI_GLASS -> "Martini Glass"
            BALLOON_GLASS -> "Balloon Glass"
            COUPE_GLASS -> "Coupe Glass"
        }
    }
}