package it.thefedex87.cooldrinks.presentation.bar.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.pager.PagerScope
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class, com.google.accompanist.pager.ExperimentalPagerApi::class)
@Composable
fun PagerScope.BarIngredientItem(
    ingredient: IngredientDetailsDomainModel,
    pageOffset: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(Color.Transparent)
            .graphicsLayer {
                // Calculate the absolute offset for the current page from the
                // scroll position. We use the absolute value which allows us to mirror
                // any effects for both directions
                //val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                // We animate the scaleX + scaleY, between 85% and 100%
                lerp(
                    start = 0.7f,
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
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.weight(1f))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(ingredient.imagePath ?: R.drawable.search_background )
                    .crossfade(true)
                    .build(),
                onLoading = {
                    //R.drawable.search_background
                },
                contentScale = ContentScale.FillHeight,
                onSuccess = {

                },
                onError = {
                    R.drawable.search_background
                },
                contentDescription = ingredient.name,
                modifier = Modifier.weight(5f)
            )
        }
    }
}