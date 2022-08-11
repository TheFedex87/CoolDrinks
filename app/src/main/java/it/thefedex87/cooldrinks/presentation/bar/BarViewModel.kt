package it.thefedex87.cooldrinks.presentation.bar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class BarViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(BarState())
        private set

    init {
        setupObservers()
    }

    fun onEvent(event: BarEvent) {
        viewModelScope.launch {
            when(event) {
                is BarEvent.SetIngredientAvailability -> {
                    try {
                        /*val i = state.ingredients.indexOfFirst { it.id == event.ingredient.id }
                        val updatedIngredient = state.ingredients[i].copy(availableLocal = event.available)
                        repository.updateIngredient(updatedIngredient)
                        state.ingredients[i] = updatedIngredient*/
                        repository.updateIngredient(event.ingredient.copy(availableLocal = event.available))
                        state = state.copy(selectedOption = if(event.available) 0 else 1)
                    } catch (ex: Exception) {

                    }
                }
                is BarEvent.SelectedIngredientChanged -> {
                    state = state.copy(selectedIngredient = event.ingredient, selectedOption = if(event.ingredient.availableLocal) 0 else 1)
                }
            }
        }
    }

    private fun setupObservers() {
        viewModelScope.launch {
            repository.storedLiquors.collect {
                Log.d(TAG, "Collect liquor")
                state = state.copy(
                    ingredients = it
                )
            }
        }
    }
}