package it.thefedex87.cooldrinks.presentation.search_drink

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.mapper.toDrinkUiModel
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchDrinkViewModel @Inject constructor(
    private val drinkRepository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(SearchDrinkState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        setupObservers()
    }

    private fun setupObservers() {
        drinkRepository.appPreferencesManager.onEach {
            state = state.copy(visualizationType = it.visualizationType)
        }.launchIn(viewModelScope)

        drinkRepository.favoritesDrinks.onEach { favorites ->
            Log.d(TAG, "Update of favorites")
            // If a drink has been added to favorites inside the screen details update also the UI in search screen
            favorites.forEach {  favorite ->
                val index = state.foundDrinks.indexOfFirst { d -> d.value.id == favorite.idDrink && !d.value.isFavorite }
                if(index >= 0) {
                    state.foundDrinks[index].value = state.foundDrinks[index].value.copy(isFavorite = true)
                }
            }

            // If a drink has been removed from favorites inside the screen details update also the UI in search screen
            val indexToRemoveFromFavorites = mutableListOf<Int>()
            state.foundDrinks.filter { it.value.isFavorite }.forEach { drink ->
                if(!favorites.any { fd -> fd.idDrink == drink.value.id }) {
                    indexToRemoveFromFavorites.add(state.foundDrinks.indexOf(drink))
                }
            }
            indexToRemoveFromFavorites.forEach { i ->
                state.foundDrinks[i].value = state.foundDrinks[i].value.copy(isFavorite = false)
            }
        }.launchIn(viewModelScope)
    }

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
                is SearchDrinkEvent.OnVisualizationTypeChange -> {
                    drinkRepository.updateVisualizationType(event.visualizationType)
                }
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
                    foundDrinks = it.map { drink -> mutableStateOf(drink.toDrinkUiModel()) }
                        .toMutableList(),
                    showNoDrinkFound = it.isEmpty()
                )
                if (it.isEmpty()) {
                    _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.no_drink_found)))
                }
            }
            .onFailure {
                if(it is CancellationException) {
                    throw it
                }
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
        if (drink.isFavorite) {
            state.foundDrinks[index].value = drink.copy(isFavorite = false)
            withContext(Dispatchers.IO) {
                drinkRepository.removeFromFavorite(drink.id)
            }
        } else {
            state.foundDrinks[index].value = drink.copy(isLoadingFavorite = true)
            drinkRepository
                .getDrinkDetails(drink.id)
                .onSuccess {
                    val drinkDetail = it.first()
                    withContext(Dispatchers.IO) {
                        drinkRepository.insertIntoFavorite(drinkDetail.copy(dominantColor = drink.dominantColor))
                    }
                    state.foundDrinks[index].value =
                        drink.copy(isLoadingFavorite = false, isFavorite = true)
                }
                .onFailure {
                    // TODO
                }
        }
    }
}