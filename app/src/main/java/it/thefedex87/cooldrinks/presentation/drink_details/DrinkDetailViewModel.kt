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
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
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

    private lateinit var drinkDetails: DrinkDetailDomainModel

    //private var collectFavoriteDrinkJob: Job? = null

    init {
        viewModelScope.launch {
            val drinkId = savedStateHandle.get<Int>("drinkId")
            Log.d(TAG, "Received id drink: $drinkId")
            collectFavoriteDrink(drinkId)
        }
    }

    private fun collectFavoriteDrink(drinkId: Int?) {
        //collectFavoriteDrinkJob?.cancel()
        /*collectFavoriteDrinkJob = */viewModelScope.launch {
            if (drinkId != null) {
                val drink = repository.getFavoriteDrink(drinkId).first()
                if (drink != null) {
                    drinkDetails = drink
                    val ingredients = drinkDetails.ingredients.mapIndexedNotNull { index, s ->
                        Pair(s!!, drinkDetails.measures[index] ?: "")
                    }
                    state = state.copy(
                        drinkImagePath = drink.drinkThumb,
                        drinkName = drink.name,
                        drinkIngredients = ingredients,
                        drinkCategory = drink.category,
                        drinkGlass = drink.glass,
                        drinkInstructions = drink.instructions,
                        drinkAlcoholic = if (drink.isAlcoholic) "Alcoholic" else "Non Alcoholic",
                        showConfirmRemoveFavoriteDialog = false,
                        isFavorite = true
                    )
                } else {
                    getDrinkDetails(drinkId = drinkId)
                }

            } else {
                getDrinkDetails(drinkId = drinkId)
            }
        }
    }

    private suspend fun getDrinkDetails(drinkId: Int?) {
        state = state.copy(isLoading = true)
        repository
            .getDrinkDetails(drinkId)
            .onSuccess {
                drinkDetails = it.first()
                val ingredients = drinkDetails.ingredients.mapIndexedNotNull { index, s ->
                    s?.let { i ->
                        Pair(i, drinkDetails.measures[index] ?: "")
                    }
                }

                val isFavorite = if (drinkId == null) {
                    val favorite = repository.getFavoriteDrink(drinkDetails.idDrink).first()
                    favorite != null
                } else false

                state = state.copy(
                    isLoading = false,
                    drinkImagePath = drinkDetails.drinkThumb,
                    drinkName = drinkDetails.name,
                    drinkIngredients = ingredients,
                    drinkInstructions = drinkDetails.instructions,
                    drinkCategory = drinkDetails.category,
                    drinkGlass = drinkDetails.glass,
                    drinkAlcoholic = if (drinkDetails.isAlcoholic) "Alcoholic" else "Non Alcoholic",
                    showConfirmRemoveFavoriteDialog = false,
                    isFavorite = isFavorite
                )
            }
    }

    fun onEvent(event: DrinkDetailEvent) {
        viewModelScope.launch {
            when (event) {
                is DrinkDetailEvent.FavoriteClicked -> {
                    if (state.isFavorite == true) {
                        state = state.copy(showConfirmRemoveFavoriteDialog = true)
                    } else {
                        withContext(Dispatchers.IO) {
                            repository.insertIntoFavorite(drinkDetails)
                            state = state.copy(isFavorite = true)
                        }
                    }
                }
                is DrinkDetailEvent.RemoveFromFavoriteConfirmed -> {
                    withContext(Dispatchers.IO) {
                        repository.removeFromFavorite(drinkDetails.idDrink)
                    }
                    state = state.copy(
                        isFavorite = false,
                        showConfirmRemoveFavoriteDialog = false
                    )
                }
                is DrinkDetailEvent.RemoveFromFavoriteCanceled -> {
                    state = state.copy(showConfirmRemoveFavoriteDialog = false)
                }
                is DrinkDetailEvent.GetRandomCocktail -> {
                    getDrinkDetails(null)
                }
            }
        }
    }
}