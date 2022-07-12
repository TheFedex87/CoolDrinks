package it.thefedex87.cooldrinks.presentation.ingredients.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
                    .padding(spacing.spaceSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ingredient,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoadingIngredientInfo) {
                    CircularProgressIndicator()
                } else {
                    if (getIngredientInfoError != null) {
                        Text(text = getIngredientInfoError!!)
                    } else {
                        ingredientInfo?.let { ii ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "#${ii.id}")
                                Text(
                                    text = if (ii.alcoholic) {
                                        stringResource(id = R.string.alcoholic)
                                    } else {
                                        stringResource(id = R.string.non_alcoholic)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = ii.description ?: stringResource(id = R.string.no_info),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}