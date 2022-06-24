package it.thefedex87.cooldrinks.presentation.favorite_drink

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.presentation.favorite_drink.components.FavoriteDrinkItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun FavoriteDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: FavoriteDrinkViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null
            )
        )
    }

    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceSmall)
    ) {
        items(viewModel.state.drinks) { drink ->
            FavoriteDrinkItem(
                drink = drink,
                onDrinkClicked = { id, color, name ->
                    onDrinkClicked(id, color, name)
                },
                modifier = Modifier.padding(
                    spacing
                        .spaceSmall
                )
            )
        }
    }
}