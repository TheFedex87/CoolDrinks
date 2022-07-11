package it.thefedex87.cooldrinks.presentation.ingredients

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.presentation.ingredients.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun IngredientsScreen(
    onItemClick: (String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: IngredientsViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

    if(viewModel.state.showIngredientDetails) {
        Dialog(onDismissRequest = {
            viewModel.onEvent(IngredientsEvent.HideIngredientsDetails)
        }) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(12.dp))
                .padding(spacing.spaceMedium)) {
            }
        }
    }

    LaunchedEffect(key1 = true) {
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = false,
                bottomBarVisible = false,
                topAppBarScrollBehavior = null,
                topBarColor = null
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