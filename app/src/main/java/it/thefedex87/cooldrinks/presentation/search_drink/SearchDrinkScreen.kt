package it.thefedex87.cooldrinks.presentation.search_drink

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import it.thefedex87.cooldrinks.util.Consts.TAG

@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
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
                    snackbarHostState.showSnackbar(
                        message = it.message.asString(context),
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    Log.d(TAG, "Composing SearchDrinkScreen")

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
            modifier = Modifier.padding(spacing.spaceSmall)
        )
        if (viewModel.state.foundDrinks.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.state.foundDrinks) { drink ->
                    DrinkItem(
                        drink = drink.value,
                        onItemClick = { id, color, name ->
                            onDrinkClicked(id, color, name)
                        },
                        onFavoriteClick = {
                            viewModel.onEvent(SearchDrinkEvent.OnFavoriteClick(it))
                        },
                        calcDominantColor = { drawable, onFinish ->
                            viewModel.calcDominantColor(drawable, drink.value, onFinish)
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