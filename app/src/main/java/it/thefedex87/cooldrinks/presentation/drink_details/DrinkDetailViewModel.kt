package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DrinkDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
) : ViewModel() {

    var state by mutableStateOf(DrinkDetailState())
        private set

    private lateinit var favoriteDrink: DrinkDetailDomainModel

    private var collectFavoriteDrinkJob: Job? = null

    init {
        viewModelScope.launch {
            val drinkId = savedStateHandle.get<Int>("drinkId")!!
            Log.d(TAG, "Received id drink: $drinkId")
            collectFavoriteDrink(drinkId)
        }
    }

    private fun collectFavoriteDrink(drinkId: Int) {
        collectFavoriteDrinkJob?.cancel()
        collectFavoriteDrinkJob = viewModelScope.launch {
            repository.getFavoriteDrink(drinkId).collect { drink ->
                if (drink != null) {
                    favoriteDrink = drink
                    state = state.copy(
                        drinkImagePath = drink.drinkThumb,
                        drinkName = drink.name,
                        drinkCategory = drink.category,
                        drinkGlass = drink.glass,
                        drinkAlcoholic = if (drink.isAlcoholic) "Alcoholic" else "Non Alcoholic",
                        showConfirmRemoveFavoriteDialog = false,
                        isFavorite = true
                    )
                } else {
                    state = state.copy(isLoading = true)
                    repository
                        .getDrinkDetails(drinkId)
                        .onSuccess {
                            favoriteDrink = it.first()
                            state = state.copy(
                                isLoading = false,
                                drinkImagePath = favoriteDrink.drinkThumb,
                                drinkName = favoriteDrink.name,
                                drinkCategory = favoriteDrink.category,
                                drinkGlass = favoriteDrink.glass,
                                drinkAlcoholic = if (favoriteDrink.isAlcoholic) "Alcoholic" else "Non Alcoholic",
                                showConfirmRemoveFavoriteDialog = false,
                                isFavorite = false
                            )
                        }
                }
            }
        }
    }

    fun onEvent(event: DrinkDetailEvent) {
        viewModelScope.launch {
            when (event) {
                is DrinkDetailEvent.FavoriteClicked -> {
                    if(state.isFavorite == true) {
                        state = state.copy(showConfirmRemoveFavoriteDialog = true)
                    } else {
                        withContext(Dispatchers.IO) {
                            repository.insertIntoFavorite(favoriteDrink)
                            state = state.copy(isFavorite = true)
                        }
                    }
                }
                is DrinkDetailEvent.RemoveFromFavoriteConfirmed -> {
                    withContext(Dispatchers.IO) {
                        repository.removeFromFavorite(favoriteDrink.idDrink)
                    }
                    state = state.copy(
                        isFavorite = false,
                        showConfirmRemoveFavoriteDialog = false
                    )
                }
                is DrinkDetailEvent.RemoveFromFavoriteCanceled -> {
                    state = state.copy(showConfirmRemoveFavoriteDialog = false)
                }
            }
        }
    }
}