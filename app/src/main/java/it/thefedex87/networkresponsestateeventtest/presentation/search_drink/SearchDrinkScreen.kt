package it.thefedex87.networkresponsestateeventtest.presentation.search_drink

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.networkresponsestateeventtest.presentation.components.SearchTextField
import it.thefedex87.networkresponsestateeventtest.presentation.search_drink.components.DrinkItem
import it.thefedex87.networkresponsestateeventtest.presentation.ui.theme.LocalSpacing
import it.thefedex87.networkresponsestateeventtest.presentation.util.UiEvent

@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    scaffoldState: ScaffoldState,
    onShowDrinkDetailsClicked: (Int) -> Unit,
    viewModel: SearchDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when(it) {
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
            .padding(spacing.spaceMedium)
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
            }
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(viewModel.state.foundDrinks) { drink ->
                DrinkItem(drink = drink)
            }
        }
    }
}