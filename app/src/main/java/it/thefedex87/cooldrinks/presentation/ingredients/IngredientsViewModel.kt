package it.thefedex87.cooldrinks.presentation.ingredients

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(IngredientsState())
        private set

    init {
        state = state.copy(isLoading = true)
        viewModelScope.launch {
            repository.getIngredients()
                .onSuccess {
                    state = state.copy(
                        isLoading = false,
                        ingredients = it
                    )
                }
                .onFailure {
                    state = state.copy(isLoading = false)
                }
        }
    }

    fun onEvent(event: IngredientsEvent) {
        viewModelScope.launch {
            when(event) {
                is IngredientsEvent.HideIngredientsDetails -> {
                    state = state.copy(showIngredientDetails = false)
                }
                is IngredientsEvent.ShowIngredientsDetails -> {
                    state = state.copy(showIngredientDetails = true)
                }
            }
        }
    }
}