package it.thefedex87.cooldrinks.presentation.components.cocktail

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerScope
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel
import it.thefedex87.cooldrinks.util.Consts

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun PagerScope.PagerDrinkItem(
    drink: DrinkUiModel,
    modifier: Modifier = Modifier,
    onItemClick: (Int, Int, String) -> Unit,
    pageOffset: Float,
    onFavoriteClick: (DrinkUiModel) -> Unit,
    onImageLoaded: (Drawable) -> Unit,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit
) {
    ElevatedCard(modifier = modifier
        .fillMaxSize()
        .clickable {
            onItemClick(
                drink.id,
                drink.dominantColor,
                drink.name
            )
        }
        .padding(8.dp)
        .graphicsLayer {
            // Calculate the absolute offset for the current page from the
            // scroll position. We use the absolute value which allows us to mirror
            // any effects for both directions

            // We animate the scaleX + scaleY, between 85% and 100%
            lerp(
                start = 0.85f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            ).also { scale ->
                scaleX = scale
                scaleY = scale
            }

            // We animate the alpha, between 50% and 100%
            alpha = lerp(
                start = 0.5f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )
        }) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val constraints = this
            Column(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                5.dp
                            )
                        )
                        .fillMaxWidth()
                        .let {
                            if (constraints.maxWidth > 600.dp) {
                                it.weight(1.5f)
                            } else {
                                it.weight(1f)
                            }
                        },
                    onSuccess = {
                        onImageLoaded(it.result.drawable)
                        if (drink.dominantColor == 0) {
                            Log.d(Consts.TAG, "Calculate dominant color")
                            calcDominantColor(it.result.drawable) { color ->
                                //dominatorColor = color
                            }
                        }
                    },
                    onLoading = {
                        R.drawable.search_background
                    },
                    onError = {
                        R.drawable.search_background
                    },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(drink.image)
                        .crossfade(true)
                        .build(),
                    contentDescription = drink.name,
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .let {
                            if (constraints.maxWidth > 600.dp) {
                                it.weight(0.5f)
                            } else {
                                it.weight(1f)
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 4.dp),
                            text = drink.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        BoxWithConstraints(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (drink.isLoadingFavorite) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(if (this.maxHeight > 30.dp) 30.dp else this.maxHeight)
                                )
                            } else {
                                Icon(
                                    imageVector = if (drink.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .clickable {
                                            onFavoriteClick(drink)
                                        }
                                        .size(if (this.maxHeight > 100.dp) 100.dp else this.maxHeight - 10.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}