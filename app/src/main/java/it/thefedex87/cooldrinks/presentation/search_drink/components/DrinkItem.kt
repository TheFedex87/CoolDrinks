package it.thefedex87.cooldrinks.presentation.search_drink.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.ImageLoader
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
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominatorColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
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
            calcDominantColor(successResult.result.drawable) { color ->
                dominatorColor = color
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            defaultDominantColor,
                            dominatorColor
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
                        style = MaterialTheme.typography.h5,
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