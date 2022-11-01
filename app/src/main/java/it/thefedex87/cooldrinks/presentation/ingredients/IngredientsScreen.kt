package it.thefedex87.cooldrinks.presentation.ingredients

import android.util.Log
import android.widget.Space
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientDetailsDialog
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientsScreen(
    snackbarHostState: SnackbarHostState,
    onItemClick: (String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    isSelectionEnabled: Boolean,
    currentBottomNavigationScreenState: BottomNavigationScreenState,
    navController: NavController,
    viewModel: IngredientsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    BackHandler(enabled = viewModel.state.isMultiSelectionEnabled) {
        viewModel.onEvent(IngredientsEvent.MultiSelectionStateChanged(false))
    }

    if (viewModel.state.showDetailOfIngredient != null) {
        IngredientDetailsDialog(
            ingredient = viewModel.state.showDetailOfIngredient!!,
            isLoadingIngredientInfo = viewModel.state.isLoadingIngredientInfo,
            getIngredientInfoError = viewModel.state.getIngredientInfoError,
            onDismiss = {
                viewModel.onEvent(IngredientsEvent.HideIngredientsDetails)
            },
            ingredientInfo = viewModel.state.ingredientInfo
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

        viewModel.uiEvent.collect {
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
    LaunchedEffect(key1 = viewModel.state.isMultiSelectionEnabled) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = viewModel.state.isMultiSelectionEnabled,
                    floatingActionButtonLabel = saveLabel,
                    floatingActionButtonClicked = {
                        viewModel.onEvent(IngredientsEvent.StoreIngredients)
                    },
                    floatingActionButtonMultiChoice = null,
                    floatingActionButtonMultiChoiceExtended = false,
                    floatingActionButtonIcon = Icons.Default.Add
                )
            )
        )
    }

    if (viewModel.state.ingredients.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(viewModel.state.ingredients) { ingredient ->
                IngredientItem(
                    ingredient = ingredient,
                    onIngredientInfoClick = {
                        viewModel.onEvent(IngredientsEvent.ShowIngredientsDetails(ingredient = ingredient.name))
                    },
                    showSeparator = viewModel.state.ingredients.indexOf(ingredient) < viewModel.state.ingredients.lastIndex,
                    onItemClick = {
                        if (!viewModel.state.isMultiSelectionEnabled) {
                            onItemClick(ingredient.name)
                        } else {
                            ingredient.isSelected.value = !ingredient.isSelected.value
                        }
                    },
                    onItemLongClick = {
                        if (isSelectionEnabled && !viewModel.state.isMultiSelectionEnabled) {
                            ingredient.isSelected.value = true
                            viewModel.onEvent(IngredientsEvent.MultiSelectionStateChanged(enabled = true))
                        }
                    }
                )
            }
        }
    }

    if (viewModel.state.showRetryButton) {
        Box(modifier = Modifier.fillMaxSize()) {
            OutlinedButton(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    viewModel.onEvent(IngredientsEvent.RetryFetchIngredients)
                }
            ) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}