package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.add_ingredient.AddIngredientEvent
import it.thefedex87.cooldrinks.presentation.components.OutlinedTextFieldWithErrorMessage
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.util.Consts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMyDrinkScreen(
    viewModel: AddMyDrinkViewModel = hiltViewModel(),
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    onNavigateBack: () -> Unit
) {
    val save = stringResource(id = R.string.save)
    val title = stringResource(id = R.string.add_new_cocktail)
    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = true,
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
                    Log.d(Consts.TAG, "Back pressed")
                },
            )
        )
    }

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.spaceMedium)
    ) {
        OutlinedTextFieldWithErrorMessage(
            value = viewModel.state.cocktailName,
            onValueChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkNameChanged(it))
            },
            errorMessage = viewModel.state.cocktailNameError,
            label = stringResource(id = R.string.name),
            imeAction = ImeAction.Next
        )
    }
}