package it.thefedex87.cooldrinks.presentation.search_drink

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.components.SearchTextField
import it.thefedex87.cooldrinks.presentation.components.cocktail.CocktailView
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onIngredientListClicked: () -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    onSelectedDrinkDrawableLoaded: (Drawable?) -> Unit,
    //paddingValues: PaddingValues,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    ingredient: String? = null,
    viewModel: SearchDrinkViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val spacing = LocalSpacing.current
    val keyboardController = LocalSoftwareKeyboardController.current

    /*var selectedDrinkDrawable by remember {
        mutableStateOf<Drawable?>(null)
    }

    if (
        viewModel.state.visualizationType == VisualizationType.Card &&
        viewModel.state.foundDrinks.isNotEmpty() &&
        !viewModel.state.isLoading
    ) {
        if (selectedDrinkDrawable != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            )
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
    }*/
    if (
        viewModel.state.visualizationType == VisualizationType.List ||
        viewModel.state.foundDrinks.isEmpty()
    ) {
        onSelectedDrinkDrawableLoaded(null)
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

        if (!viewModel.state.isLoading) {

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

                    val onVisualizationTypeChangedLambda = remember<(VisualizationType) -> Unit> {
                        {
                            viewModel.onEvent(SearchDrinkEvent.OnVisualizationTypeChange(it))
                        }
                    }

                    val onFavoriteClickedLambda = remember<(DrinkUiModel) -> Unit> {
                        {
                            viewModel.onEvent(SearchDrinkEvent.OnFavoriteClick(it))
                        }
                    }

                    CocktailView(
                        maxHeight = constraints.maxHeight,
                        maxWidth = constraints.maxWidth,
                        isLoading = viewModel.state.isLoading,
                        drinks = viewModel.state.foundDrinks,
                        visualizationType = viewModel.state.visualizationType,
                        onDrinkClicked = onDrinkClicked,
                        onFavoriteClicked = onFavoriteClickedLambda,
                        onVisualizationTypeChanged = onVisualizationTypeChangedLambda,
                        onSelectDrinkDrawableChanged = {
                            //selectedDrinkDrawable = it
                            if (
                                viewModel.state.visualizationType == VisualizationType.Card &&
                                viewModel.state.foundDrinks.isNotEmpty() &&
                                !viewModel.state.isLoading
                            ) {
                                onSelectedDrinkDrawableLoaded(it)
                            }
                        }
                    )
                } else {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        EmptyList(
                            icon = Icons.Default.Search,
                            text = stringResource(id = R.string.search_for_drink)
                        )
                    }
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}