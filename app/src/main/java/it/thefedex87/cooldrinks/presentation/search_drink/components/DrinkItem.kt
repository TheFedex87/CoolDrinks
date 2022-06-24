package it.thefedex87.cooldrinks.presentation.search_drink.components

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.util.Consts.TAG

@Composable
fun DrinkItem(
    drink: DrinkUiModel,
    modifier: Modifier = Modifier,
    onItemClick: (Int, Int, String) -> Unit,
    onFavoriteClick: (DrinkUiModel) -> Unit,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit
) {
    val spacing = LocalSpacing.current
    val defaultDominantColor = MaterialTheme.colorScheme.background
    var dominatorColor by remember {
        if (drink.dominantColor == 0) {
            mutableStateOf(defaultDominantColor)
        } else {
            mutableStateOf(Color(drink.dominantColor))
        }
    }

    val animatedDominantColor = animateColorAsState(
        targetValue = dominatorColor,
        animationSpec = tween(
            durationMillis = 400
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(108.dp)
            .padding(horizontal = spacing.spaceExtraSmall, vertical = 2.dp)
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        animatedDominantColor.value,
                        defaultDominantColor
                    )
                ),//MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
            .clickable {
                onItemClick(
                    drink.id,
                    dominatorColor.toArgb(),
                    drink.name
                )
            }
            .clip(MaterialTheme.shapes.small),
    ) {
        /*val image = rememberImagePainter(
            data = drink.image,
            builder = {
                crossfade(true)
                error(R.drawable.drink)
                fallback(R.drawable.drink)
            }
        )

        Log.d(TAG, "Composing drink item")

        (image.state as? ImagePainter.State.Success)?.let { successResult ->
            LaunchedEffect(key1 = true) {
                if (drink.dominantColor == 0) {
                    Log.d(TAG, "Calculate dominant color")
                    calcDominantColor(successResult.result.drawable) { color ->
                        dominatorColor = color
                    }
                }
            }
        }*/

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant
                    /*Brush.horizontalGradient(
                        listOf(
                            defaultDominantColor,
                            animatedDominantColor.value
                        )
                    )*/
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = spacing.spaceMedium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = drink.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if(drink.isLoadingFavorite) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Icon(
                            imageVector = if(drink.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    onFavoriteClick(drink)
                                }
                        )
                    }
                }

                AsyncImage(
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp)
                        .clip(
                            RoundedCornerShape(
                                5.dp
                            )
                        ),
                    onSuccess = {
                        if (drink.dominantColor == 0) {
                            Log.d(TAG, "Calculate dominant color")
                            calcDominantColor(it.result.drawable) { color ->
                                dominatorColor = color
                            }
                        }
                    },
                    onLoading = {
                        R.drawable.drink
                    },
                    onError = {
                        R.drawable.drink
                    },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(drink.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = drink.name,
                    contentScale = ContentScale.FillHeight
                )
            }
        }

    }
}