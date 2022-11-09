package it.thefedex87.cooldrinks.presentation.favorite_drink

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel

data class FavoriteDrinkState(
    val drinks: List<DrinkDetailDomainModel> = emptyList(),
    val glasses: List<GlassUiModel> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),

    val showFilterChips: Boolean = false,

    val showConfirmRemoveFavoriteDialog: Boolean = false,
    val drinkToRemove: DrinkDetailDomainModel? = null,

    val alcoholMenuExpanded: Boolean = false,
    val alcoholFilter: AlcoholFilter = AlcoholFilter.NONE,

    val categoryMenuExpanded: Boolean = false,
    val categoryUiModel: CategoryUiModel = CategoryUiModel.NONE,

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