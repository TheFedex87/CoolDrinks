package it.thefedex87.cooldrinks.presentation.ingredients

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(IngredientsState())
        private set

    private var loadingIngredientInfoJob: Job? = null

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
                    state = state.copy(
                        showDetailOfIngredient = null,
                        getIngredientInfoError = null,
                        isLoadingIngredientInfo = false,
                        ingredientInfo = null
                    )
                }
                is IngredientsEvent.ShowIngredientsDetails -> {
                    state = state.copy(
                        showDetailOfIngredient = event.ingredient,
                        isLoadingIngredientInfo = true
                    )
                    loadingIngredientInfoJob?.cancel()
                    loadingIngredientInfoJob = launch {
                        repository.getIngredientDetails(event.ingredient)
                            .onSuccess {
                                state = state.copy(
                                    isLoadingIngredientInfo = false,
                                    ingredientInfo = it
                                )
                            }
                            .onFailure {
                                state = state.copy(
                                    isLoadingIngredientInfo = false,
                                    getIngredientInfoError = it.message
                                )
                            }
                    }
                }
            }
        }
    }
}