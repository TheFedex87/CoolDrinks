package it.thefedex87.cooldrinks.presentation.favorite_drink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteDrinkViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(FavoriteDrinkState())
        private set

    init {
        viewModelScope.launch {
            repository.favoritesDrinks.collect {
                state = state.copy(drinks = it)
            }
        }
    }
}