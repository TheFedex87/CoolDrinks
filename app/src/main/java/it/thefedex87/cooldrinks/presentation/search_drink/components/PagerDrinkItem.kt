package it.thefedex87.cooldrinks.presentation.search_drink.components

import android.graphics.drawable.Drawable
import android.util.Log
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.search_drink.model.DrinkUiModel
import it.thefedex87.cooldrinks.util.Consts

@Composable
fun PagerDrinkItem(
    drink: DrinkUiModel,
    modifier: Modifier = Modifier,
    onItemClick: (Int, Int, String) -> Unit,
    onFavoriteClick: (DrinkUiModel) -> Unit,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        5.dp
                    )
                ),
            onSuccess = {
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
        Box(modifier = Modifier
            .weight(1f)
            .fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.TopCenter),
                text = drink.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if(drink.isLoadingFavorite) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                )
            } else {
                Icon(
                    imageVector = if(drink.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            onFavoriteClick(drink)
                        }
                        .align(Alignment.Center)
                )
            }
        }

    }
}