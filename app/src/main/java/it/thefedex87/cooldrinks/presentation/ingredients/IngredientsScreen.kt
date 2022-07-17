package it.thefedex87.cooldrinks.presentation.ingredients

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientDetailsDialog
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.util.Consts.TAG

@Composable
fun IngredientsScreen(
    onItemClick: (String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: IngredientsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

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
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = false,
                bottomBarVisible = false,
                topAppBarScrollBehavior = null,
                topBarColor = null,
                floatingActionButtonVisible = false
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium)
    ) {
        items(viewModel.state.ingredients) { ingredient ->
            IngredientItem(
                name = ingredient.name,
                onIngredientInfoClick = {
                    viewModel.onEvent(IngredientsEvent.ShowIngredientsDetails(ingredient = ingredient.name))
                },
                showSeparator = viewModel.state.ingredients.indexOf(ingredient) < viewModel.state.ingredients.lastIndex,
                onItemClick = {
                    onItemClick(ingredient.name)
                }
            )
        }
    }
}