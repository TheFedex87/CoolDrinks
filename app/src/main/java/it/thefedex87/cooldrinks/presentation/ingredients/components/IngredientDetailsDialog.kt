package it.thefedex87.cooldrinks.presentation.ingredients.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel
import it.thefedex87.cooldrinks.presentation.components.IngredientDetails
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.util.Consts.TAG

@Composable
fun IngredientDetailsDialog(
    ingredient: String,
    isLoadingIngredientInfo: Boolean,
    getIngredientInfoError: String?,
    onDismiss: () -> Unit,
    ingredientInfo: IngredientDetailsDomainModel?
) {
    val spacing = LocalSpacing.current
    val sizes = LocalConfiguration.current
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizes.screenHeightDp.dp * 70 / 100)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(12.dp))
                .padding(spacing.spaceMedium)
        ) {
            IngredientDetails(
                ingredient = ingredient,
                isLoadingIngredientInfo = isLoadingIngredientInfo,
                getIngredientInfoError = getIngredientInfoError,
                ingredientInfo = ingredientInfo,
                showDescription = true,
                modifier = Modifier.padding(spacing.spaceSmall)
            )
        }
    }
}