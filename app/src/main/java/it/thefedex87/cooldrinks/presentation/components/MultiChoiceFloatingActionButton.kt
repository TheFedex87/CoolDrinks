package it.thefedex87.cooldrinks.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.R

@Composable
fun MultiChoiceActionButton(
    icon: ImageVector = Icons.Default.Add,
    state: MultiFabState = MultiFabState.COLLAPSED,
    onClick: (MultiFabState) -> Unit,
    miniFabs: List<MiniFabSpec>,
    isExtendedFab: Boolean = false,
    extendedLabel: String? = null,
    prevExtendedLabel: String? = null,
) {
    val rotation by animateFloatAsState(targetValue = if (state == MultiFabState.COLLAPSED) 0f else 45f)

    Column(horizontalAlignment = Alignment.End) {
        miniFabs.forEachIndexed { index, miniFabSpec ->
            MiniFloatingActionButton(
                icon = miniFabSpec.icon,
                onClick = { miniFabSpec.onClick() },
                animationDelay = 100 * index,
                state = state
            )
        }


        if(!isExtendedFab) {
            FloatingActionButton(onClick = {
                if (state == MultiFabState.COLLAPSED) onClick(MultiFabState.EXPANDED) else onClick(
                    MultiFabState.COLLAPSED
                )
            }) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        } else {
            ExtendedFloatingActionButton(onClick = {
                if (state == MultiFabState.COLLAPSED) onClick(MultiFabState.EXPANDED) else onClick(
                    MultiFabState.COLLAPSED
                )
            }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = extendedLabel ?: prevExtendedLabel ?: "")
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MiniFloatingActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    animationDelay: Int,
    state: MultiFabState
) {
    AnimatedVisibility(
        visible = state == MultiFabState.EXPANDED,
        enter = slideInHorizontally(
            initialOffsetX = { it * 3 },
            animationSpec = tween(
                250 + animationDelay
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { it * 3 },
            animationSpec = tween(
                250 + animationDelay
            )
        ),
    ) {
        Column {
            ElevatedCard(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        onClick()
                    },
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null)
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

data class MiniFabSpec(
    val icon: ImageVector,
    val onClick: () -> Unit
)

enum class MultiFabState {
    COLLAPSED,
    EXPANDED
}