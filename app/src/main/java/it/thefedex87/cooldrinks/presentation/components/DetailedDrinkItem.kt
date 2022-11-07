package it.thefedex87.cooldrinks.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedDrinkItem(
    drink: DrinkDetailDomainModel,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onFavoriteChangeClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    /*val image = rememberImagePainter(
        data = drink.drinkThumb,
        builder = {
            crossfade(true)
            error(R.drawable.drink)
            fallback(R.drawable.drink)
        }
    )*/
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val constraints = this
        Card(
            modifier = modifier
                .fillMaxWidth()
                .let {
                    if (constraints.maxWidth > 600.dp) {
                        it.height(600.dp)
                    } else {
                        it.height(390.dp)
                    }
                }
                .clickable {
                    onDrinkClicked(drink.idDrink, drink.dominantColor!!, drink.name)
                },
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(drink.drinkThumb)
                        .crossfade(true)
                        .build(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .let {
                            if (constraints.maxWidth > 600.dp) {
                                it.weight(4f)
                            } else {
                                it.weight(1.2f)
                            }
                        }
                        .clip(RoundedCornerShape(12.dp)),
                    onLoading = {
                        R.drawable.search_background
                    },
                    onError = {
                        R.drawable.search_background
                    },
                    contentDescription = drink.name,
                    contentScale = ContentScale.FillWidth
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(spacing.spaceSmall),
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = drink.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Light)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            mainAxisSpacing = 8.dp,
                            mainAxisSize = SizeMode.Expand
                        ) {
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(
                                        text = if (drink.isAlcoholic) stringResource(id = R.string.alcoholic) else stringResource(
                                            id = R.string.non_alcoholic
                                        )
                                    )
                                }
                            )
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(text = drink.category)
                                }
                            )
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(text = drink.glass)
                                }
                            )
                        }
                    }
                }
                Text(
                    modifier = Modifier.padding(horizontal = spacing.spaceMedium),
                    text = "${drink.ingredients.count { it.isAvailable == false }} ${
                        stringResource(
                            id = R.string.missing_ingredients
                        )
                    }",
                    color = if (drink.ingredients.count { it.isAvailable == false } == 0) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
                IconButton(
                    modifier = Modifier
                        .padding(spacing.spaceSmall)
                        .align(alignment = Alignment.End),
                    onClick = {
                        onFavoriteChangeClicked(!drink.isFavorite)
                    }
                ) {
                    Icon(
                        imageVector = if (drink.isFavorite)
                            Icons.Default.Favorite
                        else
                            Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(
                            id = if (drink.isFavorite) R.string.unfavorite else R.string.remove
                        )
                    )
                }
            }
        }
    }
}