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
import it.thefedex87.cooldrinks.domain.utils.BitmapManager
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
    private val repository: CocktailRepository,
    private val bitmapManager: BitmapManager
) : ViewModel() {
    private val _state = MutableStateFlow(
        AddMyDrinkState(
            title = if(savedStateHandle.get<Int>("drinkId") != null) UiText.StringResource(R.string.edit_cocktail) else UiText.StringResource(R.string.add_new_cocktail),
            cocktailName = savedStateHandle.get<String>("name") ?: "",
            cocktailInstructions = savedStateHandle.get<String>("instructions") ?: "",
            selectedCocktailGlass = if(savedStateHandle.get<String>("glass") != null) GlassUiModel.from(savedStateHandle.get<String>("glass") as String)!! else GlassUiModel.NONE,
            selectedCocktailCategory = if(savedStateHandle.get<String>("category") != null) CategoryUiModel.from(savedStateHandle.get<String>("category") as String)!! else CategoryUiModel.NONE,
            cocktailIsAlcoholic = savedStateHandle.get<Boolean>("isAlcoholic") ?: true,
            addingIngredientName = savedStateHandle.get<String>("addingIngredientName"),
            addingIngredientMeasure = savedStateHandle.get<String>("addingIngredientMeasure"),
            addingIngredientIsAvailable = savedStateHandle.get<Boolean>("addingIngredientIsAvailable") ?: false,
            addingIngredientIsDecoration = savedStateHandle.get<Boolean>("addingIngredientIsDecoration") ?: false,
            selectedPicture = if (savedStateHandle.get<String>("selectedPicture") != null) Uri.parse(
                savedStateHandle.get<String>("selectedPicture")
            ) else null,
            cocktailIngredients = savedStateHandle.get<List<DrinkIngredientModel>>("ingredients") ?: emptyList(),
            addingIngredientSaveEnabled = savedStateHandle.get<Boolean>("addingIngredientSaveEnabled") ?: false
        )
    )
    val state = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var queryIngredientsJob: Job? = null

    //private var imagePath: Uri? = null
    private var isFavorite: Boolean = false

    init {
        if(savedStateHandle.get<Boolean>("loaded") != true) { // Needed to not override changes in case of process death
            val idDrink = savedStateHandle.get<Int>("drinkId")
            idDrink?.let { id ->
                if (id == 0) return@let
                viewModelScope.launch {
                    val drink = repository.storedDrinks.first().first { it.idDrink == id }
                    val imagePath =
                        if (drink.drinkThumb.isNotEmpty()) Uri.parse(drink.drinkThumb) else null
                    isFavorite = drink.isFavorite

                    imagePath.let { imagePath ->
                        savedStateHandle["prevImagePath"] = drink.drinkThumb.ifEmpty { null }
                        savedStateHandle["selectedPicture"] = drink.drinkThumb.ifEmpty { null }
                    }

                    savedStateHandle["name"] = drink.name
                    savedStateHandle["instructions"] = drink.instructions
                    savedStateHandle["glass"] = drink.glass
                    savedStateHandle["category"] = drink.category
                    savedStateHandle["isAlcoholic"] = drink.isAlcoholic
                    savedStateHandle["ingredients"] = drink.ingredients
                    savedStateHandle["loaded"] = true

                    _state.update {
                        AddMyDrinkState(
                            cocktailName = drink.name,
                            cocktailInstructions = drink.instructions,
                            selectedCocktailGlass = GlassUiModel.from(drink.glass)
                                ?: GlassUiModel.NONE,
                            selectedCocktailCategory = CategoryUiModel.from(drink.category)
                                ?: CategoryUiModel.NONE,
                            cocktailIsAlcoholic = drink.isAlcoholic,
                            addingIngredientName = savedStateHandle.get<String>("addingIngredientName"),
                            addingIngredientMeasure = savedStateHandle.get<String>("addingIngredientMeasure"),
                            cocktailIngredients = drink.ingredients,
                            selectedPicture = imagePath
                        )
                    }
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
                    savedStateHandle["glass"] = event.glass.valueStr
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
                    savedStateHandle["category"] = event.category.valueStr
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
                            addingIngredientName = event.name,
                            addingIngredientSaveEnabled = false,
                            addingIngredientNameError = null
                        )
                    }
                    savedStateHandle["addingIngredientName"] = event.name
                    savedStateHandle["isCurrentIngredientNameValid"] = false
                    savedStateHandle["addingIngredientSaveEnabled"] = false

                    queryIngredientsJob?.cancel()
                    if(event.name.isNotBlank()) {
                        queryIngredientsJob = launch(Dispatchers.IO) {
                            repository.queryLocalIngredients(
                                event.name
                            ).onSuccess { ingredients ->
                                _state.update {
                                    it.copy(
                                        addingIngredientFilteredIngredients = ingredients,
                                        addingIngredientFilteredListExpanded = ingredients.isNotEmpty() ||
                                                ((event.name != null && event.name.count() > 2)),
                                    )
                                }
                            }.onFailure {
                                // TODO: something went wrong
                            }
                        }
                    }
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientMeasureChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientMeasure = event.value,
                            addingIngredientSaveEnabled = (!event.value.isNullOrEmpty() ||
                                    it.addingIngredientIsDecoration) &&
                                    (savedStateHandle.get<Boolean>("isCurrentIngredientNameValid") ?: false)
                        )
                    }
                    savedStateHandle["addingIngredientMeasure"] = event.value
                    savedStateHandle["addingIngredientSaveEnabled"] = _state.value.addingIngredientSaveEnabled
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientIsDecorationChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientIsDecoration = event.isDecoration,
                            addingIngredientSaveEnabled = (!it.addingIngredientMeasure.isNullOrEmpty() ||
                                    event.isDecoration) &&
                                    (savedStateHandle.get<Boolean>("isCurrentIngredientNameValid") ?: false)
                        )
                    }
                    savedStateHandle["addingIngredientIsDecoration"] = event.isDecoration
                    savedStateHandle["addingIngredientSaveEnabled"] = _state.value.addingIngredientSaveEnabled
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientIsAvailableChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientIsAvailable = event.isAvailable
                        )
                    }
                    savedStateHandle["addingIngredientIsAvailable"] = event.isAvailable
                }

                is AddMyDrinkEvent.OnSearchIngredientOnlineClicked -> {
                    _state.update {
                        it.copy(
                            isLoading = true,
                            addingIngredientNameError = null
                        )
                    }
                    repository.getIngredientDetails(event.ingredient)
                        .onSuccess { remoteIngredint ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    addingIngredientName = remoteIngredint.name,
                                    addingIngredientSaveEnabled = !it.addingIngredientMeasure.isNullOrEmpty() ||
                                            it.addingIngredientIsDecoration,
                                    addingIngredientFilteredIngredients = emptyList(),
                                    addingIngredientIsAvailable = false,
                                    addingIngredientFilteredListExpanded = false,
                                    addingIngredientNameError = null
                                )
                            }
                            savedStateHandle["addingIngredientIsAvailable"] = false
                            savedStateHandle["isCurrentIngredientNameValid"] = true
                            savedStateHandle["addingIngredientSaveEnabled"] = _state.value.addingIngredientSaveEnabled
                        }
                        .onFailure {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    addingIngredientNameError = UiText.StringResource(R.string.ingredient_not_found)
                                )
                            }
                        }
                }

                is AddMyDrinkEvent.OnNewLocalIngredientStored -> {
                    if (!event.ingredient.isNullOrEmpty()) {
                        repository.queryLocalIngredients(event.ingredient).getOrNull()?.first()
                            ?.let { ingredient ->
                                _state.update {
                                    savedStateHandle["addingIngredientName"] = ingredient.name
                                    savedStateHandle["addingIngredientIsAvailable"] = ingredient.isAvailable
                                        ?: true
                                    savedStateHandle["isCurrentIngredientNameValid"] = true
                                    savedStateHandle["addingIngredientSaveEnabled"] = !it.addingIngredientMeasure.isNullOrEmpty() ||
                                            it.addingIngredientIsDecoration

                                    it.copy(
                                        addingIngredientName = ingredient.name,
                                        addingIngredientSaveEnabled = savedStateHandle.get<Boolean>("addingIngredientSaveEnabled")!!,
                                        addingIngredientFilteredIngredients = emptyList(),
                                        addingIngredientIsAvailable = ingredient.isAvailable
                                            ?: true,
                                        addingIngredientNameError = null
                                    )
                                }
                            }
                    } else {
                        _state.update {
                            it.copy(
                                addingIngredientNameError = null
                            )
                        }
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
                    savedStateHandle["ingredients"] = _state.value.cocktailIngredients
                    savedStateHandle["addingIngredientName"] = null
                    savedStateHandle["addingIngredientMeasure"] = null
                    savedStateHandle["addingIngredientIsAvailable"] = null
                    savedStateHandle["addingIngredientIsDecoration"] = null
                }

                is AddMyDrinkEvent.OnFilteredIngredientClicked -> {
                    _state.update {
                        it.copy(
                            addingIngredientName = event.ingredient.name,
                            //addingIngredientFilteredIngredients = emptyList(),
                            addingIngredientIsAvailable = event.ingredient.isAvailable ?: true,
                            addingIngredientSaveEnabled = !it.addingIngredientMeasure.isNullOrEmpty() ||
                                    it.addingIngredientIsDecoration,
                            addingIngredientFilteredListExpanded = false
                        )
                    }
                    savedStateHandle["addingIngredientName"] = event.ingredient.name
                    savedStateHandle["addingIngredientIsAvailable"] = event.ingredient.isAvailable ?: true
                    savedStateHandle["isCurrentIngredientNameValid"] = true
                    savedStateHandle["addingIngredientSaveEnabled"] = _state.value.addingIngredientSaveEnabled
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientNameFocusChanged -> {
                    _state.update {
                        it.copy(
                            addingIngredientFilteredListExpanded = event.isFocused &&
                                    (it.addingIngredientFilteredIngredients.isNotEmpty() ||
                                            ((it.addingIngredientName != null && it.addingIngredientName.count() > 2) &&
                                                    (savedStateHandle.get<Boolean>("isCurrentIngredientNameValid") ?: false)))
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
                            selectedPicture = event.imagePath
                        )
                    }
                    savedStateHandle["selectedPicture"] = event.imagePath.toString()
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

                    val prevImagePath = savedStateHandle.get<String>("prevImagePath")
                    val prevImagePathBk = prevImagePath?.let {
                        it.replace(".jpg", "_BK.jpg")
                    }
                    if (_state.value.selectedPicture != null &&
                        (prevImagePath == null || Uri.parse(prevImagePath) != _state.value.selectedPicture)) {

                        val filePath = prevImagePath?.split("/")?.last()
                            ?.replace(".jpg", "") ?: "${UUID.randomUUID()}"

                        _state.update {
                            _state.value.copy(
                                isLoading = true
                            )
                        }

                        // BK the old image
                        prevImagePathBk?.let {
                            bitmapManager.createBitmapBk(prevImagePath, prevImagePathBk)
                        }

                        try {
                            val localFilePath = bitmapManager.saveBitmapLocal(_state.value.selectedPicture!!.toString(), filePath)
                            try {
                                storeMyDrink(localFilePath)
                                _uiEvent.send(UiEvent.PopBackStack())
                            } catch (ex: Exception) {
                                bitmapManager.deleteBitmap(localFilePath)
                                prevImagePathBk?.let {
                                    bitmapManager.createBitmapBk(prevImagePathBk, prevImagePath)
                                }
                                _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_storing_new_drink)))
                            }
                        } catch (ex: Exception) {
                            _state.update {
                                _state.value.copy(
                                    isLoading = false
                                )
                            }
                            _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_drink_picture)))
                        }

                        prevImagePathBk?.let {
                            bitmapManager.deleteBitmap(it)
                        }
                    } else {
                        //storeDrink
                        storeMyDrink(_state.value.selectedPicture?.path)
                        _uiEvent.send(UiEvent.PopBackStack())
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
            isFavorite = isFavorite
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