package it.thefedex87.cooldrinks.presentation.add_my_drink.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> Material3Spinner(
    isExpanded: Boolean,
    expandRequested: () -> Unit,
    dismissRequested: () -> Unit,
    values: List<T>,
    valueMapper: (T) -> String,
    value: String,
    onSelectionIndexChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = modifier
                    .clickable {
                        expandRequested()
                    }
                    .weight(1f),
                trailingIcon = {
                    Icon(
                        imageVector = if (isExpanded)
                            Icons.Default.ArrowDropUp
                        else
                            Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier.padding(spacing.spaceSmall)
                    )
                }
            )
        }
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable {
                    expandRequested()
                }
        )
    }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = {
            dismissRequested()
        }
    ) {
        values.forEachIndexed { index, v ->
            DropdownMenuItem(
                text = {
                    Text(text = valueMapper(v))
                },
                onClick = {
                    onSelectionIndexChanged(index)
                }
            )
        }
    }
}