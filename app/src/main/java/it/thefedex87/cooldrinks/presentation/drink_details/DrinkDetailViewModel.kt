package it.thefedex87.cooldrinks.presentation.drink_details

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.util.UiText
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
            val dominantColor = savedStateHandle.get<Int>("dominantColor")
            Log.d(TAG, "Received id drink: $drinkId")
            collectFavoriteDrink(drinkId, dominantColor)
        }
    }

    private fun collectFavoriteDrink(drinkId: Int?, dominantColor: Int?) {
        //collectFavoriteDrinkJob?.cancel()
        /*collectFavoriteDrinkJob = */viewModelScope.launch {
            if (drinkId != null) {
                val drink = repository.getFavoriteDrink(drinkId).first()
                if (drink != null) {
                    drinkDetails = drink
                    state = state.copy(
                        drinkImagePath = drink.drinkThumb,
                        drinkName = drink.name,
                        drinkIngredients = drink.ingredients,
                        drinkCategory = drink.category,
                        drinkGlass = drink.glass,
                        drinkInstructions = drink.instructions,
                        drinkAlcoholic = if (drink.isAlcoholic) UiText.StringResource(R.string.alcoholic) else UiText.StringResource(R.string.non_alcoholic),
                        showConfirmRemoveFavoriteDialog = false,
                        isFavorite = true,
                        drinkDominantColor = drink.dominantColor
                    )
                } else {
                    getDrinkDetails(drinkId = drinkId, dominantColor)
                }

            } else {
                getDrinkDetails(drinkId = drinkId, dominantColor)
            }
        }
    }

    private suspend fun getDrinkDetails(drinkId: Int?, dominantColor: Int?) {
        state = state.copy(isLoading = true)
        repository
            .getDrinkDetails(drinkId)
            .onSuccess {
                drinkDetails = it.first().copy(dominantColor = dominantColor)

                val isFavorite = if (drinkId == null) {
                    val favorite = repository.getFavoriteDrink(drinkDetails.idDrink).first()
                    favorite != null
                } else false

                state = state.copy(
                    isLoading = false,
                    drinkImagePath = drinkDetails.drinkThumb,
                    drinkName = drinkDetails.name,
                    drinkIngredients = drinkDetails.ingredients,
                    drinkInstructions = drinkDetails.instructions,
                    drinkCategory = drinkDetails.category,
                    drinkGlass = drinkDetails.glass,
                    drinkAlcoholic = if (drinkDetails.isAlcoholic) UiText.StringResource(R.string.alcoholic) else UiText.StringResource(R.string.non_alcoholic),
                    showConfirmRemoveFavoriteDialog = false,
                    isFavorite = isFavorite,
                    drinkDominantColor = dominantColor
                )
                Log.d(TAG, "Is favorite: $isFavorite")
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
                    getDrinkDetails(null, null)
                }
                is DrinkDetailEvent.DrawableLoaded -> {
                    calcDominantColor(event.drawable)
                }
            }
        }
    }

    private fun calcDominantColor(drawable: Drawable) {
        it.thefedex87.cooldrinks.presentation.util.calcDominantColor(
            drawable,
            null
        ) { color ->
            state = state.copy(drinkDominantColor = color.toArgb())
            drinkDetails = drinkDetails.copy(dominantColor = color.toArgb())
        }
    }
}