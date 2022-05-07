package it.thefedex87.networkresponsestateeventtest.presentation.search_drink.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import it.thefedex87.networkresponsestateeventtest.R
import it.thefedex87.networkresponsestateeventtest.domain.model.DrinkDomainModel
import it.thefedex87.networkresponsestateeventtest.presentation.ui.theme.LocalSpacing

@Composable
fun DrinkItem(
    drink: DrinkDomainModel,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(6.dp))
            .padding(
                horizontal = spacing.spaceExtraSmall,
                vertical = spacing.spaceSmall
            ),
        elevation = 1.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Image(
            painter = rememberImagePainter(
                data = drink.image,
                builder = {
                    crossfade(true)
                    error(R.drawable.drink)
                    fallback(R.drawable.drink)
                }
            ),
            contentDescription = drink.name,
            contentScale = ContentScale.Crop
        )
    }
}