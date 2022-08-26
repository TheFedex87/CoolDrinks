package it.thefedex87.cooldrinks.presentation.favorite_drink

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.DropDownChip
import it.thefedex87.cooldrinks.presentation.components.DropDownItem
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailEvent
import it.thefedex87.cooldrinks.presentation.favorite_drink.components.FavoriteDrinkItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.util.Consts.TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    viewModel: FavoriteDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null,
                topBarColor = null,
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = false
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy()
            )
        )

        viewModel.uiEvent.collect {
            when (it) {
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
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(FavoriteDrinkEvent.RemoveFromFavoriteCanceled)
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.confirm_remove_title))
            },
            text = {
                Text(text = stringResource(id = R.string.confirm_remove_body))
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

    Box(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.bar_bg_5_b_small)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 0.6f
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.spaceMedium)
        ) {
            if (viewModel.state.showFilterChips) {


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
                                    contentDescription = stringResource(id = R.string.alcohol_filter_enabled)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (viewModel.state.alcoholMenuExpanded)
                                        Icons.Default.ArrowDropUp else
                                        Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.expand_alcohol_filter)
                                )
                            },
                            dropDownItems = listOf(
                                DropDownItem(
                                    label = stringResource(id = R.string.none),
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
                                            contentDescription = stringResource(id = R.string.remove_filter)
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
                                            contentDescription = stringResource(id = R.string.alcohol_filter)
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
                                            contentDescription = stringResource(id = R.string.non_alcoholic_filter)
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
                                    contentDescription = stringResource(id = R.string.glass_filter_enabled)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (viewModel.state.glassMenuExpanded)
                                        Icons.Default.ArrowDropUp else
                                        Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.expand_glass_filter)
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
                                    contentDescription = stringResource(id = R.string.category_filter_enabled)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (viewModel.state.categoryMenuExpanded)
                                        Icons.Default.ArrowDropUp else
                                        Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.expand_category_filter)
                                )
                            },
                            dropDownItems = categories
                        )
                    }
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

        if (!viewModel.state.showFilterChips) {
            /*Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.search_background),
                    contentDescription = null,
                    modifier = Modifier
                        .width(250.dp)
                )
                Text(text = stringResource(id = R.string.add_some_favorites))
            }*/
            EmptyList(
                icon = Icons.Default.FavoriteBorder,
                text = stringResource(id = R.string.add_some_favorites),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}