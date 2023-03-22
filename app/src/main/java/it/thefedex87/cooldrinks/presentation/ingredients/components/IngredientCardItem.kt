package it.thefedex87.cooldrinks.presentation.ingredients.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ingredients.model.IngredientUiModel
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IngredientCardItem(
    ingredient: IngredientUiModel,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit,
    onIngredientInfoClick: () -> Unit,
    onCheckedChanged: (Boolean) -> Unit,
    isSelectionEnabled: Boolean,
    showSeparator: Boolean = true,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.spaceExtraSmall)
    ) {
        val scale = animateFloatAsState(
            targetValue = if (ingredient.isSelected.value) 1.02f else 1f,
            animationSpec = tween(durationMillis = 400)
        )

        /*val cardElevation = animateDpAsState(
            targetValue = if (ingredient.isSelected.value) 6.dp else 0.dp,
            animationSpec = tween(durationMillis = 200)
        )*/
        Card(
            modifier = modifier
                .fillMaxWidth()
                .scale(scale = scale.value)
                .combinedClickable(
                    onClick = {
                        onItemClick()
                    },
                    onLongClick = {
                        onItemLongClick()
                    }
                ),/*.shadow(elevation = cardElevation.value, shape = RoundedCornerShape(12.dp))*/
            /*elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 56.dp
            )*/
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.spaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ingredient.thumbnail)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.height(150.dp)
                )
                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = {
                            onIngredientInfoClick()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.info),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    if (isSelectionEnabled) {
                        Checkbox(
                            checked = ingredient.isSelected.value,
                            onCheckedChange = onCheckedChanged
                        )
                    }
                }
            }
        }
    }
}