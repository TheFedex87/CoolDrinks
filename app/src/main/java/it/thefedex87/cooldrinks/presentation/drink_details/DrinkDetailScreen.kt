package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.drink_details.components.CharacteristicSection
import it.thefedex87.cooldrinks.presentation.drink_details.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.calcDominantColor
import it.thefedex87.cooldrinks.util.Consts
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

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
                                    contentDescription = stringResource(id = R.string.get_random_cocktail)
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
                        dominantColor!!
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
        /* Column(
             modifier = Modifier
                 .fillMaxSize()
                 .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
                 .padding(horizontal = spacing.spaceSmall)
                 .verticalScroll(rememberScrollState())
         ) {*/
        val closeCardHeight = 62.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
                    .align(Alignment.TopStart)
            ) {
                var expandedIndex by remember {
                    mutableStateOf(0)
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxSize(),
                    onClick = {
                        expandedIndex = 0
                    },
                    shape = RoundedCornerShape(31.dp)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .animateContentSize()
                            .padding(top = 180.dp)
                    ) {
                        val maxHeight = this.maxHeight
                        val expandedHeight = maxHeight - closeCardHeight * 3

                        val anim1 = remember {
                            androidx.compose.animation.core.Animatable(1f)
                        }
                        val anim2 = remember {
                            androidx.compose.animation.core.Animatable(0f)
                        }
                        val anim3 = remember {
                            androidx.compose.animation.core.Animatable(0f)
                        }

                        LaunchedEffect(key1 = expandedIndex) {
                            when(expandedIndex) {
                                0 -> {
                                    awaitAll(
                                        async { anim1.animateTo(1f) },
                                        async { anim2.animateTo(0f) },
                                        async { anim3.animateTo(0f) }
                                    )
                                }
                                1 -> {
                                    awaitAll(
                                        async { anim1.animateTo(0f) },
                                        async { anim2.animateTo(1f) },
                                        async { anim3.animateTo(0f) }
                                    )
                                }
                                else -> {
                                    awaitAll(
                                        async { anim1.animateTo(0f) },
                                        async { anim2.animateTo(0f) },
                                        async { anim3.animateTo(1f) }
                                    )
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                text = "About",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier
                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                    .height(closeCardHeight - 16.dp)
                            )
                            CharacteristicSection(
                                drinkGlass = viewModel.state.drinkGlass,
                                drinkCategory = viewModel.state.drinkCategory,
                                drinkAlcoholic = viewModel.state.drinkAlcoholic,
                                modifier = Modifier
                                    .height(
                                        expandedHeight * anim1.value
                                    )
                            )
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    expandedIndex = 1
                                },
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                shape = RoundedCornerShape(31.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Ingredients",
                                        style = MaterialTheme.typography.headlineSmall,
                                        modifier = Modifier
                                            .padding(vertical = 8.dp, horizontal = 16.dp)
                                            .height(closeCardHeight - 16.dp)
                                    )
                                    //Spacer(modifier = Modifier.height(spacing.spaceSmall))
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .height(
                                            expandedHeight * anim2.value
                                        )) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .let {
                                                    if(expandedIndex == 1) {
                                                        it.verticalScroll(rememberScrollState())
                                                    } else it
                                                }

                                        ) {
                                            viewModel.state.drinkIngredients?.map {
                                                IngredientItem(
                                                    ingredientName = it.first,
                                                    ingredientMeasure = it.second
                                                )
                                            }
                                        }
                                    }

                                    ElevatedCard(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        onClick = {
                                            expandedIndex = 2
                                        },
                                        colors = CardDefaults.elevatedCardColors(
                                            containerColor = MaterialTheme.colorScheme.inverseSurface
                                        ),
                                        shape = RoundedCornerShape(31.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Instructions",
                                                style = MaterialTheme.typography.headlineSmall,
                                                modifier = Modifier
                                                    .padding(vertical = 8.dp, horizontal = 16.dp)
                                                    .height(closeCardHeight - 16.dp)
                                            )
                                            Box(modifier = Modifier
                                                .padding(8.dp)
                                                .height(expandedHeight * anim3.value)
                                            ) {
                                                viewModel.state.drinkInstructions?.let {
                                                    Text(
                                                        modifier = Modifier
                                                            .height(
                                                                expandedHeight * anim3.value
                                                            )
                                                            .let {
                                                                if(expandedIndex == 2) {
                                                                    it.verticalScroll(state = rememberScrollState())
                                                                } else it
                                                            },
                                                        text = it
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
        //}
    }
}