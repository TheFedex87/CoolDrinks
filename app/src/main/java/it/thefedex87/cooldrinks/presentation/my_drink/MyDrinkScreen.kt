package it.thefedex87.cooldrinks.presentation.my_drink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.DetailedDrinkItem
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun MyDrinkScreen(
    viewModel: MyDrinkViewModel = hiltViewModel(),
    onDrinkClicked: (Int, Int, String) -> Unit
) {
    val spacing = LocalSpacing.current
    val state = viewModel.state.collectAsState().value

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (state.drinks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyList(
                icon = Icons.Default.LocalDrink,
                text = stringResource(id = R.string.add_your_cocktail),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val constraints = this
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.spaceMedium)
            ) {

                items(state.drinks) {
                    DetailedDrinkItem(
                        drink = it,
                        onDrinkClicked = onDrinkClicked,
                        onFavoriteChangeClicked = { },
                        modifier = Modifier.padding(
                            vertical = spacing.spaceSmall
                        ),
                    )
                }
            }
        }
    }
}