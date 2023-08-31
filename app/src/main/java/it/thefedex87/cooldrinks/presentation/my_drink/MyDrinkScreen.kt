package it.thefedex87.cooldrinks.presentation.my_drink

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.DetailedDrinkItem
import it.thefedex87.cooldrinks.presentation.components.EmptyList
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import kotlinx.coroutines.flow.Flow

@Composable
fun MyDrinkScreen(
    state: MyDrinkState,
    onEvent: (MyDrinkEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    snackbarHostState: SnackbarHostState,
    onEditDrinkClicked: (Int) -> Unit,
    onDrinkClicked: (Int, Int, String) -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackBar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }

                else -> Unit
            }
        }
    }

    if (state.showConfirmRemoveDrinkDialog) {
        AlertDialog(
            onDismissRequest = {
                onEvent(MyDrinkEvent.OnRemoveDrinkCanceled)
            },
            confirmButton = {
                TextButton(onClick = {
                    onEvent(MyDrinkEvent.OnRemoveDrinkConfirmed(state.drinkToRemove!!))
                }) {
                    Text(text = stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onEvent(MyDrinkEvent.OnRemoveDrinkCanceled)
                }) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = {
                Text(text = stringResource(id = R.string.confirm_remove_title))
            },
            text = {
                Text(text = stringResource(id = R.string.confirm_remove_custom_drink_body))
            }
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else if (state.drinks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            EmptyList(
                icon = Icons.Default.LocalDrink,
                text = stringResource(id = R.string.add_your_cocktail),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = spacing.spaceMedium,
                        end = spacing.spaceMedium,
                        start = spacing.spaceMedium,
                        bottom = spacing.spaceExtraLarge
                    ),
            ) {
                items(state.drinks) { drink ->
                    DetailedDrinkItem(
                        drink = drink,
                        drinkId = drink.idDrink,
                        onDrinkClicked = onDrinkClicked,
                        onFavoriteChangeClicked = {
                            onEvent(MyDrinkEvent.OnChangeFavoriteStateClicked(it))
                        },
                        onRemoveClicked = {
                            onEvent(MyDrinkEvent.OnRemoveDrinkClicked(it))
                        },
                        onEditDrinkClicked = {
                            onEditDrinkClicked(it)
                        },
                        modifier = Modifier.padding(
                            vertical = spacing.spaceSmall
                        ),
                    )
                }
            }
        }
    }
}