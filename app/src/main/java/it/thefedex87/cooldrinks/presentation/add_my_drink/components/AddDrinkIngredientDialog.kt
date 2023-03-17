package it.thefedex87.cooldrinks.presentation.add_my_drink.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.PopupProperties
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import it.thefedex87.cooldrinks.presentation.add_my_drink.AddMyDrinkEvent
import it.thefedex87.cooldrinks.presentation.add_my_drink.AddMyDrinkViewModel
import it.thefedex87.cooldrinks.presentation.components.DropDownItem
import it.thefedex87.cooldrinks.presentation.components.OutlinedTextFieldWithErrorMessage
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun AddDrinkIngredientDialog(
    addingIngredientName: String,
    addingIngredientMeasure: String,
    addingIngredientIsDecoration: Boolean,
    addingIngredientIsAvailable: Boolean,
    onIngredientNameChanged: (String) -> Unit,
    onIngredientMeasureChanged: (String) -> Unit,
    onIsDecorationChanged: (Boolean) -> Unit,
    onIsAvailableChanged: (Boolean) -> Unit,
    onSaveClicked: () -> Unit,
    onDismiss: () -> Unit,
    filteredLocalIngredients: List<DrinkIngredientModel> = listOf(),
    onIngredientClicked: (DrinkIngredientModel) -> Unit = {}
) {
    val spacing = LocalSpacing.current
    val sizes = LocalConfiguration.current

    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(sizes.screenHeightDp.dp * 70 / 100)
                .background(MaterialTheme.colorScheme.surface)
                .clip(RoundedCornerShape(12.dp))
                .padding(spacing.spaceMedium)
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextFieldWithErrorMessage(
                            value = addingIngredientName,
                            onValueChanged = {
                                onIngredientNameChanged(it)
                            },
                            //errorMessage = state.cocktailNameError,
                            label = stringResource(id = R.string.name),
                            imeAction = ImeAction.Next
                        )
                        DropdownMenu(
                            modifier = Modifier.defaultMinSize(200.dp),
                            expanded = filteredLocalIngredients.isNotEmpty(),
                            properties = PopupProperties(focusable = false),
                            onDismissRequest = { }) {
                            filteredLocalIngredients.forEach { ingredient ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = ingredient.name!!)
                                    },
                                    onClick = { onIngredientClicked(ingredient) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    OutlinedTextFieldWithErrorMessage(
                        value = addingIngredientMeasure,
                        onValueChanged = {
                            onIngredientMeasureChanged(it)
                        },
                        //errorMessage = state.cocktailNameError,
                        label = stringResource(id = R.string.measure),
                        imeAction = ImeAction.Next
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = addingIngredientIsDecoration,
                            onCheckedChange = {
                                onIsDecorationChanged(it)
                            }
                        )
                        Text(text = stringResource(id = R.string.is_decoration))
                    }
                    Spacer(modifier = Modifier.height(spacing.spaceSmall))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = addingIngredientIsAvailable,
                            onCheckedChange = {
                                onIsAvailableChanged(it)
                            }
                        )
                        Text(text = stringResource(id = R.string.is_available))
                    }
                }
                Spacer(modifier = Modifier.height(spacing.spaceSmall))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onSaveClicked()
                        },
                        enabled = addingIngredientName.isNotBlank() &&
                                (addingIngredientMeasure.isNotBlank() || addingIngredientIsDecoration)
                    ) {
                        Text(text = stringResource(id = R.string.add))
                    }
                    Spacer(modifier = Modifier.width(spacing.spaceSmall))
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(text = stringResource(id = R.string.cancel))
                    }
                }
            }
        }
    }
}