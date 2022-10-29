package it.thefedex87.cooldrinks.presentation.add_my_drink

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.bar.components.SegmentedButton
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
                    onNavigateBack()
                }
            )
        )
    }

    val spacing = LocalSpacing.current

    val state = viewModel.state.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.spaceMedium)
    ) {
        OutlinedTextFieldWithErrorMessage(
            value = state.cocktailName,
            onValueChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkNameChanged(it))
            },
            errorMessage = state.cocktailNameError,
            label = stringResource(id = R.string.name),
            imeAction = ImeAction.Next
        )
        OutlinedTextFieldWithErrorMessage(
            value = state.cocktailGlass,
            onValueChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkGlassChanged(it))
            },
            label = stringResource(id = R.string.glass),
            imeAction = ImeAction.Next
        )
        OutlinedTextFieldWithErrorMessage(
            value = state.cocktailCategory,
            onValueChanged = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkCategoryChanged(it))
            },
            label = stringResource(id = R.string.category),
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        SegmentedButton(
            options = listOf(
                stringResource(id = R.string.alcoholic),
                stringResource(id = R.string.non_alcoholic),
            ),
            onOptionClicked = {
                viewModel.onEvent(AddMyDrinkEvent.OnMyDrinkIsAlcoholicChanged(it == 0))
            },
            selectedOption = if (state.cocktailIsAlcoholic) 0 else 1,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        Text(
            text = stringResource(id = R.string.ingredients),
            style = MaterialTheme.typography.labelLarge
        )
        state.cocktailIngredients.forEach { i ->
            i.name?.let {
                Text(text = it)
            }
        }
        Button(onClick = {
            viewModel.onEvent(AddMyDrinkEvent.AddDrinkIngredientRequested)
        }) {
            Text(text = stringResource(id = R.string.add))
        }
    }
}