package it.thefedex87.cooldrinks.presentation.favorite_drink

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.presentation.favorite_drink.components.FavoriteDrinkItem
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun FavoriteDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int) -> Unit,
    viewModel: FavoriteDrinkViewModel = hiltViewModel()
) {
    val spacing = LocalSpacing.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceSmall)
    ) {
        items(viewModel.state.drinks) { drink ->
            FavoriteDrinkItem(
                drink = drink,
                onDrinkClicked = { id, color ->
                    onDrinkClicked(id, color)
                },
                modifier = Modifier.padding(
                    spacing
                        .spaceSmall
                )
            )
        }
    }
}