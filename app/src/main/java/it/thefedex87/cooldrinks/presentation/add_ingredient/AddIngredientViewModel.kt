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
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddIngredientViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(
        AddIngredientState(
            id = savedStateHandle.get<String>("id"),
            ingredientName = savedStateHandle.get<String>("name") ?: "",
            ingredientDescription = savedStateHandle.get<String>("description") ?: "",
            ingredientIsAlcoholic = savedStateHandle.get<Boolean>("is_alcoholic") ?: false,
            ingredientAvailable = savedStateHandle.get<Boolean>("is_available") ?: true,
            selectedPicture = if (savedStateHandle.get<String>("selectedPicture") != null) Uri.parse(
                savedStateHandle.get<String>("selectedPicture")
            ) else null
        )
    )
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if (savedStateHandle.get<String>("id") != null) {
            savedStateHandle["isEditing"] = true
            savedStateHandle.get<String>("name")?.let { passedIngredient ->
                viewModelScope.launch {
                    repository.storedLiquors.first()
                        .firstOrNull { it.name == passedIngredient }
                        ?.let { storedIngredient ->
                            state = state.copy(
                                id = storedIngredient.id,
                                ingredientName = storedIngredient.name,
                                ingredientDescription = storedIngredient.description!!,
                                ingredientIsAlcoholic = storedIngredient.alcoholic,
                                ingredientAvailable = storedIngredient.availableLocal,
                                selectedPicture = if (!storedIngredient.imagePath.isNullOrBlank()) Uri.parse(
                                    storedIngredient.imagePath
                                ) else null,
                                prevImagePath = if (!storedIngredient.imagePath.isNullOrBlank()) Uri.parse(
                                    storedIngredient.imagePath
                                ) else null
                            )
                        }
                }
            }
        } else {
            savedStateHandle["isEditing"] = false
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
                        if (state.selectedPicture != null && state.prevImagePath != state.selectedPicture) {
                            val filePath = state.prevImagePath?.toString()?.split("/")?.last()
                                ?.replace(".jpg", "") ?: "ING_${UUID.randomUUID()}"
                            /*if (storeIngredient(imagePath = null)) {
                                _uiEvent.send(
                                    UiEvent.SaveBitmapLocal(
                                        filePath
                                    )
                                )
                            }*/

                            // BK of the old image if exists
                            state.prevImagePath?.let {
                                File(it.toString()).copyTo(
                                    target = File(it.toString().replace(".jpg", "_BK.jpg")),
                                    overwrite = true
                                )
                            }

                            _uiEvent.send(
                                UiEvent.SaveBitmapLocal(
                                    filePath
                                )
                            )
                        } else {
                            if (storeIngredient(imagePath = state.selectedPicture?.toString())) {
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
                    state = state.copy(
                        selectedPicture = event.bitmap
                    )
                    savedStateHandle["selectedPicture"] = event.bitmap.toString()
                }

                is AddIngredientEvent.PictureSaveResult -> {
                    if (event.success) {
                        /*repository.updateIngredient(
                            IngredientDetailsDomainModel(
                                id = savedStateHandle.get<String>("id"),
                                name = state.ingredientName,
                                description = state.ingredientDescription,
                                type = null,
                                alcoholic = state.ingredientIsAlcoholic,
                                imagePath = event.pathCallback.invoke(),
                                availableLocal = state.ingredientAvailable,
                                isPersonalIngredient = true
                            )
                        )*/
                        if (!storeIngredient(event.pathCallback.invoke())) {
                            File(event.pathCallback.invoke()).delete()
                            state.prevImagePath?.let {
                                File(it.toString().replace(".jpg", "_BK.jpg")).copyTo(
                                    target = File(it.toString()),
                                    overwrite = true
                                )
                                File(it.toString().replace(".jpg", "_BK.jpg")).delete()
                            }
                        } else {
                            state.prevImagePath?.let {
                                File(it.toString().replace(".jpg", "_BK.jpg")).delete()
                            }
                            _uiEvent.send(UiEvent.PopBackStack)
                        }
                    } else {
                        /*if(savedStateHandle.get<Boolean>("isEditing") == false) {
                            repository.deleteIngredient(
                                IngredientDetailsDomainModel(
                                    id = savedStateHandle.get<String>("id"),
                                    name = state.ingredientName,
                                    description = state.ingredientDescription,
                                    type = null,
                                    alcoholic = state.ingredientIsAlcoholic,
                                    imagePath = null,
                                    availableLocal = state.ingredientAvailable,
                                    isPersonalIngredient = true
                                )
                            )
                        } else {
                            repository.updateIngredient(
                                IngredientDetailsDomainModel(
                                    id = savedStateHandle.get<String>("id"),
                                    name = state.ingredientName,
                                    description = state.ingredientDescription,
                                    type = null,
                                    alcoholic = state.ingredientIsAlcoholic,
                                    imagePath = state.prevImagePath.toString(),
                                    availableLocal = state.ingredientAvailable,
                                    isPersonalIngredient = true
                                )
                            )
                        }*/
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_ingredient_picture)))
                    }
                }
            }
        }
    }

    private suspend fun storeIngredient(imagePath: String?): Boolean {
        return try {
            if (savedStateHandle.get<String>("id") != null) {
                repository.updateIngredient(
                    IngredientDetailsDomainModel(
                        id = state.id,
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
                var newId = UUID.randomUUID().toString()
                repository.storeIngredients(
                    listOf(
                        IngredientDetailsDomainModel(
                            id = newId,
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
                savedStateHandle["id"] = newId
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