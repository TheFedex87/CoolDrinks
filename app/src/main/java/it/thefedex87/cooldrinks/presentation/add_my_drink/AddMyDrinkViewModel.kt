package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.UiText
import it.thefedex87.cooldrinks.presentation.util.calcDominantColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class AddMyDrinkViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CocktailRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddMyDrinkState(
            cocktailName = savedStateHandle.get<String>("name") ?: "",
            cocktailInstructions = savedStateHandle.get<String>("instructions") ?: "",
            selectedCocktailGlass = GlassUiModel.valueOf(
                savedStateHandle.get<String>("glass") ?: GlassUiModel.NONE.toString()
            ),
            selectedCocktailCategory = CategoryUiModel.valueOf(
                savedStateHandle.get<String>("category") ?: CategoryUiModel.NONE.toString()
            ),
            cocktailIsAlcoholic = savedStateHandle.get<Boolean>("isAlcoholic") ?: true,
            addingIngredientName = savedStateHandle.get<String>("addingIngredientName"),
            addingIngredientMeasure = savedStateHandle.get<String>("addingIngredientMeasure")
        )
    )
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var queryIngredientsJob: Job? = null

    private var imagePath: Uri? = null

    init {
        val idDrink = savedStateHandle.get<Int>("drinkId")
        idDrink?.let { id ->
            if(id == 0) return@let
            viewModelScope.launch {
                val drink = repository.storedDrinks.first().first { it.idDrink == id }
                imagePath = if(drink.drinkThumb.isNotEmpty()) Uri.parse(drink.drinkThumb) else null

                savedStateHandle["name"] = drink.name
                savedStateHandle["instructions"] = drink.instructions
                savedStateHandle["glass"] = drink.glass
                savedStateHandle["category"] = drink.category
                savedStateHandle["isAlcoholic"] = drink.isAlcoholic
                _state.update {
                    AddMyDrinkState(
                        cocktailName = drink.name,
                        cocktailInstructions = drink.instructions,
                        selectedCocktailGlass = GlassUiModel.from(drink.glass) ?: GlassUiModel.NONE,
                        selectedCocktailCategory = CategoryUiModel.from(drink.category) ?: CategoryUiModel.NONE,
                        cocktailIsAlcoholic = drink.isAlcoholic,
                        addingIngredientName = savedStateHandle.get<String>("addingIngredientName"),
                        addingIngredientMeasure = savedStateHandle.get<String>("addingIngredientMeasure"),
                        cocktailIngredients = drink.ingredients,
                        selectedPicturePath = imagePath
                    )
                }
            }
        }
    }

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
                    _state.update {
                        it.copy(
                            selectedCocktailGlass = event.glass,
                            cocktailGlassesMenuExpanded = false
                        )
                    }
                    savedStateHandle["glass"] = event.glass.toString()
                }
                is AddMyDrinkEvent.OnMyDrinkGlassesExpandRequested -> {
                    _state.update { it.copy(cocktailGlassesMenuExpanded = true) }
                }
                is AddMyDrinkEvent.OnMyDrinkGlassesDismissRequested -> {
                    _state.update { it.copy(cocktailGlassesMenuExpanded = false) }
                }
                is AddMyDrinkEvent.OnMyDrinkCategoryChanged -> {
                    _state.update {
                        it.copy(
                            selectedCocktailCategory = event.category,
                            cocktailCategoriesMenuExpanded = false
                        )
                    }
                    savedStateHandle["category"] = event.category.toString()
                }
                is AddMyDrinkEvent.OnMyDrinkIsAlcoholicChanged -> {
                    _state.update { it.copy(cocktailIsAlcoholic = event.isAlcoholic) }
                    savedStateHandle["isAlcoholic"] = event.isAlcoholic
                }
                is AddMyDrinkEvent.OnMyDrinkCategoriesExpandRequested -> {
                    _state.update { it.copy(cocktailCategoriesMenuExpanded = true) }
                }
                is AddMyDrinkEvent.OnMyDrinkCategoriesDismissRequested -> {
                    _state.update { it.copy(cocktailCategoriesMenuExpanded = false) }
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
                            selectedPicturePath = event.imagePath
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
                                cocktailIngredientsError = if (_state.value.cocktailIngredients.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                            )
                        }

                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.provide_all_info_message)))
                        return@launch
                    }
                    if (_state.value.selectedPicturePath == null ||
                        _state.value.selectedPicturePath?.path == imagePath?.path) {
                        //storeDrink
                        storeMyDrink(_state.value.selectedPicturePath?.path)
                        _uiEvent.send(UiEvent.PopBackStack)
                    } else {
                        val filePath = if(imagePath != null)
                            imagePath!!.path!!.split("/").last().replace(".jpg", "")
                        else
                            "${UUID.randomUUID()}"

                        _state.update {
                            _state.value.copy(
                                isLoading = true
                            )
                        }
                        _uiEvent.send(
                            UiEvent.SaveBitmapLocal(
                                filePath
                            )
                        )
                    }
                }
                is AddMyDrinkEvent.PictureSaveResult -> {
                    if (event.success) {
                        storeMyDrink(event.pathCallback.invoke())
                        //storeIngredient(event.pathCallback.invoke())
                        _uiEvent.send(UiEvent.PopBackStack)
                    } else {
                        _state.update {
                            _state.value.copy(
                                isLoading = false
                            )
                        }
                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_drink_picture)))
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private suspend fun storeMyDrink(filePath: String?) {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        val dominantColor = try {
            if (filePath == null)
                0
            else
                suspendCoroutine {
                    calcDominantColor(
                        Drawable.createFromPath(filePath)!!,
                        null
                    ) { color ->
                        it.resume(color.toArgb())
                    }
                }
        } catch (_: Exception) {
            0
        }
        _state.update {
            it.copy(
                isLoading = false
            )
        }

        val id: Int = if (savedStateHandle.get<Int>("drinkId") != 0) {
            savedStateHandle.get<Int>("drinkId")!!
        } else {
            getFirstFreeId(1)
        }

        val detailDomainModel = DrinkDetailDomainModel(
            idDrink = id,
            isAlcoholic = _state.value.cocktailIsAlcoholic,
            category = _state.value.selectedCocktailCategory.valueStr,
            name = _state.value.cocktailName,
            drinkThumb = filePath ?: "",
            glass = _state.value.selectedCocktailGlass.valueStr,
            ingredients = _state.value.cocktailIngredients,
            instructions = _state.value.cocktailInstructions,
            isCustomCocktail = true,
            addedDate = LocalDate.now(),
            dominantColor = dominantColor,
            isFavorite = false
        )

        if (savedStateHandle.get<Int>("drinkId") == 0) {
            repository.insertMyDrink(detailDomainModel)
        } else {
            repository.updateMyDrink(detailDomainModel)
        }
    }

    private suspend fun getFirstFreeId(firstValidId: Int): Int {
        // Method to find first free Id after Id 10000 (start range for the id of MyDrink)
        val myDrinkIds = repository.myDrinks.first().map { it.idDrink }.sorted()
        var id = firstValidId
        if (myDrinkIds.isNotEmpty() && myDrinkIds[0] == id) {
            for (i in myDrinkIds.indices) {
                id++
                if (i == myDrinkIds.indices.last || myDrinkIds[i + 1] - myDrinkIds[i] != 1) {
                    break
                }
            }
        }
        return id
    }
}