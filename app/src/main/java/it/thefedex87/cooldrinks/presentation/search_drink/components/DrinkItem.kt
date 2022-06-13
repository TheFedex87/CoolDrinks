package it.thefedex87.cooldrinks.presentation.search_drink.components

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalCoilApi::class)
@Composable
fun DrinkItem(
    drink: DrinkDomainModel,
    modifier: Modifier = Modifier,
    onItemClick: (Int, Int) -> Unit,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit
) {
    val spacing = LocalSpacing.current
    val defaultDominantColor = MaterialTheme.colorScheme.background
    var dominatorColor by remember {
        if(drink.dominantColor == 0) {
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
            .height(100.dp)
            .padding(vertical = spacing.spaceExtraSmall)
            .clickable {
                onItemClick(
                    drink.id,
                    dominatorColor.toArgb()
                )
            },
    ) {
        val image = rememberImagePainter(
            data = drink.image,
            builder = {
                crossfade(true)
                error(R.drawable.drink)
                fallback(R.drawable.drink)
            }
        )

        (image.state as? ImagePainter.State.Success)?.let { successResult ->
            LaunchedEffect(key1 = true) {
                if(drink.dominantColor == 0) {
                    Log.d("COOL_DRINKS", "Calculate dominant color")
                    calcDominantColor(successResult.result.drawable) { color ->
                        dominatorColor = color
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            defaultDominantColor,
                            animatedDominantColor.value
                        )
                    )
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
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Light,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite"
                    )
                }

                Image(
                    modifier = Modifier
                        .width(90.dp)
                        .height(90.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 5.dp,
                                bottomStart = 5.dp
                            )
                        ),
                    painter = image,
                    contentDescription = drink.name,
                    contentScale = ContentScale.FillHeight
                )
            }
        }

    }
}