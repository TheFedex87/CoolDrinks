package it.thefedex87.cooldrinks.presentation.ingredients

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.SearchTextField
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientCardItem
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientDetailsDialog
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    state: IngredientsState,
    onEvent: (IngredientsEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
    onItemClick: (String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    isSelectionEnabled: Boolean,
    currentBottomNavigationScreenState: BottomNavigationScreenState,
    navController: NavController
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    BackHandler(enabled = state.isMultiSelectionEnabled) {
        onEvent(IngredientsEvent.MultiSelectionStateChanged(false))
    }

    if (state.showDetailOfIngredient != null) {
        IngredientDetailsDialog(
            ingredient = state.showDetailOfIngredient!!,
            isLoadingIngredientInfo = state.isLoadingIngredientInfo,
            getIngredientInfoError = state.getIngredientInfoError,
            onDismiss = {
                onEvent(IngredientsEvent.HideIngredientsDetails)
            },
            ingredientInfo = state.ingredientInfo
        )
    }

    LaunchedEffect(key1 = true) {
        /*onComposed(
            currentBottomNavigationScreenState.copy(
                topBarVisible = false,
                bottomBarVisible = false,
                topAppBarScrollBehavior = null,
                topBarColor = null,
                floatingActionButtonVisible = false
            )
        )*/

        uiEvent.collect {
            when (it) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    launch {
                        snackbarHostState.showSnackbar(
                            it.message.asString(context),
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                is UiEvent.PopBackStack -> {
                    navController.popBackStack()
                }
                else -> {}
            }
        }
    }

    val saveLabel = stringResource(id = R.string.add)
    LaunchedEffect(key1 = state.isMultiSelectionEnabled) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = state.isMultiSelectionEnabled,
                    floatingActionButtonLabel = saveLabel,
                    floatingActionButtonClicked = {
                        onEvent(IngredientsEvent.StoreIngredients)
                    },
                    floatingActionButtonMultiChoice = null,
                    floatingActionButtonMultiChoiceExtended = false,
                    floatingActionButtonIcon = Icons.Default.Add
                )
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.bar_bg_5_b_small)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 0.6f
        )

        Column(modifier = Modifier.fillMaxSize()) {
            SearchTextField(
                text = state.searchQuery,
                showHint = state.showSearchHint,
                onSearch = {},
                onValueChanged = {
                    onEvent(IngredientsEvent.OnSearchQueryChange(it))
                },
                onFocusChanged = {
                    onEvent(IngredientsEvent.OnSearchFocusChange(it.isFocused))
                },
                hint = stringResource(id = R.string.search_ingredient_hint),
                trailingIcon = null,
                modifier = Modifier.padding(spacing.spaceSmall)
            )
            if (state.ingredients.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(state.ingredients) { ingredient ->
                        IngredientCardItem(
                            ingredient = ingredient,
                            onIngredientInfoClick = {
                                onEvent(IngredientsEvent.ShowIngredientsDetails(ingredient = ingredient.name))
                            },
                            showSeparator = state.ingredients.indexOf(ingredient) < state.ingredients.lastIndex,
                            onItemClick = {
                                if (!state.isMultiSelectionEnabled) {
                                    onItemClick(ingredient.name)
                                } else {
                                    //ingredient.isSelected.value = !ingredient.isSelected.value
                                    onEvent(IngredientsEvent.ItemSelectionChanged(ingredient))
                                }
                            },
                            onItemLongClick = {
                                if (isSelectionEnabled && !state.isMultiSelectionEnabled) {
                                    ingredient.isSelected.value = true
                                    onEvent(
                                        IngredientsEvent.MultiSelectionStateChanged(
                                            enabled = true
                                        )
                                    )
                                }
                            },
                            onCheckedChanged = {
                                if (isSelectionEnabled) {
                                    onEvent(IngredientsEvent.ItemSelectionChanged(ingredient))
                                }
                            },
                            isSelectionEnabled = isSelectionEnabled
                        )
                    }
                }
            }
        }

        if (state.showRetryButton) {
            Box(modifier = Modifier.fillMaxSize()) {
                OutlinedButton(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = {
                        onEvent(IngredientsEvent.RetryFetchIngredients)
                    }
                ) {
                    Text(text = stringResource(id = R.string.retry))
                }
            }
        }
    }
}