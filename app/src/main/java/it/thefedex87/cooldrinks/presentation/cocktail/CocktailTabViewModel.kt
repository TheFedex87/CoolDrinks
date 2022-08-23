package it.thefedex87.cooldrinks.presentation.cocktail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CocktailTabViewModel @Inject constructor() : ViewModel() {
    var state by mutableStateOf(CocktailTabState())
        private set

    fun onEvent(event: CocktailTabEvent) {
        viewModelScope.launch {
            when(event) {
                is CocktailTabEvent.OnPagerScrolled -> {
                    state = state.copy(selectedTab = event.index)
                }
                is CocktailTabEvent.OnTabClicked -> {
                    state = state.copy(selectedTab = event.index)
                }
            }
        }
    }
}