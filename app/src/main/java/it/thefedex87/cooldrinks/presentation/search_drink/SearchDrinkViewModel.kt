package it.thefedex87.cooldrinks.presentation.search_drink

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.mapper.toDrinkUiModel
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchDrinkViewModel @Inject constructor(
    val drinkRepository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(SearchDrinkState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event: SearchDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchDrinkEvent.OnDrinkClick -> {

                }
                is SearchDrinkEvent.OnSearchClick -> {
                    state = state.copy(isLoading = true, foundDrinks = mutableListOf())
                    searchDrink()
                }
                is SearchDrinkEvent.OnSearchQueryChange -> {
                    state = state.copy(
                        searchQuery = event.query,
                        showSearchHint = false
                    )
                }
                is SearchDrinkEvent.OnSearchFocusChange -> {
                    state =
                        state.copy(showSearchHint = !event.isFocused && state.searchQuery.isEmpty())
                }
                is SearchDrinkEvent.OnFavoriteClick -> {
                    changeFavoriteState(event.drink)
                }
            }
        }
    }

    fun calcDominantColor(
        drawable: Drawable,
        drink: DrinkUiModel,
        onFinish: (Color) -> Unit
    ) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                drink.copy(dominantColor = colorValue)
                onFinish(Color(colorValue))
            }
        }
    }

    private suspend fun searchDrink() {
        drinkRepository
            .searchCocktails(
                state.searchQuery
            )
            .onSuccess {
                state = state.copy(
                    isLoading = false,
                    searchQuery = "",
                    showSearchHint = true,
                    foundDrinks = it.map { drink -> mutableStateOf(drink.toDrinkUiModel()) }.toMutableList(),
                    showNoDrinkFound = it.isEmpty()
                )
            }
            .onFailure {
                state = state.copy(
                    isLoading = false,
                    searchQuery = "",
                    showSearchHint = true,
                    showNoDrinkFound = false
                )
                _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.search_drink_error)))
            }
    }

    private suspend fun changeFavoriteState(drink: DrinkUiModel) {
        val index = state.foundDrinks.indexOfFirst { d -> d.component1().id == drink.id }
        if(drink.isFavorite) {
            state.foundDrinks[index].value = drink.copy(isFavorite = false)
            withContext(Dispatchers.IO) {
                drinkRepository.removeFromFavorite(drink.id)
            }
        } else {
            state.foundDrinks[index].value = drink.copy(isLoadingFavorite = true)
            drinkRepository
                .getDrinkDetails(drink.id)
                .onSuccess {
                    var drinkDetail = it.first()
                    withContext(Dispatchers.IO) {
                        drinkRepository.insertIntoFavorite(drinkDetail.copy(dominantColor = drink.dominantColor))
                    }
                    state.foundDrinks[index].value = drink.copy(isLoadingFavorite = false, isFavorite = true)
                }
                .onFailure {

                }
        }
    }
}