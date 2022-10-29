package it.thefedex87.cooldrinks.presentation.add_my_drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddMyDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddMyDrinkState(
            cocktailName = savedStateHandle.get<String>("name") ?: "",
			cocktailInstructions = savedStateHandle.get<String>("instructions") ?: "",
            cocktailGlass = savedStateHandle.get<String>("glass") ?: "",
            cocktailCategory = savedStateHandle.get<String>("category") ?: "",
            cocktailIsAlcoholic = savedStateHandle.get<Boolean>("isAlcoholic") ?: true,
            addingIngredientName = savedStateHandle.get<String>("addingIngredientName"),
            addingIngredientMeasure = savedStateHandle.get<String>("addingIngredientMeasure")
        )
    )
    val state = _state.asStateFlow()

    fun onEvent(event: AddMyDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is AddMyDrinkEvent.OnMyDrinkNameChanged -> {
                    _state.update { it.copy(cocktailName = event.name) }
                    savedStateHandle.set("name", event.name)
                }
                is AddMyDrinkEvent.OnMyDrinkInstructionsChanged -> {
                    _state.update { it.copy(cocktailInstructions = event.instructions) }

                    savedStateHandle.set("instructions", event.instructions)
				}
                is AddMyDrinkEvent.OnMyDrinkGlassChanged -> {
                    _state.update { it.copy(cocktailGlass = event.glass) }
                    savedStateHandle.set("glass", event.glass)
                }
                is AddMyDrinkEvent.OnMyDrinkCategoryChanged -> {
                    _state.update { it.copy(cocktailCategory = event.category) }
                    savedStateHandle.set("category", event.category)
                }
                is AddMyDrinkEvent.OnMyDrinkIsAlcoholicChanged -> {
                    _state.update { it.copy(cocktailIsAlcoholic = event.isAlcoholic) }
                    savedStateHandle.set("isAlcoholic", event.isAlcoholic)
                }
                is AddMyDrinkEvent.AddDrinkIngredientRequested -> {
                    _state.update {
                        it.copy(
                            addingIngredientName = "",
                            addingIngredientMeasure = ""
                        )
                    }
                    savedStateHandle.set("addingIngredientName", "")
                    savedStateHandle.set("addingIngredientMeasure", "")
                }
                is AddMyDrinkEvent.OnSaveClicked -> {

                }
            }
        }
    }
}