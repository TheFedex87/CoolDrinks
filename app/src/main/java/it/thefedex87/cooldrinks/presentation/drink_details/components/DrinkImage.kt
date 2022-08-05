package it.thefedex87.cooldrinks.presentation.drink_details.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailEvent
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailViewModel
import it.thefedex87.cooldrinks.util.Consts

@Composable
fun BoxScope.DrinkImage(
    imageSize: Double,
    calculatedDominantColor: Color?,
    viewModel: DrinkDetailViewModel
) {
    Box(
        modifier = Modifier
            .align(alignment = Alignment.TopCenter)
            .size(imageSize.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.background),
    ) {
        Box(
            modifier = Modifier
                .align(alignment = Alignment.Center)
                .size((imageSize - 10).dp)
                .clip(CircleShape)
                .background(
                    (if (viewModel.state.drinkDominantColor != null)
                        Color(viewModel.state.drinkDominantColor!!) else
                        MaterialTheme.colorScheme.onBackground).copy(alpha = 0.6f)
                ),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(viewModel.state.drinkImagePath)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .clip(CircleShape)
                    .size((imageSize - 25).dp)
                    .align(Alignment.Center),
                onLoading = {
                    R.drawable.search_background
                },
                onSuccess = {
                    if (calculatedDominantColor == null) {
                        Log.d(
                            Consts.TAG,
                            "Calculate dominant color inside drink details screen"
                        )
                        viewModel.onEvent(DrinkDetailEvent.DrawableLoaded(it.result.drawable))
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