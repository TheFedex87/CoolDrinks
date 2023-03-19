package it.thefedex87.cooldrinks.presentation.ingredients.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            .combinedClickable(
                onClick = {
                    onItemClick()
                },
                onLongClick = {
                    onItemLongClick()
                }
            )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.spaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (isSelectionEnabled) Arrangement.Start else Arrangement.Center
                ) {
                    if (isSelectionEnabled) {
                        Checkbox(
                            checked = ingredient.isSelected.value,
                            onCheckedChange = onCheckedChanged
                        )
                    }
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
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
                Box(modifier = Modifier.fillMaxWidth()) {
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
                }
            }
        }
    }
}