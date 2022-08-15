package it.thefedex87.cooldrinks.presentation.bar

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
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
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.presentation.bar.components.BarIngredientItem
import it.thefedex87.cooldrinks.presentation.bar.components.SegmentedButton
import it.thefedex87.cooldrinks.presentation.components.IngredientDetails
import it.thefedex87.cooldrinks.presentation.components.MiniFabSpec
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalPagerApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun BarScreen(
    paddingValues: PaddingValues,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    onMiniFabIngredientsListClicked: () -> Unit,
    onMiniFabCustomIngredientClicked: () -> Unit,
    onSearchDrinkClicked: (String) -> Unit,
    viewModel: BarViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
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
        /*BubblesBackGround(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            bottomPadding = paddingValues.calculateBottomPadding()
        )*/

        if (viewModel.state.ingredients.isEmpty()) {
            Text(
                text = "Your bar seems to be empty...add your first liquor",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        } else {
            /*LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(viewModel.state.ingredients) {
                    BarIngredientItem(ingredientDetails = it)
                }
            }*/

            val pagerState = rememberPagerState()
            val spacing = LocalSpacing.current

            /*var selectedIngredient by remember {
                mutableStateOf<IngredientDetailsDomainModel?>(null)
            }*/
            LaunchedEffect(key1 = pagerState) {
                snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect {
                    if (it >= 0)
                    //selectedIngredient = viewModel.state.ingredients[it]
                        viewModel.onEvent(BarEvent.SelectedIngredientChanged(viewModel.state.ingredients[it]))
                }
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                val constraints = this
                Column(modifier = Modifier.fillMaxHeight()) {
                    Card(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxWidth()
                            .padding(spacing.spaceMedium)
                    ) {
                        viewModel.state.selectedIngredient?.let {
                            Column(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                IngredientDetails(
                                    ingredient = it.name,
                                    isLoadingIngredientInfo = false,
                                    getIngredientInfoError = null,
                                    ingredientInfo = it,
                                    modifier = Modifier
                                        .padding(spacing.spaceMedium)
                                        .weight(1f),
                                    showSearchIcon = true,
                                    onSearchIconClicked = {
                                        onSearchDrinkClicked(it.name)
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
                                            .weight(1f)
                                    )
                                    /*IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier
                                            .padding(
                                                start = spacing.spaceSmall,
                                                end = spacing.spaceSmall,
                                                bottom = spacing.spaceSmall
                                            )
                                    ) {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search for drink")
                                    }*/
                                }
                            }
                        }
                    }

                    HorizontalPager(
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 24.dp),
                        count = viewModel.state.ingredients.size,
                        contentPadding = PaddingValues(
                            horizontal = 32.dp
                        ),
                        state = pagerState
                    ) { page ->
                        BarIngredientItem(
                            ingredientDetails = viewModel.state.ingredients[page],
                            page = page,
                        )
                    }
                }

            }
        }
    }
}