package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.drink_details.components.CharacteristicSection
import it.thefedex87.cooldrinks.presentation.drink_details.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.ui.theme.Red40
import it.thefedex87.cooldrinks.presentation.util.calcDominantColor
import it.thefedex87.cooldrinks.util.Consts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    calculatedDominantColor: Color?,
    drinkId: Int?,
    onComposed: (BottomNavigationScreenState) -> Unit,
    onDrinkLoaded: ((String) -> Unit)?,
    viewModel: DrinkDetailViewModel = hiltViewModel()
) {
    var dominantColor by remember {
        mutableStateOf(calculatedDominantColor)
    }

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
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.onEvent(DrinkDetailEvent.RemoveFromFavoriteCanceled)
                }) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(text = "Confirm Remove")
            },
            text = {
                Text(text = "Do you really want remove this drink from the favorites?")
            }
        )
    }

    LaunchedEffect(key1 = viewModel.state.isFavorite) {
        onComposed(
            BottomNavigationScreenState(
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
                                    contentDescription = "Get new random cocktail"
                                )
                            }
                        }
                        IconButton(onClick = {
                            viewModel.onEvent(DrinkDetailEvent.FavoriteClicked)
                        }) {
                            Icon(
                                imageVector = if (viewModel.state.isFavorite == false)
                                    Icons.Default.FavoriteBorder else
                                    Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                },
                topBarShowBack = drinkId != null,
                floatingActionButtonVisible = false,//drinkId == null,
                floatingActionButtonLabel = null,//if(drinkId != null) null else "Random Drink",
                floatingActionButtonClicked = {
                    //viewModel.onEvent(DrinkDetailEvent.GetRandomCocktail)
                }
            )
        )
    }

    val spacing = LocalSpacing.current

    /*Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        dominantColor
                    )
                )
            )
    )*/

    if (viewModel.state.isLoading) {
        dominantColor = null
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        onDrinkLoaded?.invoke(viewModel.state.drinkName!!)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
                .padding(horizontal = spacing.spaceSmall)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.TopCenter)
                        .padding(
                            top = 80.dp,
                            start = spacing.spaceSmall,
                            end = spacing.spaceSmall,
                            bottom = spacing.spaceSmall
                        ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 180.dp,
                                    start = spacing.spaceSmall,
                                    end = spacing.spaceSmall,
                                    bottom = spacing.spaceSmall
                                )
                        ) {
                            CharacteristicSection(
                                drinkGlass = viewModel.state.drinkGlass,
                                drinkCategory = viewModel.state.drinkCategory,
                                drinkAlcoholic = viewModel.state.drinkAlcoholic
                            )
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = spacing.spaceSmall,
                                    end = spacing.spaceSmall,
                                    bottom = spacing.spaceSmall
                                ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(spacing.spaceSmall),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                                viewModel.state.drinkIngredients?.map {
                                    IngredientItem(
                                        ingredientName = it.first,
                                        ingredientMeasure = it.second
                                    )
                                }
                            }
                        }
                        viewModel.state.drinkInstructions?.let { instructions ->
                            Text(
                                modifier = Modifier
                                    .padding(spacing.spaceMedium),
                                text = instructions
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .size(245.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .size(235.dp)
                            .clip(CircleShape)
                            .background(
                                (dominantColor
                                    ?: MaterialTheme.colorScheme.background).copy(alpha = 0.6f)
                            ),
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(viewModel.state.drinkImagePath)
                                .crossfade(true)
                                .build(),
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(220.dp)
                                .align(Alignment.Center),
                            onLoading = {
                                R.drawable.search_background
                            },
                            onSuccess = {
                                if (calculatedDominantColor == null) {
                                    Log.d(Consts.TAG, "Calculate dominant color")
                                    calcDominantColor(it.result.drawable, null) { color ->
                                        dominantColor = color
                                    }
                                }
                            },
                            onError = {
                                R.drawable.search_background
                            },
                            contentDescription = viewModel.state.drinkName
                        )
                    }
                }
            }
            /*if (drinkId == null) {
                Spacer(modifier = Modifier.height(70.dp))
            }*/
        }
    }
}