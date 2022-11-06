package it.thefedex87.cooldrinks.presentation.my_drink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
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
            repository.appPreferencesManager.onEach { appPref ->
                _state.update {
                    _state.value.copy(
                        visualizationType = appPref.visualizationType
                    )
                }
            }.launchIn(this)

            _state.update {
                _state.value.copy(
                    isLoading = true
                )
            }

            repository.myDrinks.collect { drinks ->
                _state.update {
                    _state.value.copy(
                        isLoading = false,
                        drinks = drinks
                    )
                }
            }
        }
    }
}