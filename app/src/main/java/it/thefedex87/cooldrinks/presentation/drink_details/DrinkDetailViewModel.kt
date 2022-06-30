package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrinkDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
) : ViewModel() {

    var state by mutableStateOf(DrinkDetailState())
        private set

    init {
        viewModelScope.launch {
            val drinkId = savedStateHandle.get<Int>("drinkId")!!
            val drink = repository.getFavoriteDrink(drinkId)
            state = state.copy(
                drinkImagePath = drink.drinkThumb,
                drinkDescription = drink.d
            )
        }
    }
}