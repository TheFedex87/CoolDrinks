package it.thefedex87.cooldrinks.presentation.search_drink

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Liquor
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.SearchTextField
import it.thefedex87.cooldrinks.presentation.search_drink.components.DrinkItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: SearchDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(key1 = true) {
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null
            )
        )

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
            modifier = Modifier.padding(
                horizontal = spacing.spaceExtraSmall,
                vertical = spacing.spaceSmall
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceSmall)
                .horizontalScroll(rememberScrollState()),
        ) {
            Column {
                FilterChip(
                    selected = viewModel.state.alcoholFilter != AlcoholFilter.NONE,
                    onClick = {
                        viewModel.onEvent(SearchDrinkEvent.ExpandeAlcoholMenu)
                    },
                    label = {
                        Text(text = viewModel.state.alcoholFilter.toString())
                    },
                    leadingIcon = {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Liquor,
                            contentDescription = "Alcoholic/Not Alcoholic"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (viewModel.state.alcoholMenuExpanded)
                                Icons.Default.ArrowDropUp else
                                Icons.Default.ArrowDropDown,
                            contentDescription = "Expand alcohol filter"
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Alcohol filter enabled"
                        )
                    }
                )
                DropdownMenu(
                    expanded = viewModel.state.alcoholMenuExpanded,
                    onDismissRequest = {
                        viewModel.onEvent(SearchDrinkEvent.CollapseAlcoholMenu)
                    }
                ) {
                    DropdownMenuItem(
                        text = { Text(text = "None") },
                        onClick = {
                            viewModel.onEvent(
                                SearchDrinkEvent.AlcoholFilterValueChanged(
                                    AlcoholFilter.NONE
                                )
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Alcoholic") },
                        onClick = {
                            viewModel.onEvent(
                                SearchDrinkEvent.AlcoholFilterValueChanged(AlcoholFilter.ALCOHOLIC)
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(text = "Not Alcoholic") },
                        onClick = {
                            viewModel.onEvent(
                                SearchDrinkEvent.AlcoholFilterValueChanged(AlcoholFilter.NOT_ALCOHOLIC)
                            )
                        }
                    )
                }
            }

        }

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