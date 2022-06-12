package it.thefedex87.cooldrinks.presentation.search_drink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.SearchTextField
import it.thefedex87.cooldrinks.presentation.search_drink.components.DrinkItem
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent

@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    scaffoldState: ScaffoldState,
    onShowDrinkDetailsClicked: (Int, Int) -> Unit,
    viewModel: SearchDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.ShowSnackBar -> {
                    keyboardController?.hide()
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = it.message.asString(context),
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchTextField(
            text = viewModel.state.searchQuery,
            showHint = viewModel.state.showSearchHint,
            onSearch = {
                keyboardController?.hide()
                viewModel.onEvent(SearchDrinkEvent.OnSearchClick)
            },
            onValueChanged = {
                viewModel.onEvent(SearchDrinkEvent.OnSearchQueryChange(it))
            },
            onFocusChanged = {
                viewModel.onEvent(SearchDrinkEvent.OnSearchFocusChange(it.isFocused))
            },
            modifier = Modifier.padding(spacing.spaceMedium)
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        if (viewModel.state.foundDrinks.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.state.foundDrinks) { drink ->
                    DrinkItem(
                        drink = drink,
                        onItemClick = { id, color ->
                            onShowDrinkDetailsClicked(id, color)
                        },
                        calcDominantColor = { drawable, onFinish ->
                            viewModel.calcDominantColor(drawable, onFinish)
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.state.showNoDrinkFound)
                    Text(text = stringResource(id = R.string.no_drink_found))

                if (viewModel.state.isLoading)
                    CircularProgressIndicator()
            }
        }
    }
}