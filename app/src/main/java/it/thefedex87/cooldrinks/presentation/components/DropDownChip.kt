package it.thefedex87.cooldrinks.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownChip(
    isChipSelected: Boolean,
    onChipClick: () -> Unit,
    label: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    selectedIcon: (@Composable () -> Unit)? = null,
    isMenuExpanded: Boolean,
    onDismissRequest: () -> Unit,
    dropDownItems: List<DropDownItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        FilterChip(
            selected = isChipSelected,
            onClick = onChipClick,
            label = {
                Text(text = label)
            },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            selectedIcon = selectedIcon,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = onDismissRequest
        ) {
            dropDownItems.map {
                DropdownMenuItem(
                    text = {
                        Text(text = it.label)
                    },
                    onClick = it.onItemClick,
                    leadingIcon = it.icon
                )
            }
        }
    }
}

data class DropDownItem(
    val label: String,
    val onItemClick: () -> Unit,
    val icon: @Composable (() -> Unit)? = null,
)