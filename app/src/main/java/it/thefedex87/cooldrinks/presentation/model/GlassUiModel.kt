package it.thefedex87.cooldrinks.presentation.model

enum class GlassUiModel(val valueStr: String) {
    NONE("None"),
    HIGHBALL_GLASS("Highball glass"),
    COCKTAIL_GLASS("Cocktail glass"),
    OLD_FASCHIONED_GLASS("Old-fashioned glass"),
    COLLIN_GLASS("Collins glass"),
    WHISKEY_GLASS("Whiskey Glass"),
    POUSSE_CAFE_GLASS("Pousse cafe glass"),
    CHAMPAGNE_FLUTE("Champagne flute"),
    WHISKEY_SOUR_GLASS("Whiskey sour glass"),
    CORDIAL_GLASS("Cordial glass"),
    BRANDY_SNIFTER("Brandy snifter"),
    WHITE_WINE_GLASS("White wine glass"),
    NICK_AND_NORA_GLASS("Nick and Nora Glass"),
    HURRICANE_GLASS("Hurricane glass"),
    COFFEE_MUG("Coffee mug"),
    SHOT_GLASS("Shot glass"),
    JAR("Jar"),
    IRISH_COFFEE_CUP("Irish coffee cup"),
    PUNCH_BOWL("Punch bowl"),
    PITCHER("Pitcher"),
    PINT_GLASS("Pint glass"),
    COPPER_MUG("Copper Mug"),
    WINE_GLASS("Wine Glass"),
    BEER_MUG("Beer mug"),
    MARGARITA_COUPETTE_GLASS("Margarita/Coupette glass"),
    BEER_PILSNER("Beer pilsner"),
    BEER_GLASS("Beer Glass"),
    MASON_JAR("Mason jar"),
    MARGARITA_GLASS("Margarita glass"),
    MARTINI_GLASS("Martini Glass"),
    BALLOON_GLASS("Balloon Glass"),
    COUPE_GLASS("Coupe Glass");

    companion object {
        infix fun from(value: String): GlassUiModel? = values().firstOrNull { it.valueStr == value }
    }
}