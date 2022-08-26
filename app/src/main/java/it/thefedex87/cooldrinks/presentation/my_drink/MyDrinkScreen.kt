package it.thefedex87.cooldrinks.presentation.my_drink

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState

@Composable
fun MyDrinkScreen(
    viewModel: MyDrinkViewModel = hiltViewModel(),
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState()
) {
    /*val fabTitle = stringResource(id = R.string.add)
    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null,
                topBarColor = null,
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = true,
                    floatingActionButtonClicked = {
                        
                    },
                    floatingActionButtonIcon = Icons.Default.Add,
                    floatingActionButtonLabel = fabTitle,
                    floatingActionButtonMultiChoice = null
                )
            )
        )
    }*/
    
    Text(text = "q")
}