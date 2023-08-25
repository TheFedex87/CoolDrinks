package it.thefedex87.cooldrinks.presentation.bar

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.presentation.bar.components.BarIngredientItem
import it.thefedex87.cooldrinks.presentation.bar.components.SegmentedButton
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.components.IngredientDetails
import it.thefedex87.cooldrinks.presentation.components.MiniFabSpec
import it.thefedex87.cooldrinks.presentation.my_drink.MyDrinkEvent
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.util.Consts
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BarScreen(
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    onMiniFabIngredientsListClicked: () -> Unit,
    onMiniFabCustomIngredientClicked: () -> Unit,
    onSearchDrinkClicked: (String) -> Unit,
    moveToIngredientName: String? = null,
    viewModel: BarViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = true,
                    floatingActionButtonLabel = null,
                    floatingActionButtonMultiChoice = listOf(
                        MiniFabSpec(icon = Icons.Default.List, onClick = {
                            onMiniFabIngredientsListClicked()
                        }),
                        MiniFabSpec(icon = Icons.Default.Edit, onClick = {
                            onMiniFabCustomIngredientClicked()
                        })
                    ),
                    floatingActionButtonMultiChoiceExtended = false,
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                topBarVisible = false,
                bottomBarVisible = true
            )
        )
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.bar_bg_5_b_small)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 0.6f
        )

        if (viewModel.state.ingredients.isEmpty()) {
            EmptyList(
                icon = Icons.Default.Liquor,
                text = stringResource(id = R.string.empty_bar),
                modifier = Modifier.align(Alignment.Center)
            )
        } else {

            val pagerState = rememberPagerState()
            val spacing = LocalSpacing.current

            LaunchedEffect(key1 = moveToIngredientName) {
                moveToIngredientName?.let {
                    viewModel.onEvent(BarEvent.JumpToStoredIngredient(it))
                }
            }

            LaunchedEffect(key1 = true) {
                snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect {
                    if (it >= 0)
                        viewModel.onEvent(BarEvent.SelectedIngredientChanged(ingredient = viewModel.state.ingredients[it], page = it))
                }
            }

            LaunchedEffect(key1 = true) {
                viewModel.uiEvent.onEach {
                    when(it) {
                        is UiEvent.ScrollPagerToPage -> {
                            pagerState.animateScrollToPage(it.page)
                        }
                        else -> Unit
                    }
                }.launchIn(this)
            }

            if(viewModel.state.showRemoveElementDialog) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.onEvent(BarEvent.OnRemoveCanceled)
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.onEvent(BarEvent.OnDeleteConfirmClicked(viewModel.state.elementToRemove!!))
                        }) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            viewModel.onEvent(BarEvent.OnRemoveCanceled)
                        }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    },
                    title = {
                        Text(text = stringResource(id = R.string.confirm_remove_title))
                    },
                    text = {
                        Text(text = stringResource(id = R.string.confirm_remove_custom_ingredient_body))
                    }
                )
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column {
                    Card(
                        modifier = Modifier
                            //.weight(1.5f)
                            .fillMaxWidth()
                            .padding(spacing.spaceMedium)
                    ) {
                        viewModel.state.selectedIngredient?.let {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IngredientDetails(
                                    ingredient = it.name,
                                    isLoadingIngredientInfo = false,
                                    getIngredientInfoError = null,
                                    ingredientInfo = it,
                                    modifier = Modifier
                                        .padding(spacing.spaceMedium),
                                    showSearchIcon = !it.isPersonalIngredient,
                                    showDeleteIcon = true,
                                    showEditIcon = it.isPersonalIngredient,
                                    onSearchIconClicked = onSearchDrinkClicked,
                                    onEditIconClicked = {

                                    },
                                    onDeleteIconClicked = {
                                        viewModel.onEvent(BarEvent.OnDeleteIconClicked(it, pagerState.currentPage))
                                    }
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SegmentedButton(
                                        options = listOf(
                                            stringResource(id = R.string.available),
                                            stringResource(id = R.string.not_available)
                                        ),
                                        selectedOption = viewModel.state.selectedOption,
                                        onOptionClicked = { i ->
                                            viewModel.onEvent(
                                                BarEvent.SetIngredientAvailability(
                                                    it,
                                                    i == 0
                                                )
                                            )
                                        },
                                        modifier = Modifier
                                            .padding(
                                                start = spacing.spaceMedium,
                                                end = spacing.spaceMedium,
                                                bottom = spacing.spaceMedium
                                            )
                                            .height(30.dp)
                                            //.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 50.dp),
                        count = viewModel.state.ingredients.size,
                        contentPadding = PaddingValues(
                            horizontal = 32.dp
                        ),
                        state = pagerState
                    ) { page ->
                        if(page <= viewModel.state.ingredients.lastIndex) {
                            BarIngredientItem(
                                ingredient = viewModel.state.ingredients[page],
                                pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
                            )
                        }
                    }
                }

            }
        }
    }
}