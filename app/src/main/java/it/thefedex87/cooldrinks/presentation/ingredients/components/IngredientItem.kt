package it.thefedex87.cooldrinks.presentation.ingredients.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ingredients.model.IngredientUiModel
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IngredientItem(
    ingredient: IngredientUiModel,
    onItemClick: () -> Unit,
    onItemLongClick: () -> Unit,
    onIngredientInfoClick: () -> Unit,
    showSeparator: Boolean = true,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    val addSelectionAnimation = animateFloatAsState(
        targetValue = if (ingredient.isSelected.value) 180f else 0f,
        animationSpec = tween(
            durationMillis = 600
        )
    )

    Box(modifier = modifier
        .fillMaxWidth()
        .combinedClickable(
            onClick = {
                onItemClick()
            },
            onLongClick = {
                onItemLongClick()
            }
        )
        .background(
            if (!ingredient.isSelected.value) {
                MaterialTheme.colorScheme.background
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
        .padding(horizontal = spacing.spaceMedium)
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ingredient.name,
                    style = MaterialTheme.typography.headlineSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                IconButton(onClick = {
                    if (!ingredient.isSelected.value) {
                        onIngredientInfoClick()
                    }
                },
                    modifier = Modifier.graphicsLayer {
                        rotationY = addSelectionAnimation.value
                    }) {
                    Icon(
                        imageVector = if (addSelectionAnimation.value < 90) Icons.Default.Info else Icons.Default.Done,
                        contentDescription = stringResource(id = R.string.ingredient_info),
                        modifier = Modifier
                            .size(32.dp)
                            .scale(
                                if (addSelectionAnimation.value < 90) 1f else (addSelectionAnimation.value - 90) / 90
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            if (showSeparator) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}