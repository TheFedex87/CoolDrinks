package it.thefedex87.cooldrinks.presentation.add_ingredient

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.bar.components.SegmentedButton
import it.thefedex87.cooldrinks.presentation.components.GalleryPictureSelector
import it.thefedex87.cooldrinks.presentation.components.OutlinedTextFieldWithErrorMessage
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddIngredientScreen(
    state: AddIngredientState,
    onEvent: (AddIngredientEvent) -> Unit,
    uiEvent: Flow<UiEvent>,
    onComposed: (BottomNavigationScreenState) -> Unit,
    onNavigateBack: (String?) -> Unit,
    snackbarHostState: SnackbarHostState,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val title = state.title

    val save = stringResource(id = R.string.save)
    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = false,
                    floatingActionButtonIcon = Icons.Default.Save,
                    floatingActionButtonMultiChoice = null,
                    floatingActionButtonLabel = save,
                    floatingActionButtonClicked = {
                        onEvent(AddIngredientEvent.OnSaveClicked)
                    },
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                topBarVisible = true,
                topBarTitle = title.asString(context),
                topBarActions ={
                    IconButton(onClick = {
                        onEvent(AddIngredientEvent.OnSaveClicked)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = stringResource(id = R.string.save),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } ,
                bottomBarVisible = false
            )
        )

        uiEvent.onEach { event ->
            when (event) {
                is UiEvent.PopBackStack -> {
                    onNavigateBack(event.bundle?.get("storedIngredient"))
                }
                is UiEvent.ShowSnackBar -> {
                    keyboardController?.hide()
                    snackbarHostState.showSnackbar(
                        message = event.message.asString(context),
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }.launchIn(this)
    }

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.spaceMedium)
    ) {

        GalleryPictureSelector(
            onPicturePicked = {
                onEvent(AddIngredientEvent.OnPictureSelected(it))
            },
            state.selectedPicture,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        OutlinedTextFieldWithErrorMessage(
            value = state.ingredientName,
            onValueChanged = {
                onEvent(AddIngredientEvent.OnIngredientNameChanged(it))
            },
            errorMessage = state.ingredientNameError,
            label = stringResource(id = R.string.name),
            imeAction = ImeAction.Next
        )
        OutlinedTextFieldWithErrorMessage(
            value = state.ingredientDescription,
            onValueChanged = {
                onEvent(AddIngredientEvent.OnIngredientDescriptionChanged(it))
            },
            modifier = Modifier.height(300.dp),
            label = stringResource(id = R.string.description),
            singleLine = false,
            imeAction = ImeAction.Next
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        SegmentedButton(
            options = listOf(
                stringResource(id = R.string.alcoholic),
                stringResource(id = R.string.non_alcoholic),
            ),
            onOptionClicked = {
                onEvent(AddIngredientEvent.OnIngredientAlcoholicChanged(it == 0))
            },
            selectedOption = if (state.ingredientIsAlcoholic) 0 else 1,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        SegmentedButton(
            options = listOf(
                stringResource(id = R.string.available),
                stringResource(id = R.string.not_available),
            ),
            onOptionClicked = {
                onEvent(AddIngredientEvent.OnIngredientAvailableChanged(it == 0))
            },
            selectedOption = if (state.ingredientAvailable) 0 else 1,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}