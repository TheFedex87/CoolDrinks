package it.thefedex87.cooldrinks.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

@Composable
fun SearchTextField(
    text: String,
    showHint: Boolean,
    hint: String = stringResource(id = R.string.search_hint),
    onSearch: () -> Unit,
    onValueChanged: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Box(modifier = modifier) {
        TextField(
            value = text,
            onValueChange = {
                onValueChanged(it)
            },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch()
                    defaultKeyboardAction(ImeAction.Search)
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            placeholder = {
                Text(text = hint)
            },
            modifier = modifier
                .fillMaxWidth(),
            trailingIcon = {
                IconButton(
                    onClick = onSearch,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(id = R.string.search)
                    )
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.background,
                focusedTrailingIconColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}