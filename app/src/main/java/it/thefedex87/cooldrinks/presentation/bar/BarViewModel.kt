package it.thefedex87.cooldrinks.presentation.bar

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class BarViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(BarState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

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
                    state = state.copy(selectedIngredient = event.ingredient,
                        selectedPage = event.page,
                        selectedOption = if(event.ingredient.availableLocal) 0 else 1)
                }
                is BarEvent.JumpToStoredIngredient -> {
                    val page = state.ingredients.indexOfFirst { it.name == event.name }
                    if(page >= 0 && page <= state.ingredients.lastIndex) {
                        _uiEvent.send(UiEvent.ScrollPagerToPage(page))
                    }
                }
                is BarEvent.OnDeleteIconClicked -> {
                    state = state.copy(
                        showRemoveElementDialog = true,
                        elementToRemove = event.ingredient
                    )
                }
                is BarEvent.OnDeleteConfirmClicked -> {
                    try {
                        state = state.copy(
                            showRemoveElementDialog = false,
                            elementToRemove = null
                        )
                        val imagePath = event.ingredient.imagePath
                        repository.deleteIngredient(
                            event.ingredient
                        )
                        if(!imagePath.isNullOrEmpty()) {
                            try {
                                File(imagePath).delete()
                            } catch (ex:Exception) {
                                Log.d(TAG, "Error removing image associated with ingredient")
                                ex.printStackTrace()
                            }
                        }
                    } catch (ex: Exception) {
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.DynamicString(ex.message!!)))
                    }
                }
                is BarEvent.OnRemoveCanceled -> {
                    state = state.copy(
                        showRemoveElementDialog = false,
                        elementToRemove = null
                    )
                }
            }
        }
    }

    private fun setupObservers() {
        viewModelScope.launch {
            repository.storedLiquors.collect {
                Log.d(TAG, "Collect liquor")
                val selectedIngredient = if (state.selectedPage != null && it.isNotEmpty()) {
                    if(state.selectedPage!! <= it.lastIndex)
                        it[state.selectedPage!!]
                    else
                        it.last()
                } else null

                state = state.copy(
                    ingredients = it,
                    selectedIngredient = selectedIngredient
                )
            }
        }
    }
}