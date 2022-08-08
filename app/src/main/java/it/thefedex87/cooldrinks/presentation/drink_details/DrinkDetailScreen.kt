package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.util.Consts.TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    calculatedDominantColor: Color?,
    drinkId: Int?,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    onDrinkLoaded: ((String) -> Unit)?,
    viewModel: DrinkDetailViewModel = hiltViewModel()
) {
    /*var dominantColor by remember {
        mutableStateOf(calculatedDominantColor)
    }*/

    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())

    if (viewModel.state.showConfirmRemoveFavoriteDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(DrinkDetailEvent.RemoveFromFavoriteCanceled)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onEvent(DrinkDetailEvent.RemoveFromFavoriteConfirmed)
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(DrinkDetailEvent.RemoveFromFavoriteCanceled)
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

    LaunchedEffect(key1 = viewModel.state.isFavorite) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                topBarVisible = true,
                bottomBarVisible = drinkId == null,
                topAppBarScrollBehavior = {
                    appBarScrollBehavior
                },
                topBarActions = {
                    Row {
                        if (drinkId == null) {
                            IconButton(onClick = {
                                viewModel.onEvent(DrinkDetailEvent.GetRandomCocktail)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(id = R.string.get_random_cocktail)
                                )
                            }
                        }
                        IconButton(onClick = {
                            viewModel.onEvent(DrinkDetailEvent.FavoriteClicked)
                        }) {
                            Icon(
                                imageVector = if (viewModel.state.isFavorite == true)
                                    Icons.Default.Favorite else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                },
                topBarShowBack = drinkId != null,
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = false
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy()
                //drinkId == null,
                //floatingActionButtonLabel = null,//if(drinkId != null) null else "Random Drink",
                /*floatingActionButtonClicked = {
                    //viewModel.onEvent(DrinkDetailEvent.GetRandomCocktail)
                }*/
            )
        )
    }

    /*Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        dominantColor!!
                    )
                )
            )
    )*/

    if (viewModel.state.isLoading) {
        //dominantColor = null
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        onDrinkLoaded?.invoke(viewModel.state.drinkName!!)

        val screenHeightInPx = with(LocalDensity.current) { LocalConfiguration.current.screenHeightDp.dp }
        Log.d(TAG, "Screen height in dp: ${LocalConfiguration.current.screenHeightDp} - screen height in px: $screenHeightInPx")

        val screenWidth = LocalConfiguration.current.screenWidthDp
        val imageSize: Double = if (screenWidth * 0.60 > 245) 245.0 else screenWidth * 0.60

        if (screenHeightInPx > 680.dp) {
            DrinkDetailScreenAdvanced(
                calculatedDominantColor = calculatedDominantColor,
                viewModel = viewModel,
                imageSize = imageSize
            )
        } else {
            DrinkDetailScreenSimplified(
                calculatedDominantColor = calculatedDominantColor,
                appBarScrollBehavior = appBarScrollBehavior,
                viewModel = viewModel,
                imageSize = imageSize
            )
        }
    }
}