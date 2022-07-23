package it.thefedex87.cooldrinks.presentation.ingredients

import android.nfc.Tag
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IngredientsViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(IngredientsState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var loadingIngredientInfoJob: Job? = null

    init {
        fetchIngredients()
    }

    fun onEvent(event: IngredientsEvent) {
        viewModelScope.launch {
            when (event) {
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
                is IngredientsEvent.RetryFetchIngredients -> {
                    fetchIngredients()
                }
            }
        }
    }

    private fun fetchIngredients() {
        state = state.copy(
            isLoading = true,
            showRetryButton = false
        )
        viewModelScope.launch {
            repository.getIngredients()
                .onSuccess {
                    Log.d(TAG, "GetIngredients success")
                    state = state.copy(
                        isLoading = false,
                        ingredients = it,
                        showRetryButton = false
                    )
                }
                .onFailure {
                    Log.d(TAG, "GetIngredients error")
                    _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.generic_error)))
                    state = state.copy(
                        isLoading = false,
                        showRetryButton = true
                    )
                }
        }
    }
}