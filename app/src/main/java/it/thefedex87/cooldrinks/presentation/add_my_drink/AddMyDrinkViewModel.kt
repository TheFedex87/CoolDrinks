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
    /*private val _state = MutableStateFlow(
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
    val state = _state.asStateFlow()*/

    val state2 = savedStateHandle.getStateFlow("state", AddMyDrinkState(
        title = if(savedStateHandle.get<Int>("drinkId") != null) UiText.StringResource(R.string.edit_cocktail) else UiText.StringResource(R.string.add_new_cocktail)
    ))

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
                        //savedStateHandle["selectedPicture"] = drink.drinkThumb.ifEmpty { null }
                    }


                    savedStateHandle["loaded"] = true

                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailName = drink.name,
                            cocktailInstructions = drink.instructions,
                            selectedCocktailGlass = GlassUiModel.from(drink.glass)
                                ?: GlassUiModel.NONE,
                            selectedCocktailCategory = CategoryUiModel.from(drink.category)
                                ?: CategoryUiModel.NONE,
                            cocktailIsAlcoholic = drink.isAlcoholic,
                            cocktailIngredients = drink.ingredients,
                            selectedPicture = imagePath
                        )
                }
            }
        }
    }

    fun onEvent(event: AddMyDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is AddMyDrinkEvent.OnMyDrinkNameChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailName = event.name,
                            cocktailNameError = null
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkInstructionsChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailInstructions = event.instructions,
                            cocktailInstructionsError = null
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkGlassChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            selectedCocktailGlass = event.glass,
                            cocktailGlassesMenuExpanded = false
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkGlassesExpandRequested -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailGlassesMenuExpanded = true
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkGlassesDismissRequested -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailGlassesMenuExpanded = false
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkCategoryChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            selectedCocktailCategory = event.category,
                            cocktailCategoriesMenuExpanded = false
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkIsAlcoholicChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailIsAlcoholic = event.isAlcoholic
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkCategoriesExpandRequested -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailCategoriesMenuExpanded = true
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkCategoriesDismissRequested -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailCategoriesMenuExpanded = false
                        )
                }

                is AddMyDrinkEvent.AddDrinkIngredientRequested -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientName = "",
                            addingIngredientMeasure = ""
                        )
                }

                is AddMyDrinkEvent.DismissDrinkIngredientDialogRequested -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientName = null,
                            addingIngredientMeasure = null,
                            addingIngredientFilteredIngredients = emptyList()
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientNameChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientName = event.name,
                            addingIngredientSaveEnabled = false,
                            addingIngredientNameError = null
                        )
                    savedStateHandle["isCurrentIngredientNameValid"] = false

                    queryIngredientsJob?.cancel()
                    if(event.name.isNotBlank()) {
                        queryIngredientsJob = launch(Dispatchers.IO) {
                            repository.queryLocalIngredients(
                                event.name
                            ).onSuccess { ingredients ->
                                savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                                    .copy(
                                        addingIngredientFilteredIngredients = ingredients,
                                        addingIngredientFilteredListExpanded = ingredients.isNotEmpty() ||
                                                ((event.name != null && event.name.count() > 2)),
                                    )
                            }.onFailure {
                                // TODO: something went wrong
                            }
                        }
                    }
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientMeasureChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientMeasure = event.value,
                            addingIngredientSaveEnabled = (!event.value.isNullOrEmpty() ||
                                    state2.value.addingIngredientIsDecoration) &&
                                    (savedStateHandle.get<Boolean>("isCurrentIngredientNameValid") ?: false)
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientIsDecorationChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientIsDecoration = event.isDecoration,
                            addingIngredientSaveEnabled = (!state2.value.addingIngredientMeasure.isNullOrEmpty() ||
                                    event.isDecoration) &&
                                    (savedStateHandle.get<Boolean>("isCurrentIngredientNameValid") ?: false)
                        )
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientIsAvailableChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientIsAvailable = event.isAvailable
                        )
                }

                is AddMyDrinkEvent.OnSearchIngredientOnlineClicked -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            isLoading = true,
                            addingIngredientNameError = null
                        )
                    repository.getIngredientDetails(event.ingredient)
                        .onSuccess { remoteIngredint ->
                            savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                                .copy(
                                    isLoading = false,
                                    addingIngredientName = remoteIngredint.name,
                                    addingIngredientSaveEnabled = !state2.value.addingIngredientMeasure.isNullOrEmpty() ||
                                            state2.value.addingIngredientIsDecoration,
                                    addingIngredientFilteredIngredients = emptyList(),
                                    addingIngredientIsAvailable = false,
                                    addingIngredientFilteredListExpanded = false,
                                    addingIngredientNameError = null
                                )
                            savedStateHandle["isCurrentIngredientNameValid"] = true
                        }
                        .onFailure {
                            savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                                .copy(
                                    isLoading = false,
                                    addingIngredientNameError = UiText.StringResource(R.string.ingredient_not_found)
                                )
                        }
                }

                is AddMyDrinkEvent.OnNewLocalIngredientStored -> {
                    if (!event.ingredient.isNullOrEmpty()) {
                        repository.queryLocalIngredients(event.ingredient).getOrNull()?.first()
                            ?.let { ingredient ->
                                savedStateHandle["isCurrentIngredientNameValid"] = true
                                savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                                    .copy(
                                        addingIngredientName = ingredient.name,
                                        addingIngredientSaveEnabled = !state2.value.addingIngredientMeasure.isNullOrEmpty() ||
                                                state2.value.addingIngredientIsDecoration,
                                        addingIngredientFilteredIngredients = emptyList(),
                                        addingIngredientIsAvailable = ingredient.isAvailable
                                            ?: true,
                                        addingIngredientNameError = null
                                    )
                            }
                    } else {
                        savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                            .copy(
                                addingIngredientNameError = null
                            )
                    }
                }

                is AddMyDrinkEvent.AddDrinkIngredientSaveClicked -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailIngredients = listOf(
                                *state2.value.cocktailIngredients.toTypedArray(),
                                DrinkIngredientModel(
                                    name = state2.value.addingIngredientName,
                                    measure = if (state2.value.addingIngredientIsDecoration) "Decoration" else state2.value.addingIngredientMeasure,
                                    isAvailable = state2.value.addingIngredientIsAvailable
                                )
                            ),
                            addingIngredientName = null,
                            addingIngredientMeasure = null,
                            addingIngredientFilteredIngredients = emptyList(),
                            cocktailIngredientsError = null
                        )
                }

                is AddMyDrinkEvent.OnFilteredIngredientClicked -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientName = event.ingredient.name,
                            //addingIngredientFilteredIngredients = emptyList(),
                            addingIngredientIsAvailable = event.ingredient.isAvailable ?: true,
                            addingIngredientSaveEnabled = !state2.value.addingIngredientMeasure.isNullOrEmpty() ||
                                    state2.value.addingIngredientIsDecoration,
                            addingIngredientFilteredListExpanded = false
                        )
                    savedStateHandle["isCurrentIngredientNameValid"] = true
                }

                is AddMyDrinkEvent.OnMyDrinkAddingIngredientNameFocusChanged -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            addingIngredientFilteredListExpanded = event.isFocused &&
                                    (state2.value.addingIngredientFilteredIngredients.isNotEmpty() ||
                                            ((state2.value.addingIngredientName != null && state2.value.addingIngredientName!!.count() > 2) &&
                                                    (savedStateHandle.get<Boolean>("isCurrentIngredientNameValid") ?: false)))
                        )
                }

                is AddMyDrinkEvent.RemoveAddedIngredient -> {
                    val newIngredientList = listOf(
                        *state2.value.cocktailIngredients.filter {
                            it != event.ingredient
                        }.toTypedArray()
                    )
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            cocktailIngredients = newIngredientList
                        )
                }

                is AddMyDrinkEvent.OnPictureSelected -> {
                    savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                        .copy(
                            selectedPicture = event.imagePath
                        )
                }

                is AddMyDrinkEvent.OnSaveClicked -> {
                    if (state2.value.cocktailName.isEmpty() ||
                        state2.value.cocktailIngredients.isEmpty() ||
                        state2.value.cocktailInstructions.isEmpty()
                    ) {
                        savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                            .copy(
                                cocktailNameError = if (state2.value.cocktailName.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                                cocktailInstructionsError = if (state2.value.cocktailName.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                                cocktailIngredientsError = if (state2.value.cocktailIngredients.isEmpty())
                                    UiText.StringResource(R.string.required) else null,
                            )

                        _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.provide_all_info_message)))
                        return@launch
                    }

                    val prevImagePath = savedStateHandle.get<String>("prevImagePath")
                    val prevImagePathBk = prevImagePath?.let {
                        it.replace(".jpg", "_BK.jpg")
                    }
                    if (state2.value.selectedPicture != null &&
                        (prevImagePath == null || Uri.parse(prevImagePath) != state2.value.selectedPicture)) {

                        val filePath = prevImagePath?.split("/")?.last()
                            ?.replace(".jpg", "") ?: "${UUID.randomUUID()}"

                        savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                            .copy(
                                isLoading = true
                            )

                        // BK the old image
                        prevImagePathBk?.let {
                            bitmapManager.createBitmapBk(prevImagePath, prevImagePathBk)
                        }

                        try {
                            val localFilePath = bitmapManager.saveBitmapLocal(state2.value.selectedPicture!!.toString(), filePath)
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
                            savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
                                .copy(
                                    isLoading = false
                                )
                            _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.error_coping_drink_picture)))
                        }

                        prevImagePathBk?.let {
                            bitmapManager.deleteBitmap(it)
                        }
                    } else {
                        //storeDrink
                        storeMyDrink(state2.value.selectedPicture?.path)
                        _uiEvent.send(UiEvent.PopBackStack())
                    }
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private suspend fun storeMyDrink(filePath: String?) {
        savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
            .copy(
                isLoading = true
            )

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

        savedStateHandle["state"] = savedStateHandle.get<AddMyDrinkState>("state")!!
            .copy(
                isLoading = false
            )

        val id: Int = if (savedStateHandle.get<Int>("drinkId") != 0) {
            savedStateHandle.get<Int>("drinkId")!!
        } else {
            getFirstFreeId(1)
        }

        val detailDomainModel = DrinkDetailDomainModel(
            idDrink = id,
            isAlcoholic = state2.value.cocktailIsAlcoholic,
            category = state2.value.selectedCocktailCategory.valueStr,
            name = state2.value.cocktailName,
            drinkThumb = filePath ?: "",
            glass = state2.value.selectedCocktailGlass.valueStr,
            ingredients = state2.value.cocktailIngredients,
            instructions = state2.value.cocktailInstructions,
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