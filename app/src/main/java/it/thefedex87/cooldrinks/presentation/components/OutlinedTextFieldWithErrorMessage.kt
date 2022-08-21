package it.thefedex87.cooldrinks.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiText

@Composable
fun OutlinedTextFieldWithErrorMessage(
    value: String,
    onValueChanged: (String) -> Unit,
    imeAction: ImeAction,
    label: String,
    singleLine: Boolean = true,
    errorMessage: UiText? = null,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChanged(it)
            },
            singleLine = singleLine,
            keyboardActions = KeyboardActions(
                onNext = {
                    defaultKeyboardAction(imeAction)
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = imeAction
            ),
            label = {
                Text(text = label)
            },
            modifier = modifier
                .fillMaxWidth(),
            isError = errorMessage != null,
        )
        if(errorMessage != null) {
            Text(
                text = errorMessage.asString(LocalContext.current),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = spacing.spaceMedium)
            )
        }
    }
}