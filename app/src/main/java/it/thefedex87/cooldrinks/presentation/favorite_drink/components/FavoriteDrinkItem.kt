package it.thefedex87.cooldrinks.presentation.favorite_drink.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDrinkItem(
    drink: DrinkDetailDomainModel,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onUnfavoriteClicked: () -> Unit,
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(390.dp)
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
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp)),
                onLoading = {
                    R.drawable.drink
                },
                onError = {
                    R.drawable.drink
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
                                Text(text = if (drink.isAlcoholic) "Alcoholic" else "Not Alcoholic")
                            },
                            /*icon = {
                                Icon(
                                    imageVector = if (drink.isAlcoholic) Icons.Filled.LocalBar else Icons.Filled.NoDrinks,
                                    contentDescription = null
                                )
                            }*/
                        )
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = drink.category)
                            },
                            /*icon = {
                                Icon(
                                    imageVector = Icons.Filled.Category,
                                    contentDescription = null
                                )
                            }*/
                        )
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(text = drink.glass)
                            },
                            /*icon = {
                                Icon(
                                    imageVector = Icons.Filled.WineBar,
                                    contentDescription = null
                                )
                            }*/
                        )
                    }
                }
            }
            Button(
                modifier = Modifier
                    .padding(spacing.spaceSmall)
                    .align(alignment = Alignment.End),
                onClick = onUnfavoriteClicked
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "Unfavorite")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Unfavorite")
                }

            }
        }
    }
}