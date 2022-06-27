package it.thefedex87.cooldrinks.presentation.search_drink

import androidx.compose.runtime.MutableState
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel

data class SearchDrinkState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showSearchHint: Boolean = true,
    val showNoDrinkFound: Boolean = false,
    val foundDrinks: MutableList<MutableState<DrinkUiModel>> = mutableListOf(),

    val alcoholMenuExpanded: Boolean = false,
    val alcoholFilter: AlcoholFilter = AlcoholFilter.NONE
)

enum class AlcoholFilter{
    NONE,
    ALCOHOLIC,
    NOT_ALCOHOLIC;

    override fun toString(): String {
        return when(this) {
            NONE -> "None"
            ALCOHOLIC -> "Alcoholic"
            NOT_ALCOHOLIC -> "Not Alcoholic"
        }
    }
}