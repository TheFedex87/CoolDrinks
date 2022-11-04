package it.thefedex87.cooldrinks.presentation.add_my_drink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddMyDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
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

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var queryIngredientsJob: Job? = null

    fun onEvent(event: AddMyDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is AddMyDrinkEvent.OnMyDrinkNameChanged -> {
                    _state.update {
                        it.copy(
                            cocktailName = event.name,
                            cocktailNameError = null
                        )
                    }
                    savedStateHandle["name"] = event.name
                }
                is AddMyDrinkEvent.OnMyDrinkInstructionsChanged -> {
                    _state.update {
                        it.copy(
                            cocktailInstructions = event.instructions,
                            cocktailInstructionsError = null
                        )
                    }

                    savedStateHandle["instructions"] = event.instructions
                }
                is AddMyDrinkEvent.OnMyDrinkGlassChanged -> {
                    _state.update { it.copy(cocktailGlass = event.glass) }
                    savedStateHandle["glass"] = event.glass
                }
                is AddMyDrinkEvent.OnMyDrinkCategoryChanged -> {
                    _state.update { it.copy(cocktailCategory = event.category) }
                    savedStateHandle["category"] = event.category
                }
                is AddMyDrinkEvent.OnMyDrinkIsAlcoholicChanged -> {
                    _state.update { it.copy(cocktailIsAlcoholic = event.isAlcoholic) }
                    savedStateHandle["isAlcoholic"] = event.isAlcoholic
                }
                is AddMyDrinkEvent.AddDrinkIngredientRequested -> {
                    _state.update {
                        it.copy(
                            addingIngredientName = "",
                            addingIngredientMeasure = ""
                        )
                    }
                    savedStateHandle["addingIngredientName"] = ""
                    savedStateHandle["addingIngredientMeasure"] = ""
                }
                is AddMyDrinkEvent.DismissDrinkIngredientDialogRequested -> {
                    _state.update {
                        it.copy(
                            addingIngredientName = null,
                            addingIngredientMeasure = null,
                            addingIngredientFilteredIngredients = emptyList()
                        )
                    }
                    savedStateHandle["addingIngredientName"] = null
                    savedStateHandle["addingIngredientMeasure"] = null
                }
                is AddMyDrinkEvent.OnMyDrinkAddingIngredientNameChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientName = event.name
                        )
                    }
                    savedStateHandle["addingIngredientName"] = event.name

                    queryIngredientsJob?.cancel()
                    queryIngredientsJob = launch(Dispatchers.IO) {
                        repository.queryLocalIngredients(
                            event.name
                        ).onSuccess { ingredients ->
                            _state.update {
                                it.copy(
                                    addingIngredientFilteredIngredients = ingredients
                                )
                            }
                        }.onFailure {
                            // TODO: something went wrong
                        }
                    }
                }
                is AddMyDrinkEvent.OnMyDrinkAddingIngredientMeasureChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientMeasure = event.value
                        )
                    }
                    savedStateHandle["addingIngredientMeasure"] = event.value
                }
                is AddMyDrinkEvent.OnMyDrinkAddingIngredientIsDecorationChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientIsDecoration = event.isDecoration
                        )
                    }
                }
                is AddMyDrinkEvent.OnMyDrinkAddingIngredientIsAvailableChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientIsAvailable = event.isAvailable
                        )
                    }
                }
                is AddMyDrinkEvent.AddDrinkIngredientSaveClicked -> {
                    _state.update {
                        it.copy(
                            cocktailIngredients = listOf(
                                *it.cocktailIngredients.toTypedArray(),
                                DrinkIngredientModel(
                                    name = it.addingIngredientName,
                                    measure = if (it.addingIngredientIsDecoration) "Decoration" else it.addingIngredientMeasure,
                                    isAvailable = it.addingIngredientIsAvailable
                                )
                            ),
                            addingIngredientName = null,
                            addingIngredientMeasure = null,
                            addingIngredientFilteredIngredients = emptyList(),
                            cocktailIngredientsError = null
                        )
                    }
                }
                is AddMyDrinkEvent.OnFilteredIngredientClicked -> {
                    _state.update {
                        it.copy(
                            addingIngredientName = event.ingredient.name,
                            addingIngredientFilteredIngredients = emptyList(),
                            addingIngredientIsAvailable = event.ingredient.isAvailable ?: true
                        )
                    }
                }
                is AddMyDrinkEvent.RemoveAddedIngredient -> {
                    val newIngredientList = listOf(
                        *_state.value.cocktailIngredients.filter {
                            it != event.ingredient
                        }.toTypedArray()
                    )
                    _state.update {
                        it.copy(
                            cocktailIngredients = newIngredientList
                        )
                    }
                }
                is AddMyDrinkEvent.OnPictureSelected -> {
                    _state.update {
                        it.copy(
                            selectedPicture = event.image
                        )
                    }
                }
                is AddMyDrinkEvent.OnSaveClicked -> {
                    if (_state.value.cocktailName.isEmpty() ||
                        _state.value.cocktailIngredients.isEmpty() ||
                        _state.value.cocktailInstructions.isEmpty()
                    ) {
                        _state.update {
                            it.copy(
                                cocktailNameError = if (_state.value.cocktailName.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                                cocktailInstructionsError = if (_state.value.cocktailName.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                                cocktailIngredientsError = if(_state.value.cocktailIngredients.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                            )
                        }

                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.provide_all_info_message)))
                        return@launch
                    }
                    if (_state.value.selectedPicture == null) {
                        //storeDrink
                        _uiEvent.send(UiEvent.PopBackStack)
                    } else {
                        _uiEvent.send(
                            UiEvent.SaveBitmapLocal(
                                "${_state.value.cocktailName}_${UUID.randomUUID()}"
                            )
                        )
                    }
                }
                is AddMyDrinkEvent.PictureSaveResult -> {
                    if (event.success) {
                        //storeIngredient(event.pathCallback.invoke())
                        _uiEvent.send(UiEvent.PopBackStack)
                    } else {
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_ingredient_picture)))
                    }
                }
            }
        }
    }
}