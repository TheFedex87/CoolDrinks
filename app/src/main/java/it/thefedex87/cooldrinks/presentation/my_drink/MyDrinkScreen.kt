package it.thefedex87.cooldrinks.presentation.my_drink

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState

@Composable
fun MyDrinkScreen(
    viewModel: MyDrinkViewModel = hiltViewModel(),
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState()
) {
    if(viewModel.state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if(viewModel.state.drinks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyList(
                icon = Icons.Default.LocalDrink,
                text = stringResource(id = R.string.add_your_cocktail),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {

    }
}