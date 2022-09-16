package it.thefedex87.cooldrinks.presentation.add_my_drink.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun AddDrinkIngredientDialog(
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val sizes = LocalConfiguration.current
    Dialog(onDismissRequest = onDismiss) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(sizes.screenHeightDp.dp * 70 / 100)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RoundedCornerShape(12.dp))
            .padding(spacing.spaceMedium)) {

        }
    }
}