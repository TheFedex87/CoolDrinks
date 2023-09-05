package it.thefedex87.cooldrinks.presentation.add_ingredient

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.domain.utils.BitmapManager
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddIngredientViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository,
    private val bitmapManager: BitmapManager
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddIngredientState(
            title = if(savedStateHandle.get<Int>("id") != null) UiText.StringResource(R.string.edit_ingredient) else UiText.StringResource(R.string.add_new_ingredient),
            ingredientName = savedStateHandle.get<String>("name") ?: "",
            ingredientDescription = savedStateHandle.get<String>("description") ?: "",
            ingredientIsAlcoholic = savedStateHandle.get<Boolean>("isAlcoholic") ?: false,
            ingredientAvailable = savedStateHandle.get<Boolean>("isAvailable") ?: true,
            selectedPicture = if (savedStateHandle.get<String>("selectedPicture") != null) Uri.parse(
                savedStateHandle.get<String>("selectedPicture")
            ) else null
        )
    )

    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        if(savedStateHandle.get<Boolean>("loaded") != true) { // Needed to not override changes in case of process death
            if (savedStateHandle.get<String>("id") != null) {
                savedStateHandle.get<String>("name")?.let { passedIngredient ->
                    viewModelScope.launch {
                        repository.storedLiquors.first()
                            .firstOrNull { it.name == passedIngredient }
                            ?.let { storedIngredient ->
                                storedIngredient.imagePath?.let { imagePath ->
                                    if (imagePath.isNotEmpty()) {
                                        savedStateHandle["prevImagePath"] = imagePath
                                        savedStateHandle["selectedPicture"] = imagePath
                                    }
                                }
                                savedStateHandle["name"] = storedIngredient.name
                                savedStateHandle["description"] = storedIngredient.description
                                savedStateHandle["isAlcoholic"] = storedIngredient.alcoholic
                                savedStateHandle["isAvailable"] = storedIngredient.availableLocal
                                savedStateHandle["loaded"] = true


                                _state.update {
                                    it.copy(
                                        ingredientName = storedIngredient.name,
                                        ingredientDescription = storedIngredient.description!!,
                                        ingredientIsAlcoholic = storedIngredient.alcoholic,
                                        ingredientAvailable = storedIngredient.availableLocal,
                                        selectedPicture = if (!storedIngredient.imagePath.isNullOrBlank()) Uri.parse(
                                            storedIngredient.imagePath
                                        ) else null
                                    )
                                }
                            }
                    }
                }
            }
        }
    }

    fun onEvent(event: AddIngredientEvent) {
        viewModelScope.launch {
            when (event) {
                is AddIngredientEvent.OnIngredientNameChanged -> {
                    _state.update {
                        it.copy(
                            ingredientName = event.name,
                            ingredientNameError = if (event.name.isEmpty()) UiText.StringResource(R.string.required) else null
                        )
                    }
                    savedStateHandle["name"] = event.name
                }

                is AddIngredientEvent.OnIngredientDescriptionChanged -> {
                    _state.update {
                        it.copy(
                            ingredientDescription = event.description
                        )
                    }
                    savedStateHandle["description"] = event.description
                }

                is AddIngredientEvent.OnIngredientAlcoholicChanged -> {
                    _state.update {
                        it.copy(
                            ingredientIsAlcoholic = event.isAlcoholic
                        )
                    }
                    savedStateHandle["isAlcoholic"] = event.isAlcoholic
                }

                is AddIngredientEvent.OnIngredientAvailableChanged -> {
                    _state.update {
                        it.copy(
                            ingredientAvailable = event.isAvailable
                        )
                    }
                    savedStateHandle["isAvailable"] = event.isAvailable
                }

                is AddIngredientEvent.OnSaveClicked -> {
                    if (state.value.ingredientName.isBlank()) {
                        _state.update {
                            it.copy(
                                ingredientNameError = UiText.StringResource(R.string.required)
                            )
                        }
                    } else {
                        val prevImagePath = savedStateHandle.get<String>("prevImagePath")
                        val prevImagePathBk = prevImagePath?.let {
                            it.replace(".jpg", "_BK.jpg")
                        }

                        if (state.value.selectedPicture != null && (prevImagePath == null || Uri.parse(prevImagePath) != state.value.selectedPicture)) {
                            val filePath = prevImagePath?.split("/")?.last()
                                ?.replace(".jpg", "") ?: "ING_${UUID.randomUUID()}"

                            // BK of the old image if exists
                            prevImagePathBk?.let {
                                bitmapManager.createBitmapBk(prevImagePath, prevImagePathBk)
                            }
                            try {
                                val localFilePath = bitmapManager.saveBitmapLocal(state.value.selectedPicture!!.toString(), filePath)
                                Log.d(Consts.TAG, "Local file path is: $localFilePath")
                                if (!storeIngredient(localFilePath)) {
                                    bitmapManager.deleteBitmap(localFilePath)
                                    prevImagePathBk?.let {
                                        bitmapManager.createBitmapBk(prevImagePathBk, prevImagePath)
                                    }
                                } else {
                                    _uiEvent.send(UiEvent.PopBackStack(mapOf(
                                        "storedIngredient" to state.value.ingredientName
                                    )))
                                }
                            } catch (ex: Exception) {
                                _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_ingredient_picture)))
                            }
                            prevImagePathBk?.let {
                                bitmapManager.deleteBitmap(it)
                            }
                        } else {
                            if (storeIngredient(imagePath = state.value.selectedPicture?.toString())) {
                                _uiEvent.send(UiEvent.PopBackStack(mapOf(
                                    "storedIngredient" to state.value.ingredientName
                                )))
                            }
                        }
                    }
                }

                is AddIngredientEvent.OnPictureSelected -> {
                    _state.update {
                        it.copy(
                            selectedPicture = event.bitmap
                        )
                    }
                    savedStateHandle["selectedPicture"] = event.bitmap.toString()
                }
            }
        }
    }

    private suspend fun storeIngredient(imagePath: String?): Boolean {
        return try {
            if (savedStateHandle.get<String>("id") != null) {
                repository.updateIngredient(
                    IngredientDetailsDomainModel(
                        id = savedStateHandle["id"],
                        name = state.value.ingredientName,
                        description = state.value.ingredientDescription,
                        type = null,
                        alcoholic = state.value.ingredientIsAlcoholic,
                        imagePath = imagePath,
                        availableLocal = state.value.ingredientAvailable,
                        isPersonalIngredient = true
                    )
                )
            } else {
                var newId = UUID.randomUUID().toString()
                repository.storeIngredients(
                    listOf(
                        IngredientDetailsDomainModel(
                            id = newId,
                            name = state.value.ingredientName,
                            description = state.value.ingredientDescription,
                            type = null,
                            alcoholic = state.value.ingredientIsAlcoholic,
                            imagePath = imagePath,
                            availableLocal = state.value.ingredientAvailable,
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