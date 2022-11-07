package it.thefedex87.cooldrinks.presentation.my_drink

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyDrinkViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MyDrinkState())
    val state = _state.asStateFlow()


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
                    if(event.drink.isFavorite) {
                        repository.deleteOrRemoveFromFavorite(event.drink.idDrink)
                    } else {
                        repository.insertIntoFavorite(event.drink)
                    }
                }
            }
        }
    }
}