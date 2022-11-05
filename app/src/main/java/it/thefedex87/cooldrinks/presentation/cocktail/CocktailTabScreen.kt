package it.thefedex87.cooldrinks.presentation.cocktail

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.PersonPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.my_drink.MyDrinkScreen
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkScreen
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.util.Consts
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(
    ExperimentalPagerApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun CocktailTabScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    ingredientForSearch: String? = null,
    viewModel: CocktailTabViewModel = hiltViewModel()
) {
    val addText = stringResource(id = R.string.add)
    var localCurrentBottomNavigationScreenState = currentBottomNavigationScreenState

    var selectedDrinkDrawable by remember {
        mutableStateOf<Drawable?>(null)
    }

    val pagerState = rememberPagerState()
    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect {
            viewModel.onEvent(CocktailTabEvent.OnPagerScrolled(it))
            when (it) {
                0 -> {
                    localCurrentBottomNavigationScreenState =
                        localCurrentBottomNavigationScreenState.copy(
                            topBarVisible = false,
                            bottomBarVisible = true,
                            topAppBarScrollBehavior = null,
                            topBarColor = null,
                            prevFabState = localCurrentBottomNavigationScreenState.fabState.copy(),
                            fabState = localCurrentBottomNavigationScreenState.fabState.copy(
                                floatingActionButtonVisible = false
                            )
                        )
                    onComposed(
                        localCurrentBottomNavigationScreenState
                    )
                }
                1 -> {
                    localCurrentBottomNavigationScreenState =
                        localCurrentBottomNavigationScreenState.copy(
                            topBarVisible = false,
                            bottomBarVisible = true,
                            topAppBarScrollBehavior = null,
                            topBarColor = null,
                            prevFabState = localCurrentBottomNavigationScreenState.fabState.copy(),
                            fabState = localCurrentBottomNavigationScreenState.fabState.copy(
                                floatingActionButtonVisible = true,
                                floatingActionButtonClicked = {
                                    navController.navigate(Route.ADD_MY_DRINK)
                                },
                                floatingActionButtonIcon = Icons.Default.Add,
                                floatingActionButtonLabel = addText,
                                floatingActionButtonMultiChoice = null
                            )
                        )
                    onComposed(
                        localCurrentBottomNavigationScreenState
                    )
                }
            }
        }
    }
    LaunchedEffect(key1 = viewModel.state.selectedTab) {
        pagerState.animateScrollToPage(viewModel.state.selectedTab)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedDrinkDrawable == null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.bar_bg_5_b_small)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                alpha = 0.6f
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(selectedDrinkDrawable!!)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = 2.dp
                    ),
                contentScale = ContentScale.FillHeight,
                alpha = 0.4f,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.4f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        )
                    )
                ),
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,

                ) {
                Tab(
                    selected = viewModel.state.selectedTab == 0,
                    onClick = {
                        viewModel.onEvent(CocktailTabEvent.OnTabClicked(0))
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.search)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }
                )
                Tab(
                    selected = viewModel.state.selectedTab == 1,
                    onClick = {
                        viewModel.onEvent(CocktailTabEvent.OnTabClicked(1))
                    },
                    text = {
                        Text(
                            text = stringResource(id = R.string.my_cocktails)
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.LocalDrink,
                            contentDescription = stringResource(id = R.string.my_cocktails)
                        )
                    }
                )
            }
            HorizontalPager(
                modifier = Modifier.weight(1f),
                count = 2,
                state = pagerState
            ) { page ->
                when (page) {
                    0 -> {
                        // Ingredient which is got when ingredient is selected from the whole list of ingredients
                        val ingredient = navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.get<String>("ingredient")

                        SearchDrinkScreen(
                            snackbarHostState = snackbarHostState,
                            onComposed = { state ->

                            },
                            //paddingValues = values,
                            currentBottomNavigationScreenState = currentBottomNavigationScreenState,
                            onIngredientListClicked = {
                                navController.navigate("${Route.INGREDIENTS}/true")
                            },
                            ingredient = ingredient ?: ingredientForSearch,
                            onDrinkClicked = { id, color, name ->
                                navController.navigate("${Route.DRINK_DETAILS}/$color/$id/$name")
                            },
                            onSelectedDrinkDrawableLoaded = {
                                selectedDrinkDrawable = it
                            }
                        )
                    }
                    1 -> {
                        MyDrinkScreen(
                            onDrinkClicked = {_, _, _ ->

                            },
                            onSelectedDrinkDrawableLoaded = {

                            },
                        )
                    }
                }
            }
        }
    }
}