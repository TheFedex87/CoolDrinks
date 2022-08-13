package it.thefedex87.cooldrinks.presentation.search_drink

import android.graphics.drawable.Drawable
import androidx.compose.animation.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.components.SearchTextField
import it.thefedex87.cooldrinks.presentation.search_drink.components.DrinkItem
import it.thefedex87.cooldrinks.presentation.search_drink.components.PagerDrinkItem
import it.thefedex87.cooldrinks.presentation.search_drink.components.VisualizationTypeSelector
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.presentation.util.calcDominantColor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onIngredientListClicked: () -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    paddingValues: PaddingValues,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    ingredient: String? = null,
    viewModel: SearchDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val defaultDominantColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    val animatedDominantColor = remember {
        Animatable(defaultDominantColor)
    }

    LaunchedEffect(key1 = dominantColor) {
        animatedDominantColor.animateTo(dominantColor)
    }

    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                topBarVisible = false,
                bottomBarVisible = true,
                topAppBarScrollBehavior = null,
                topBarColor = null,
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = false
                )
            )
        )

        ingredient?.let {
            viewModel.onEvent(SearchDrinkEvent.OnSearchQueryChange(it))
            viewModel.onEvent(SearchDrinkEvent.OnSearchClick)
        }

        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.ShowSnackBar -> {
                    keyboardController?.hide()
                    snackbarHostState.currentSnackbarData?.dismiss()
                    launch {
                        snackbarHostState.showSnackbar(
                            message = it.message.asString(context),
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                else -> {}
            }
        }
    }
    
    var selectedDrinkDrawable by remember {
        mutableStateOf<Drawable?>(null)
    }

    if(viewModel.state.visualizationType == VisualizationType.Card && viewModel.state.foundDrinks.isNotEmpty()) {
        if (selectedDrinkDrawable != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(selectedDrinkDrawable!!)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        radius = 6.dp
                    ),
                contentScale = ContentScale.FillHeight,
                alpha = 0.4f,
            )
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.bar_bg_5_b_small)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 0.6f
        )
    }


    /*BubblesBackGround(
        color = animatedDominantColor.value,
        bottomPadding = paddingValues.calculateBottomPadding()
    )*/
    

    val pagerState = rememberPagerState()
    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect {
            if (viewModel.state.foundDrinks.isNotEmpty()) {
                dominantColor =
                    Color(viewModel.state.foundDrinks[it].value.dominantColor).copy(alpha = 0.8f)
                selectedDrinkDrawable = viewModel.state.foundDrinks[it].value.imageDrawable
            }
        }
    }

    val columnState = rememberLazyListState()

    /*Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //if (viewModel.state.showNoDrinkFound)
        //    Text(text = stringResource(id = R.string.no_drink_found))
        Image(
            painter = painterResource(id = R.drawable.search_background),
            contentDescription = null,
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.Center),
            alpha = if (viewModel.state.foundDrinks.isEmpty()) 1f else 0.5f
        )
        if (viewModel.state.isLoading)
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(spacing.spaceMedium)
            )
    }*/

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val constraints = this

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
                trailingIcon = Icons.Default.List,
                trailingIconOnClick = onIngredientListClicked,
                modifier = Modifier.padding(
                    horizontal = spacing.spaceExtraSmall,
                    vertical = spacing.spaceSmall
                )
            )

            if (viewModel.state.foundDrinks.isNotEmpty()) {
                if (constraints.maxHeight > 500.dp &&
                    viewModel.state.visualizationType == VisualizationType.Card
                ) {
                    HorizontalPager(
                        modifier = Modifier.weight(1f),
                        count = viewModel.state.foundDrinks.size,
                        contentPadding = PaddingValues(
                            if (constraints.maxWidth > 600.dp) {
                                128.dp
                            } else {
                                64.dp
                            }
                        ),
                        state = pagerState
                    ) { page ->
                        if (page <= viewModel.state.foundDrinks.lastIndex) {
                            val drink = viewModel.state.foundDrinks[page]
                            PagerDrinkItem(
                                drink = drink.value,
                                onItemClick = { id, color, name ->
                                    onDrinkClicked(id, color, name)
                                },
                                page = page,
                                onFavoriteClick = {
                                    viewModel.onEvent(SearchDrinkEvent.OnFavoriteClick(it))
                                },
                                calcDominantColor = { drawable, onFinish ->
                                    calcDominantColor(drawable, drink, onFinish)
                                },
                                onImageLoaded = {
                                    drink.value = drink.value.copy(imageDrawable = it)
                                    if(page == pagerState.currentPage) {
                                        selectedDrinkDrawable = it
                                    }
                                }
                            )
                        }
                    }
                } else if (constraints.maxHeight <= 500.dp ||
                    viewModel.state.visualizationType == VisualizationType.List
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = columnState
                    ) {
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
                                    calcDominantColor(drawable, drink, onFinish)
                                }
                            )
                        }
                    }
                }

                if (constraints.maxHeight > 500.dp) {
                    VisualizationTypeSelector(
                        selectedVisualizationType = viewModel.state.visualizationType,
                        onClick = {
                            viewModel.onEvent(SearchDrinkEvent.OnVisualizationTypeChange(it))
                        }
                    )
                }

            }
        }
    }
}