package it.thefedex87.cooldrinks.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel

@Composable
fun IngredientDetails(
    ingredient: String,
    isLoadingIngredientInfo: Boolean,
    getIngredientInfoError: String?,
    ingredientInfo: IngredientDetailsDomainModel?,
    showSearchIcon: Boolean = false,
    showEditIcon: Boolean = false,
    showDeleteIcon: Boolean = false,
    onSearchIconClicked: (String) -> Unit = {},
    onEditIconClicked: () -> Unit = {},
    onDeleteIconClicked: () -> Unit = {},
    showDescription: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ingredient,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            if (showSearchIcon) {
                IconButton(onClick = {
                    onSearchIconClicked(ingredient)
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search_drink)
                    )
                }
            }
            if (showEditIcon) {
                IconButton(onClick = onEditIconClicked) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(id = R.string.search_drink)
                    )
                }
            }
            if (showDeleteIcon) {
                IconButton(onClick = onDeleteIconClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.search_drink)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (isLoadingIngredientInfo) {
            CircularProgressIndicator()
        } else {
            if (getIngredientInfoError != null) {
                Text(text = getIngredientInfoError!!)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    ingredientInfo?.let { ii ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "#${if (ii.id!!.length < 5) ii.id else "N/A"}")
                            Text(
                                text = if (ii.alcoholic) {
                                    stringResource(id = R.string.alcoholic)
                                } else {
                                    stringResource(id = R.string.non_alcoholic)
                                }
                            )
                        }
                        if (showDescription) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (ii.description.isNullOrBlank()) stringResource(id = R.string.no_info) else ii.description,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}