package it.thefedex87.cooldrinks.presentation.favorite_drink

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.presentation.components.DropDownChip
import it.thefedex87.cooldrinks.presentation.components.DropDownItem
import it.thefedex87.cooldrinks.presentation.favorite_drink.components.FavoriteDrinkItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun FavoriteDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: FavoriteDrinkViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null
            )
        )
    }

    val spacing = LocalSpacing.current

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
                                viewModel.onEvent(FavoriteDrinkEvent.AlcoholFilterValueChanged(AlcoholFilter.NONE))
                            },
                            icon = {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Filter")
                            }
                        ),
                        DropDownItem(
                            label = AlcoholFilter.ALCOHOLIC.toString(),
                            onItemClick = {
                                viewModel.onEvent(FavoriteDrinkEvent.AlcoholFilterValueChanged(AlcoholFilter.ALCOHOLIC))
                            },
                            icon = {
                                Icon(imageVector = Icons.Default.LocalBar, contentDescription = "Alcoholic Filter")
                            }
                        ),
                        DropDownItem(
                            label = AlcoholFilter.NOT_ALCOHOLIC.toString(),
                            onItemClick = {
                                viewModel.onEvent(FavoriteDrinkEvent.AlcoholFilterValueChanged(AlcoholFilter.NOT_ALCOHOLIC))
                            },
                            icon = {
                                Icon(imageVector = Icons.Default.NoDrinks, contentDescription = "Non Alcoholic Filter")
                            }
                        )
                    )
                )
            }
        }
        items(viewModel.state.drinks) { drink ->
            FavoriteDrinkItem(
                drink = drink,
                onDrinkClicked = { id, color, name ->
                    onDrinkClicked(id, color, name)
                },
                modifier = Modifier.padding(
                    vertical = spacing.spaceSmall
                )
            )
        }
    }
}