package it.thefedex87.cooldrinks.presentation.bar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SegmentedButton(
    options: List<String>,
    selectedOption: Int = 0,
    onOptionClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        options.forEachIndexed { index, option ->
            OutlinedButton(
                onClick = {
                    onOptionClicked(index)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedOption == index) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                ),
                shape = when (index) {
                    0 -> {
                        RoundedCornerShape(
                            topStartPercent = 50,
                            bottomStartPercent = 50,
                            topEndPercent = 0,
                            bottomEndPercent = 0
                        )
                    }
                    options.lastIndex -> {
                        RoundedCornerShape(
                            topEndPercent = 50,
                            bottomEndPercent = 50,
                            topStartPercent = 0,
                            bottomStartPercent = 0
                        )
                    }
                    else -> {
                        RoundedCornerShape(
                            topEndPercent = 0,
                            bottomEndPercent = 0,
                            topStartPercent = 0,
                            bottomStartPercent = 0
                        )
                    }
                }
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedOption == index) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Selected option: $option",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = option,
                        color = if (selectedOption == index) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}