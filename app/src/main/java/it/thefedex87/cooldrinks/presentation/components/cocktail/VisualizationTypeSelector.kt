package it.thefedex87.cooldrinks.presentation.search_drink.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.domain.model.VisualizationType

@Composable
fun VisualizationTypeSelector(
    selectedVisualizationType: VisualizationType,
    onClick: (type: VisualizationType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                onClick(VisualizationType.Card)
            }
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.ViewCarousel,
                contentDescription = "View carousel",
                tint = if (selectedVisualizationType == VisualizationType.Card) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = {
                onClick(VisualizationType.List)
            }
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Default.List,
                contentDescription = "View list",
                tint = if (selectedVisualizationType == VisualizationType.List) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground
            )
        }
    }
}