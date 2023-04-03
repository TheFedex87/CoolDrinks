package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.add_my_drink.components.AddDrinkIngredientDialog
import it.thefedex87.cooldrinks.presentation.add_my_drink.components.Material3Spinner
import it.thefedex87.cooldrinks.presentation.bar.components.SegmentedButton
import it.thefedex87.cooldrinks.presentation.components.GalleryPictureSelector
import it.thefedex87.cooldrinks.presentation.components.OutlinedTextFieldWithErrorMessage
import it.thefedex87.cooldrinks.presentation.components.saveToLocalStorage
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.toBitmap
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMyDrinkScreen(
    viewModel: AddMyDrinkViewModel = hiltViewModel(),
    onComposed: (BottomNavigationScreenState) -> Unit,
    drinkId: Int?,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val save = stringResource(id = R.string.save)
    val title = stringResource(id = R.string.add_new_cocktail)
    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = false,
                    floatingActionButtonIcon = Icons.Default.Save,
                    floatingActionButtonMultiChoice = null,
                    floatingActionButtonLabel = save,
                    floatingActionButtonClicked = {
                        viewModel.onEvent(AddMyDrinkEvent.OnSaveClicked)
                    },
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                topBarVisible = true,
                topBarTitle = title,
                topBarBackPressed = {
                    onNavigateBack()
                },
                topBarActions = {
                    IconButton(onClick = {
                        viewModel.onEvent(AddMyDrinkEvent.OnSaveClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.save),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        )

        viewModel.uiEvent.onEach { event ->
            when (event) {
                is UiEvent.SaveBitmapLocal -> {
                    val bitmap = viewModel.state.value.selectedPicturePath!!.toBitmap(context)
                    viewModel.onEvent(
                        AddMyDrinkEvent.PictureSaveResult(
                            bitmap.saveToLocalStorage(
                                context,
                                "${event.path}.jpg"
                            ),
                            pathCallback = {
                                "${context.filesDir.path}/${event.path}.jpg"
                            }
                        ))
                }
                is UiEvent.PopBackStack -> {
                    onNavigateBack()
                }
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }.launchIn(this)
    }

    val spacing = LocalSpacing.current

    val state = viewModel.state.collectAsState().value

    if (state.addingIngredientName != null && state.addingIngredientMeasure != null) {
        AddDrinkIngredientDialog(
            addingIngredientName = state.addingIngredientName,
            addingIngredientMeasure = state.addingIngredientMeasure,
            addingIngredientIsDecoration = state.addingIngredientIsDecoration,
            addingIngredientIsAvailable = state.addingIngredientIsAvailable,
            onIngredientNameChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkAddingIngredientNameChanged(it))
            },
            onIngredientMeasureChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkAddingIngredientMeasureChanged(it))
            },
            onIsDecorationChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkAddingIngredientIsDecorationChanged(it))
            },
            onIsAvailableChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkAddingIngredientIsAvailableChanged((it)))
            },
            onSaveClicked = {
                viewModel.onEvent(AddMyDrinkEvent.AddDrinkIngredientSaveClicked)
            },
            onDismiss = {
                viewModel.onEvent(AddMyDrinkEvent.DismissDrinkIngredientDialogRequested)
            },
            filteredLocalIngredients = state.addingIngredientFilteredIngredients,
            onIngredientClicked = {
                viewModel.onEvent(AddMyDrinkEvent.OnFilteredIngredientClicked(it))
            }
        )
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spaceMedium)
        ) {
            GalleryPictureSelector(
                onPicturePicked = {
                    viewModel.onEvent(AddMyDrinkEvent.OnPictureSelected(it))
                },
                selectedPicturePath = state.selectedPicturePath,
                isCircular = true,
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            OutlinedTextFieldWithErrorMessage(
                value = state.cocktailName,
                onValueChanged = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkNameChanged(it))
                },
                errorMessage = state.cocktailNameError,
                label = stringResource(id = R.string.name),
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            OutlinedTextFieldWithErrorMessage(
                value = state.cocktailInstructions,
                onValueChanged = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkInstructionsChanged(it))
                },
                errorMessage = state.cocktailInstructionsError,
                label = stringResource(id = R.string.instructions),
                imeAction = ImeAction.Default,
                modifier = Modifier.height(300.dp),
                singleLine = false
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            Material3Spinner(
                isExpanded = state.cocktailGlassesMenuExpanded,
                expandRequested = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkGlassesExpandRequested)
                },
                dismissRequested = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkGlassesDismissRequested)
                },
                values = state.cocktailGlasses,
                valueMapper = {
                    it.valueStr
                },
                value = if (state.selectedCocktailGlass == GlassUiModel.NONE)
                    stringResource(id = R.string.glass)
                else
                    state.selectedCocktailGlass.valueStr,
                onSelectionIndexChanged = {
                    viewModel.onEvent(
                        AddMyDrinkEvent.OnMyDrinkGlassChanged(
                            state.cocktailGlasses[it]
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            Material3Spinner(
                isExpanded = state.cocktailCategoriesMenuExpanded,
                expandRequested = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkCategoriesExpandRequested)
                },
                dismissRequested = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkCategoriesDismissRequested)
                },
                values = state.cocktailCategories,
                valueMapper = {
                    it.valueStr
                },
                value = if (state.selectedCocktailCategory == CategoryUiModel.NONE)
                    stringResource(id = R.string.category)
                else
                    state.selectedCocktailCategory.valueStr,
                onSelectionIndexChanged = {
                    viewModel.onEvent(
                        AddMyDrinkEvent.OnMyDrinkCategoryChanged(
                            state.cocktailCategories[it]
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            SegmentedButton(
                options = listOf(
                    stringResource(id = R.string.alcoholic),
                    stringResource(id = R.string.non_alcoholic),
                ),
                onOptionClicked = {
                    viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkIsAlcoholicChanged(it == 0))
                },
                selectedOption = if (state.cocktailIsAlcoholic) 0 else 1,
                textStyle = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.spaceSmall)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.ingredients),
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(
                            onClick = {
                                viewModel.onEvent(AddMyDrinkEvent.AddDrinkIngredientRequested)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.add)
                            )
                        }
                    }

                    if (state.cocktailIngredientsError != null) {
                        Text(
                            text = state.cocktailIngredientsError.asString(LocalContext.current),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = spacing.spaceMedium)
                        )
                    }

                    state.cocktailIngredients.forEach { i ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            i.name?.let {
                                Text(text = it)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            i.measure?.let {
                                Text(text = it)
                            }
                            IconButton(
                                onClick = {
                                    viewModel.onEvent(AddMyDrinkEvent.RemoveAddedIngredient(i))
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(
                                        id = R.string.remove
                                    )
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(spacing.spaceSmall))
        }
    }
}