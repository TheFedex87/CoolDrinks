package it.thefedex87.cooldrinks.presentation.add_ingredient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.add_my_drink.AddMyDrinkEvent
import it.thefedex87.cooldrinks.presentation.bar.components.SegmentedButton
import it.thefedex87.cooldrinks.presentation.components.GalleryPictureSelector
import it.thefedex87.cooldrinks.presentation.components.OutlinedTextFieldWithErrorMessage
import it.thefedex87.cooldrinks.presentation.components.saveToLocalStorage
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    onComposed: (BottomNavigationScreenState) -> Unit,
    onNavigateBack: () -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    viewModel: AddIngredientViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val title = stringResource(id = R.string.add_new_ingredient)
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
                        viewModel.onEvent(AddIngredientEvent.OnSaveClicked)
                    },
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                topBarVisible = true,
                topBarTitle = title,
                topBarActions ={
                    IconButton(onClick = {
                        viewModel.onEvent(AddIngredientEvent.OnSaveClicked)
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

        viewModel.uiEvent.onEach { event ->
            when (event) {
                is UiEvent.PopBackStack -> {
                    onNavigateBack()
                }
                is UiEvent.SaveBitmapLocal -> {
                    context.filesDir.path
                    viewModel.onEvent(AddIngredientEvent.PictureSaveResult(
                        viewModel.state.selectedPicture!!.saveToLocalStorage(
                            context,
                            "${event.path}.jpg"
                        ),
                        pathCallback = {
                            "${context.filesDir.path}/${event.path}.jpg"
                        }
                    ))
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
                viewModel.onEvent(AddIngredientEvent.OnPictureSelected(it))
            },
            viewModel.state.selectedPicture,
            isCircular = true,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        OutlinedTextFieldWithErrorMessage(
            value = viewModel.state.ingredientName,
            onValueChanged = {
                viewModel.onEvent(AddIngredientEvent.OnIngredientNameChanged(it))
            },
            errorMessage = viewModel.state.ingredientNameError,
            label = stringResource(id = R.string.name),
            imeAction = ImeAction.Next
        )
        OutlinedTextFieldWithErrorMessage(
            value = viewModel.state.ingredientDescription,
            onValueChanged = {
                viewModel.onEvent(AddIngredientEvent.OnIngredientDescriptionChanged(it))
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
                viewModel.onEvent(AddIngredientEvent.OnIngredientAlcoholicChanged(it == 0))
            },
            selectedOption = if (viewModel.state.ingredientIsAlcoholic) 0 else 1,
            textStyle = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(spacing.spaceSmall))
        SegmentedButton(
            options = listOf(
                stringResource(id = R.string.available),
                stringResource(id = R.string.not_available),
            ),
            onOptionClicked = {
                viewModel.onEvent(AddIngredientEvent.OnIngredientAvailableChanged(it == 0))
            },
            selectedOption = if (viewModel.state.ingredientAvailable) 0 else 1,
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}