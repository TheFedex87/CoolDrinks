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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.components.DetailedDrinkItem
import it.thefedex87.cooldrinks.presentation.components.DropDownChip
import it.thefedex87.cooldrinks.presentation.components.DropDownItem
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.model.CategoryUiModel
import it.thefedex87.cooldrinks.presentation.model.GlassUiModel
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDrinkScreen(
    state: FavoriteDrinkState,
    onEvent: (FavoriteDrinkEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onEditDrinkClicked: (DrinkDetailDomainModel) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState()
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

        uiEvent.collect {
            when (it) {
                is UiEvent.ShowSnackBar -> {
                    Log.d(TAG, "Show snack bar with message: ${it.message.asString(context)}")
                    snackbarHostState.showSnackbar(
                        message = it.message.asString(context),
                        duration = SnackbarDuration.Long
                    )
                }
                else -> {}
            }
        }
    }

    val spacing = LocalSpacing.current

    if (state.showConfirmRemoveFavoriteDialog) {
        AlertDialog(
            onDismissRequest = {
                onEvent(FavoriteDrinkEvent.RemoveFromFavoriteCanceled)
            },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(FavoriteDrinkEvent.RemoveFromFavoriteConfirmed(state.drinkToRemove!!))
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onEvent(FavoriteDrinkEvent.RemoveFromFavoriteCanceled)
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.confirm_remove_title))
            },
            text = {
                Text(text = stringResource(id = R.string.confirm_remove_from_favorite_body))
            }
        )
    }

    val glasses = state.glasses.map {
        DropDownItem(
            label = it.valueStr,
            onItemClick = {
                onEvent(FavoriteDrinkEvent.GlassFilterValueChanged(it))
            }
        )
    }

    val categories = state.categories.map {
        DropDownItem(
            label = it.valueStr,
            onItemClick = {
                onEvent(
                    FavoriteDrinkEvent.CategoryFilterValueChanged(it)
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
            if (state.showFilterChips) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        DropDownChip(
                            isChipSelected = state.alcoholFilter != AlcoholFilter.NONE,
                            onChipClick = {
                                onEvent(FavoriteDrinkEvent.ExpandeAlcoholMenu)
                            },
                            label = state.alcoholFilter.toString(),
                            isMenuExpanded = state.alcoholMenuExpanded,
                            onDismissRequest = {
                                onEvent(FavoriteDrinkEvent.CollapseAlcoholMenu)
                            },
                            selectedIcon = {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = stringResource(id = R.string.alcohol_filter_enabled)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (state.alcoholMenuExpanded)
                                        Icons.Default.ArrowDropUp else
                                        Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.expand_alcohol_filter)
                                )
                            },
                            dropDownItems = listOf(
                                DropDownItem(
                                    label = stringResource(id = R.string.none),
                                    onItemClick = {
                                        onEvent(
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
                                        onEvent(
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
                                        onEvent(
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
                            isChipSelected = state.glassUiModel != GlassUiModel.NONE,
                            onChipClick = {
                                onEvent(FavoriteDrinkEvent.ExpandeGlassMenu)
                            },
                            label = if (state.glassUiModel == GlassUiModel.NONE)
                                stringResource(id = R.string.glass)
                            else
                                state.glassUiModel.valueStr,
                            isMenuExpanded = state.glassMenuExpanded,
                            onDismissRequest = {
                                onEvent(FavoriteDrinkEvent.CollapseGlassMenu)
                            },
                            selectedIcon = {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = stringResource(id = R.string.glass_filter_enabled)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (state.glassMenuExpanded)
                                        Icons.Default.ArrowDropUp else
                                        Icons.Default.ArrowDropDown,
                                    contentDescription = stringResource(id = R.string.expand_glass_filter)
                                )
                            },
                            dropDownItems = glasses
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DropDownChip(
                            isChipSelected = state.categoryUiModel != CategoryUiModel.NONE,
                            onChipClick = {
                                onEvent(FavoriteDrinkEvent.ExpandeCategoryMenu)
                            },
                            label = if (state.categoryUiModel == CategoryUiModel.NONE)
                                stringResource(id = R.string.category)
                            else
                                state.categoryUiModel.valueStr,
                            isMenuExpanded = state.categoryMenuExpanded,
                            onDismissRequest = {
                                onEvent(FavoriteDrinkEvent.CollapseCategoryMenu)
                            },
                            selectedIcon = {
                                Icon(
                                    imageVector = Icons.Default.Done,
                                    contentDescription = stringResource(id = R.string.category_filter_enabled)
                                )
                            },
                            trailingIcon = {
                                Icon(
                                    imageVector = if (state.categoryMenuExpanded)
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

            items(state.drinks) { drink ->
                DetailedDrinkItem(
                    drink = drink,
                    drinkId = drink.idDrink,
                    onDrinkClicked = { id, color, name ->
                        onDrinkClicked(id, color, name)
                    },
                    onFavoriteChangeClicked = {
                        onEvent(FavoriteDrinkEvent.UnfavoriteClicked(drink))
                    },
                    onEditDrinkClicked = null,
                    onRemoveClicked = null,
                    modifier = Modifier.padding(
                        vertical = spacing.spaceSmall
                    )
                )
            }
        }

        if (!state.showFilterChips) {
            EmptyList(
                icon = Icons.Default.FavoriteBorder,
                text = stringResource(id = R.string.add_some_favorites),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}