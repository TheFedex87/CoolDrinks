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
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel

@Composable
fun IngredientItem(
    ingredient: DrinkIngredientModel,
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
            text = ingredient.name!!,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
            color = if(ingredient.isAvailable == true) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colorScheme.onSurfaceVariant))
        Text(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            text = ingredient.measure ?: "",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Light),
            color = if(ingredient.isAvailable == true) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
        )
    }
}