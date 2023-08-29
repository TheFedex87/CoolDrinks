package it.thefedex87.cooldrinks.presentation.my_drink

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyDrinkViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MyDrinkState())
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            _state.update {
                _state.value.copy(
                    isLoading = true
                )
            }

            repository.myDrinks.collect { drinks ->
                Log.d(TAG, "Received my drinks list: $drinks")
                _state.update {
                    _state.value.copy(
                        isLoading = false,
                        drinks = drinks
                    )
                }
            }
        }
    }

    fun onEvent(event: MyDrinkEvent) {
        viewModelScope.launch {
            when(event) {
                is MyDrinkEvent.OnChangeFavoriteStateClicked -> {
                    val drink = _state.value.drinks.first { it.idDrink == event.drinkId }
                    if(drink.isFavorite) {
                        repository.deleteOrRemoveFromFavorite(drink.idDrink)
                    } else {
                        repository.insertIntoFavorite(drink)
                    }
                }
                is MyDrinkEvent.OnRemoveDrinkClicked -> {
                    val drink = _state.value.drinks.first { it.idDrink == event.drinkId }
                    _state.update {
                        _state.value.copy(
                            showConfirmRemoveDrinkDialog = true,
                            drinkToRemove = drink
                        )
                    }
                }
                is MyDrinkEvent.OnRemoveDrinkConfirmed -> {
                    _state.update {
                        _state.value.copy(
                            showConfirmRemoveDrinkDialog = false,
                            drinkToRemove = null
                        )
                    }
                    try {
                        repository.removeDrink(event.drink)
                        if(event.drink.drinkThumb.isNotEmpty()) {
                            try {
                                File(event.drink.drinkThumb).delete()
                            } catch (ex:Exception) {
                                ex.printStackTrace()
                            }
                        }
                    } catch (ex: Exception) {
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_removing_drink)))
                    }
                }
                is MyDrinkEvent.OnRemoveDrinkCanceled -> {
                    _state.update {
                        _state.value.copy(
                            showConfirmRemoveDrinkDialog = false,
                            drinkToRemove = null
                        )
                    }
                }
            }
        }
    }
}