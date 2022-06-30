package it.thefedex87.cooldrinks.presentation.drink_details.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.LocalBar
import androidx.compose.material.icons.outlined.WineBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CharacteristicSection(
    drinkGlass: String?,
    drinkCategory: String?,
    drinkAlcoholic: String?,
    separatorColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CharacteristicDescription(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.WineBar,
                value = drinkGlass ?: ""
            )
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .width(2.dp)
                    .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                    .background(separatorColor)
            )
            CharacteristicDescription(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Category,
                value = drinkCategory ?: ""
            )
        }
        Box(
            modifier = Modifier
                .height(2.dp)
                .fillMaxWidth()
                .clip(CircleShape)
                .background(separatorColor)
        )
        CharacteristicDescription(
            icon = Icons.Outlined.LocalBar,
            value = drinkAlcoholic ?: ""
        )
    }
}