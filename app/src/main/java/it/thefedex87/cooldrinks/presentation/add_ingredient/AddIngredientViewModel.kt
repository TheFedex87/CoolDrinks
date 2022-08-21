package it.thefedex87.cooldrinks.presentation.add_ingredient

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.components.saveToLocalStorage
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddIngredientViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(AddIngredientState())
        private set

    private val _addIngredientUiEvent = Channel<AddIngredientUiEvent>()
    val addIngredientUiEvent = _addIngredientUiEvent.receiveAsFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        state = state.copy(
            ingredientName = savedStateHandle.get<String>("name") ?: "",
            ingredientDescription = savedStateHandle.get<String>("description") ?: ""
        )
    }

    fun onEvent(event: AddIngredientEvent) {
        viewModelScope.launch {
            when (event) {
                is AddIngredientEvent.OnIngredientNameChanged -> {
                    state = state.copy(
                        ingredientName = event.name,
                        ingredientNameError = if (event.name.isEmpty()) UiText.StringResource(R.string.required) else null
                    )
                    savedStateHandle.set("name", event.name)
                }
                is AddIngredientEvent.OnIngredientDescriptionChanged -> {
                    state = state.copy(ingredientDescription = event.description)
                    savedStateHandle.set("description", event.description)
                }
                is AddIngredientEvent.OnIngredientAlcoholicChanged -> {
                    state = state.copy(ingredientIsAlcoholic = event.isAlcoholic)
                }
                is AddIngredientEvent.OnIngredientAvailableChanged -> {
                    state = state.copy(ingredientAvailable = event.isAvailable)
                }
                is AddIngredientEvent.OnSaveClicked -> {
                    if (state.ingredientName.isBlank()) {
                        state =
                            state.copy(ingredientNameError = UiText.StringResource(R.string.required))
                    } else {
                        if (state.selectedPicture != null) {
                            _addIngredientUiEvent.send(
                                AddIngredientUiEvent.SaveBitmapLocal(
                                    "${state.ingredientName}_${UUID.randomUUID()}"
                                )
                            )
                        } else {
                            storeIngredient(null)
                        }
                        /*repository.storeIngredients(
                            listOf(
                                IngredientDetailsDomainModel(
                                    name = state.ingredientName,
                                    description = state.ingredientDescription,
                                    type = null,
                                    alcoholic = state.ingredientIsAlcoholic,
                                    imagePath = state.
                                )
                            )
                        )*/
                    }
                }
                is AddIngredientEvent.OnPictureSelected -> {
                    state = state.copy(selectedPicture = event.bitmap)
                }
                is AddIngredientEvent.PictureSaveResult -> {
                    if (event.success) {
                        storeIngredient(event.pathCallback.invoke())
                        _uiEvent.send(UiEvent.PopBackStack)
                    } else {
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_ingredient_picture)))
                    }
                }
            }
        }
    }

    private suspend fun storeIngredient(imagePath: String?) {
        try {
            repository.storeIngredients(
                listOf(
                    IngredientDetailsDomainModel(
                        name = state.ingredientName,
                        description = state.ingredientDescription,
                        type = null,
                        alcoholic = state.ingredientIsAlcoholic,
                        imagePath = imagePath,
                        availableLocal = state.ingredientAvailable
                    )
                )
            )
        } catch (ex: Exception) {
            _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_storing_new_ingredient)))
        }
    }
}