package it.thefedex87.cooldrinks.presentation.my_drink

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
class MyDrinkViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(MyDrinkState())
        private set

    init {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            repository.myDrinks.collect {
                state = state.copy(
                    drinks = it,
                    isLoading = false
                )
            }
        }
    }
}