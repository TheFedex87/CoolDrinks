package it.thefedex87.cooldrinks.presentation.search_drink

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.components.SearchTextField
import it.thefedex87.cooldrinks.presentation.components.cocktail.CocktailView
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@ExperimentalComposeUiApi
@Composable
fun SearchDrinkScreen(
    state: SearchDrinkState,
    onEvent: (SearchDrinkEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onIngredientListClicked: () -> Unit,
    onComposed: (BottomNavigationScreenState) -> Unit,
    onSelectedDrinkDrawableLoaded: (Drawable?) -> Unit,
    //paddingValues: PaddingValues,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    ingredient: String? = null
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
        state.visualizationType == VisualizationType.List ||
        state.foundDrinks.isEmpty()
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
            onEvent(SearchDrinkEvent.OnIngredientPassed(it))
            onEvent(SearchDrinkEvent.OnSearchClick)
        }

        uiEvent.collect {
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
                else -> Unit
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val constraints = this

        if (!state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SearchTextField(
                    text = state.searchQuery,
                    showHint = state.showSearchHint,
                    onSearch = {
                        keyboardController?.hide()
                        onEvent(SearchDrinkEvent.OnSearchClick)
                    },
                    onValueChanged = {
                        onEvent(SearchDrinkEvent.OnSearchQueryChange(it))
                    },
                    onFocusChanged = {
                        onEvent(SearchDrinkEvent.OnSearchFocusChange(it.isFocused))
                    },
                    trailingIcon = Icons.Default.List,
                    trailingIconOnClick = onIngredientListClicked,
                    modifier = Modifier.padding(
                        horizontal = spacing.spaceExtraSmall,
                        vertical = spacing.spaceSmall
                    )
                )

                if (state.foundDrinks.isNotEmpty()) {
                    val onVisualizationTypeChangedLambda = remember<(VisualizationType) -> Unit> {
                        {
                            onEvent(SearchDrinkEvent.OnVisualizationTypeChange(it))
                        }
                    }

                    val onFavoriteClickedLambda = remember<(DrinkUiModel) -> Unit> {
                        {
                            onEvent(SearchDrinkEvent.OnFavoriteClick(it))
                        }
                    }

                    val onSelectDrinkDrawableChangedLambda = remember<(Drawable?) -> Unit> {
                        {
                            if (
                                state.visualizationType == VisualizationType.Card &&
                                state.foundDrinks.isNotEmpty()
                            ) {
                                onSelectedDrinkDrawableLoaded(it)
                            }
                        }
                    }

                    CocktailView(
                        maxHeight = constraints.maxHeight,
                        maxWidth = constraints.maxWidth,
                        isLoading = false,
                        drinks = state.foundDrinks,
                        visualizationType = state.visualizationType,
                        onDrinkClicked = onDrinkClicked,
                        onFavoriteClicked = onFavoriteClickedLambda,
                        onVisualizationTypeChanged = onVisualizationTypeChangedLambda,
                        onSelectDrinkDrawableChanged = onSelectDrinkDrawableChangedLambda
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