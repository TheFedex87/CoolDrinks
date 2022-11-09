package it.thefedex87.cooldrinks.presentation.favorite_drink

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavoriteDrinkViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(FavoriteDrinkState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.favoritesDrinks.collect {
                val glass = mutableListOf(GlassUiModel.NONE)
                glass.addAll(it.mapNotNull { g -> GlassUiModel from g.glass }.distinct())

                val categories = mutableListOf(CategoryUiModel.NONE)
                categories.addAll(it.mapNotNull { g -> CategoryUiModel from g.category }.distinct())

                state = state.copy(
                    drinks = filterDrinks(
                        alcoholFilter = state.alcoholFilter,
                        categoryUiModel = state.categoryUiModel,
                        glassUiModel = state.glassUiModel,
                        drinks = it
                    ),
                    showFilterChips = it.isNotEmpty(),
                    glasses = glass,
                    categories = categories
                )
            }
        }
    }

    fun onEvent(event: FavoriteDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is FavoriteDrinkEvent.UnfavoriteClicked -> {
                    state = state.copy(
                        showConfirmRemoveFavoriteDialog = true,
                        drinkToRemove = event.drink
                    )
                }
                is FavoriteDrinkEvent.RemoveFromFavoriteConfirmed -> {
                    try {
                        withContext(Dispatchers.IO) {
                            repository.deleteOrRemoveFromFavorite(event.drink.idDrink)
                        }
                        state = state.copy(
                            showConfirmRemoveFavoriteDialog = false,
                            drinkToRemove = null
                        )
                        _uiEvent.send(
                            UiEvent.ShowSnackBar(
                                UiText.StringResource(
                                    R.string.drink_removed_from_favorite
                                )
                            )
                        )
                    } catch (ex: Exception) {
                        _uiEvent.send(
                            UiEvent.ShowSnackBar(
                                UiText.StringResource(
                                    R.string.drink_remove_error_from_favorite
                                )
                            )
                        )
                    }
                }
                is FavoriteDrinkEvent.RemoveFromFavoriteCanceled -> {
                    state = state.copy(
                        showConfirmRemoveFavoriteDialog = false,
                        drinkToRemove = null
                    )
                }
                is FavoriteDrinkEvent.AlcoholFilterValueChanged -> {
                    Log.d(TAG, "Alcohol filter ${event.filter}")
                    state = state.copy(
                        alcoholFilter = event.filter,
                        alcoholMenuExpanded = false,
                        drinks = filterDrinks(
                            alcoholFilter = event.filter,
                            categoryUiModel = state.categoryUiModel,
                            glassUiModel = state.glassUiModel,
                            drinks = repository.favoritesDrinks.first()
                        )
                    )
                }
                is FavoriteDrinkEvent.CollapseAlcoholMenu -> {
                    state = state.copy(alcoholMenuExpanded = false)
                }
                is FavoriteDrinkEvent.ExpandeAlcoholMenu -> {
                    state = state.copy(alcoholMenuExpanded = true)
                }

                is FavoriteDrinkEvent.GlassFilterValueChanged -> {
                    Log.d(TAG, "Glass filter ${event.filter}")
                    state = state.copy(
                        glassUiModel = event.filter,
                        glassMenuExpanded = false,
                        drinks = filterDrinks(
                            alcoholFilter = state.alcoholFilter,
                            categoryUiModel = state.categoryUiModel,
                            glassUiModel = event.filter,
                            drinks = repository.favoritesDrinks.first()
                        )
                    )
                }
                is FavoriteDrinkEvent.CollapseGlassMenu -> {
                    state = state.copy(glassMenuExpanded = false)
                }
                is FavoriteDrinkEvent.ExpandeGlassMenu -> {
                    state = state.copy(glassMenuExpanded = true)
                }

                is FavoriteDrinkEvent.CategoryFilterValueChanged -> {
                    Log.d(TAG, "Category filter ${event.filter}")
                    state = state.copy(
                        categoryUiModel = event.filter,
                        categoryMenuExpanded = false,
                        drinks = filterDrinks(
                            alcoholFilter = state.alcoholFilter,
                            categoryUiModel = event.filter,
                            glassUiModel = state.glassUiModel,
                            drinks = repository.favoritesDrinks.first()
                        )
                    )
                }
                is FavoriteDrinkEvent.CollapseCategoryMenu -> {
                    state = state.copy(categoryMenuExpanded = false)
                }
                is FavoriteDrinkEvent.ExpandeCategoryMenu -> {
                    state = state.copy(categoryMenuExpanded = true)
                }
            }
        }
    }

    private fun filterDrinks(
        alcoholFilter: AlcoholFilter,
        categoryUiModel: CategoryUiModel,
        glassUiModel: GlassUiModel,
        drinks: List<DrinkDetailDomainModel>
    ): List<DrinkDetailDomainModel> {
        val filteredDrinks = drinks.filter {
            it.isAlcoholic == (if (alcoholFilter == AlcoholFilter.NONE) it.isAlcoholic else alcoholFilter == AlcoholFilter.ALCOHOLIC) &&
                    it.glass.lowercase() == (if (glassUiModel == GlassUiModel.NONE) it.glass.lowercase() else glassUiModel.valueStr
                .lowercase()) &&
                    it.category.lowercase() == (if (categoryUiModel == CategoryUiModel.NONE) it.category.lowercase() else categoryUiModel.valueStr
                .lowercase())
        }

        return filteredDrinks
    }
}