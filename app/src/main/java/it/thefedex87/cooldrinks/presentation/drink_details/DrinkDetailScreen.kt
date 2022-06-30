package it.thefedex87.cooldrinks.presentation.drink_details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.drink_details.components.CharacteristicSection
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    dominantColor: Color,
    drinkId: Int,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: DrinkDetailViewModel = hiltViewModel()
) {
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
                bottomBarVisible = false,
                topAppBarScrollBehavior = {
                    appBarScrollBehavior
                },
                topBarActions = {
                    IconButton(onClick = {
                        viewModel.onEvent(DrinkDetailEvent.FavoriteClicked)
                    }) {
                        Icon(
                            imageVector = if (viewModel.state.isFavorite == false)
                                Icons.Default.FavoriteBorder else
                                Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
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
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.spaceSmall)
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 150.dp,
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
                }
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(dominantColor.copy(alpha = 0.7f)),
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(viewModel.state.drinkImagePath)
                            .crossfade(true)
                            .build(),
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(200.dp)
                            .align(Alignment.Center),
                        onLoading = {
                            R.drawable.drink
                        },
                        onError = {
                            R.drawable.drink
                        },
                        contentDescription = viewModel.state.drinkName
                    )
                }
            }
        }
    }
}