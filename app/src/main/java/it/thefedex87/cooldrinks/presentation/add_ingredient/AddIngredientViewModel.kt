package it.thefedex87.cooldrinks.presentation.add_ingredient

import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.util.Log
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
import it.thefedex87.cooldrinks.domain.utils.BitmapManager
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.util.Consts
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
    private val repository: CocktailRepository,
    private val bitmapManager: BitmapManager
) : ViewModel() {
    var state by mutableStateOf(
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
        private set

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


                                state = state.copy(
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

    fun onEvent(event: AddIngredientEvent) {
        viewModelScope.launch {
            when (event) {
                is AddIngredientEvent.OnIngredientNameChanged -> {
                    state = state.copy(
                        ingredientName = event.name,
                        ingredientNameError = if (event.name.isEmpty()) UiText.StringResource(R.string.required) else null
                    )
                    savedStateHandle["name"] = event.name
                }

                is AddIngredientEvent.OnIngredientDescriptionChanged -> {
                    state = state.copy(ingredientDescription = event.description)
                    savedStateHandle["description"] = event.description
                }

                is AddIngredientEvent.OnIngredientAlcoholicChanged -> {
                    state = state.copy(ingredientIsAlcoholic = event.isAlcoholic)
                    savedStateHandle["isAlcoholic"] = event.isAlcoholic
                }

                is AddIngredientEvent.OnIngredientAvailableChanged -> {
                    state = state.copy(ingredientAvailable = event.isAvailable)
                    savedStateHandle["isAvailable"] = event.isAvailable
                }

                is AddIngredientEvent.OnSaveClicked -> {
                    if (state.ingredientName.isBlank()) {
                        state =
                            state.copy(ingredientNameError = UiText.StringResource(R.string.required))
                    } else {
                        val prevImagePath = savedStateHandle.get<String>("prevImagePath")
                        val prevImagePathBk = prevImagePath?.let {
                            it.replace(".jpg", "_BK.jpg")
                        }

                        if (state.selectedPicture != null && (prevImagePath == null || Uri.parse(prevImagePath) != state.selectedPicture)) {
                            val filePath = prevImagePath?.split("/")?.last()
                                ?.replace(".jpg", "") ?: "ING_${UUID.randomUUID()}"

                            // BK of the old image if exists
                            prevImagePathBk?.let {
                                bitmapManager.createBitmapBk(prevImagePath, prevImagePathBk)
                            }
                            try {
                                val localFilePath = bitmapManager.saveBitmapLocal(state.selectedPicture!!.toString(), filePath)
                                Log.d(Consts.TAG, "Local file path is: $localFilePath")
                                if (!storeIngredient(localFilePath)) {
                                    bitmapManager.deleteBitmap(localFilePath)
                                    prevImagePathBk?.let {
                                        bitmapManager.createBitmapBk(prevImagePathBk, prevImagePath)
                                    }
                                } else {
                                    _uiEvent.send(UiEvent.PopBackStack(mapOf(
                                        "storedIngredient" to state.ingredientName
                                    )))
                                }
                            } catch (ex: Exception) {
                                _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_ingredient_picture)))
                            }
                            prevImagePathBk?.let {
                                bitmapManager.deleteBitmap(it)
                            }
                        } else {
                            if (storeIngredient(imagePath = state.selectedPicture?.toString())) {
                                _uiEvent.send(UiEvent.PopBackStack(mapOf(
                                    "storedIngredient" to state.ingredientName
                                )))
                            }
                        }
                    }
                }

                is AddIngredientEvent.OnPictureSelected -> {
                    state = state.copy(
                        selectedPicture = event.bitmap
                    )
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