package it.thefedex87.cooldrinks.presentation.model

enum class CategoryUiModel(val valueStr: String) {
    NONE("None"),
    ORDINARY_DRINK("Ordinary Drink"),
    COCKTAIL("Cocktail"),
    SHAKE("Shake"),
    OTHER_UNKNOWN("Other/Unknown"),
    COCOA("Cocoa"),
    SHOT("Shot"),
    COFFEE_TEA("Coffee / Tea"),
    HOMEMADE_LIQUEUR("Homemade Liqueur"),
    PUNCH_PARTY_DRINK("Punch / Party Drink"),
    BEER("Beer"),
    SOFT_DRINK("Soft Drink");

    companion object {
        infix fun from(value: String): CategoryUiModel? = CategoryUiModel.values().firstOrNull { it.valueStr == value }
    }
}