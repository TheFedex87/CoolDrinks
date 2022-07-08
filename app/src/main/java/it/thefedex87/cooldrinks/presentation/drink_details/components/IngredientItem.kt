package it.thefedex87.cooldrinks.presentation.drink_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun IngredientItem(
    ingredientName: String,
    ingredientMeasure: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(2f),
            text = ingredientName,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
        )
        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.onSurfaceVariant))
        Text(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            text = ingredientMeasure,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light)
        )
    }
}