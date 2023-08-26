package it.thefedex87.cooldrinks.presentation.add_ingredient

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
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
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddIngredientViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(
        AddIngredientState(
            isEditing = savedStateHandle.get<Boolean>("isEditing") == true,
            ingredientName = savedStateHandle.get<String>("name") ?: "",
            ingredientDescription = savedStateHandle.get<String>("description") ?: "",
            ingredientIsAlcoholic = savedStateHandle.get<Boolean>("is_alcoholic") ?: false,
            ingredientAvailable = savedStateHandle.get<Boolean>("is_available") ?: true
        )
    )
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if(savedStateHandle.get<Boolean>("isEditing") == true) {
            savedStateHandle.get<String>("name")?.let { passedIngredient ->
                viewModelScope.launch {
                    repository.storedLiquors.first()
                        .firstOrNull { it.name == passedIngredient }
                        ?.let { storedIngredient ->
                            state = state.copy(
                                ingredientName = storedIngredient.name,
                                ingredientDescription = storedIngredient.description!!,
                                ingredientIsAlcoholic = storedIngredient.alcoholic,
                                ingredientAvailable = storedIngredient.availableLocal,
                                selectedPicture = if(!storedIngredient.imagePath.isNullOrBlank()) Uri.parse(storedIngredient.imagePath) else null
                            )
                        }
                }
            }
        }
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
                    savedStateHandle.set("is_alcoholic", event.isAlcoholic)
                }

                is AddIngredientEvent.OnIngredientAvailableChanged -> {
                    state = state.copy(ingredientAvailable = event.isAvailable)
                    savedStateHandle.set("is_available", event.isAvailable)
                }

                is AddIngredientEvent.OnSaveClicked -> {
                    if (state.ingredientName.isBlank()) {
                        state =
                            state.copy(ingredientNameError = UiText.StringResource(R.string.required))
                    } else {
                        if (state.selectedPicture != null) {
                            val filePath = "${state.ingredientName}_${UUID.randomUUID()}"
                            if (storeIngredient(imagePath = null)) {
                                _uiEvent.send(
                                    UiEvent.SaveBitmapLocal(
                                        filePath
                                    )
                                )
                            }
                        } else {
                            if (storeIngredient(imagePath = null)) {
                                _uiEvent.send(UiEvent.PopBackStack)
                            }
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
                        repository.updateIngredient(
                            IngredientDetailsDomainModel(
                                name = state.ingredientName,
                                description = state.ingredientDescription,
                                type = null,
                                alcoholic = state.ingredientIsAlcoholic,
                                imagePath = event.pathCallback.invoke(),
                                availableLocal = state.ingredientAvailable,
                                isPersonalIngredient = true
                            )
                        )
                        _uiEvent.send(UiEvent.PopBackStack)
                    } else {
                        repository.deleteIngredient(
                            IngredientDetailsDomainModel(
                                name = state.ingredientName,
                                description = state.ingredientDescription,
                                type = null,
                                alcoholic = state.ingredientIsAlcoholic,
                                imagePath = null,
                                availableLocal = state.ingredientAvailable,
                                isPersonalIngredient = true
                            )
                        )
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_ingredient_picture)))
                    }
                }
            }
        }
    }

    private suspend fun storeIngredient(imagePath: String?): Boolean {
        return try {
            if(savedStateHandle.get<Boolean>("isEditing") == true) {
                repository.updateIngredient(
                    IngredientDetailsDomainModel(
                        name = state.ingredientName,
                        description = state.ingredientDescription,
                        type = null,
                        alcoholic = state.ingredientIsAlcoholic,
                        imagePath = imagePath,
                        availableLocal = state.ingredientAvailable,
                        isPersonalIngredient = true
                    )
                )
            } else {
                repository.storeIngredients(
                    listOf(
                        IngredientDetailsDomainModel(
                            name = state.ingredientName,
                            description = state.ingredientDescription,
                            type = null,
                            alcoholic = state.ingredientIsAlcoholic,
                            imagePath = imagePath,
                            availableLocal = state.ingredientAvailable,
                            isPersonalIngredient = true
                        )
                    )
                )
            }
            true
        } catch (ex: SQLiteConstraintException) {
            _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.ingredient_already_exists)))
            false
        } catch (ex: Exception) {
            _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_storing_new_ingredient)))
            false
        }
    }
}