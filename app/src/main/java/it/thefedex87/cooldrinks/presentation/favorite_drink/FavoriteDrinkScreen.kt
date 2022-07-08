package it.thefedex87.cooldrinks.presentation.favorite_drink

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.presentation.components.DropDownChip
import it.thefedex87.cooldrinks.presentation.components.DropDownItem
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailEvent
import it.thefedex87.cooldrinks.presentation.favorite_drink.components.FavoriteDrinkItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.util.Consts.TAG

@Composable
fun FavoriteDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: FavoriteDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null,
                topBarColor = null
            )
        )

        viewModel.uiEvent.collect {
            when(it) {
                is UiEvent.ShowSnackBar -> {
                    Log.d(TAG, "Show snack bar with message: ${it.message.asString(context)}")
                    snackbarHostState.showSnackbar(
                        message = it.message.asString(context),
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    val spacing = LocalSpacing.current

    if (viewModel.state.showConfirmRemoveFavoriteDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(FavoriteDrinkEvent.RemoveFromFavoriteCanceled)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(FavoriteDrinkEvent.RemoveFromFavoriteConfirmed(viewModel.state.drinkToRemove!!))
                }) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(FavoriteDrinkEvent.RemoveFromFavoriteCanceled)
                }) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Confirm Remove")
            },
            text = {
                Text(text = "Do you really want remove this drink from the favorites?")
            }
        )
    }

    val glasses = viewModel.state.glasses.map {
        DropDownItem(
            label = it,
            onItemClick = {
                viewModel.onEvent(FavoriteDrinkEvent.GlassFilterValueChanged(GlassFilter.toEnum(it)))
            }
        )
    }

    val categories = viewModel.state.categories.map {
        DropDownItem(
            label = it,
            onItemClick = {
                viewModel.onEvent(
                    FavoriteDrinkEvent.CategoryFilterValueChanged(
                        CategoryFilter.toEnum(
                            it
                        )
                    )
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceMedium)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                DropDownChip(
                    isChipSelected = viewModel.state.alcoholFilter != AlcoholFilter.NONE,
                    onChipClick = {
                        viewModel.onEvent(FavoriteDrinkEvent.ExpandeAlcoholMenu)
                    },
                    label = viewModel.state.alcoholFilter.toString(),
                    isMenuExpanded = viewModel.state.alcoholMenuExpanded,
                    onDismissRequest = {
                        viewModel.onEvent(FavoriteDrinkEvent.CollapseAlcoholMenu)
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Alcohol filter enabled"
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
                    dropDownItems = listOf(
                        DropDownItem(
                            label = "None",
                            onItemClick = {
                                viewModel.onEvent(
                                    FavoriteDrinkEvent.AlcoholFilterValueChanged(
                                        AlcoholFilter.NONE
                                    )
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Filter"
                                )
                            }
                        ),
                        DropDownItem(
                            label = AlcoholFilter.ALCOHOLIC.toString(),
                            onItemClick = {
                                viewModel.onEvent(
                                    FavoriteDrinkEvent.AlcoholFilterValueChanged(
                                        AlcoholFilter.ALCOHOLIC
                                    )
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.LocalBar,
                                    contentDescription = "Alcoholic Filter"
                                )
                            }
                        ),
                        DropDownItem(
                            label = AlcoholFilter.NOT_ALCOHOLIC.toString(),
                            onItemClick = {
                                viewModel.onEvent(
                                    FavoriteDrinkEvent.AlcoholFilterValueChanged(
                                        AlcoholFilter.NOT_ALCOHOLIC
                                    )
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.NoDrinks,
                                    contentDescription = "Non Alcoholic Filter"
                                )
                            }
                        )
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                DropDownChip(
                    isChipSelected = viewModel.state.glassFilter != GlassFilter.NONE,
                    onChipClick = {
                        viewModel.onEvent(FavoriteDrinkEvent.ExpandeGlassMenu)
                    },
                    label = viewModel.state.glassFilter.toString(),
                    isMenuExpanded = viewModel.state.glassMenuExpanded,
                    onDismissRequest = {
                        viewModel.onEvent(FavoriteDrinkEvent.CollapseGlassMenu)
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Glass filter enabled"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (viewModel.state.glassMenuExpanded)
                                Icons.Default.ArrowDropUp else
                                Icons.Default.ArrowDropDown,
                            contentDescription = "Expand glass filter"
                        )
                    },
                    dropDownItems = glasses
                )
                Spacer(modifier = Modifier.width(8.dp))
                DropDownChip(
                    isChipSelected = viewModel.state.categoryFilter != CategoryFilter.NONE,
                    onChipClick = {
                        viewModel.onEvent(FavoriteDrinkEvent.ExpandeCategoryMenu)
                    },
                    label = viewModel.state.categoryFilter.toString(),
                    isMenuExpanded = viewModel.state.categoryMenuExpanded,
                    onDismissRequest = {
                        viewModel.onEvent(FavoriteDrinkEvent.CollapseCategoryMenu)
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Category filter enabled"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = if (viewModel.state.categoryMenuExpanded)
                                Icons.Default.ArrowDropUp else
                                Icons.Default.ArrowDropDown,
                            contentDescription = "Expand category filter"
                        )
                    },
                    dropDownItems = categories
                )
            }
        }
        items(viewModel.state.drinks) { drink ->
            FavoriteDrinkItem(
                drink = drink,
                onDrinkClicked = { id, color, name ->
                    onDrinkClicked(id, color, name)
                },
                onUnfavoriteClicked = {
                    viewModel.onEvent(FavoriteDrinkEvent.UnfavoriteClicked(drink))
                },
                modifier = Modifier.padding(
                    vertical = spacing.spaceSmall
                )
            )
        }
    }
}