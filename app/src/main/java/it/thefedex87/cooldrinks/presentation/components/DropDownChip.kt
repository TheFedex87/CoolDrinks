package it.thefedex87.cooldrinks.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import it.thefedex87.cooldrinks.util.Consts.TAG

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
    dropDownItems: List<DropDownItem>
) {
    Column {
        FilterChip(
            selected = isChipSelected,
            onClick = {
                onChipClick()
            },
            label = {
                Text(text = label)
            },
            leadingIcon = {
                leadingIcon?.invoke()
            },
            trailingIcon = {
                trailingIcon?.invoke()
            },
            selectedIcon = {
                selectedIcon?.invoke()
            }
        )
        DropdownMenu(
            expanded = isMenuExpanded,
            onDismissRequest = {
                onDismissRequest()
            }
        ) {
            dropDownItems.map {
                DropdownMenuItem(
                    text = {
                        Text(text = it.label)
                    },
                    onClick = {
                        it.onItemClick()
                    },
                    leadingIcon = {
                        it.icon?.invoke()
                    }
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