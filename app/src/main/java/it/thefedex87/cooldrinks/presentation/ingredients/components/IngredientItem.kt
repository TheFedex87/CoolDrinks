package it.thefedex87.cooldrinks.presentation.ingredients.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun IngredientItem(
    name: String,
    onItemClick: () -> Unit,
    onIngredientInfoClick: () -> Unit,
    showSeparator: Boolean = true,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable {
            onItemClick()
        }
    ) {
        Spacer(modifier = Modifier.height(spacing.spaceMedium))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = {
                onIngredientInfoClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Ingredient info",
                    modifier = Modifier.size(32.dp)
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