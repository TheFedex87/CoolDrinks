package it.thefedex87.cooldrinks.presentation.add_my_drink

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddMyDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var state by mutableStateOf(AddMyDrinkState(
        cocktailName = savedStateHandle.get<String>("name") ?: ""
    ))
        private set

    fun onEvent(event: AddMyDrinkEvent) {
        viewModelScope.launch {
            when(event) {
                is AddMyDrinkEvent.OnMyDrinkNameChanged -> {
                    state = state.copy(cocktailName = event.name)

                    savedStateHandle.set("name", event.name)
                }
            }
        }
    }
}