package it.thefedex87.networkresponsestateeventtest.presentation.search_drink

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.networkresponsestateeventtest.R
import it.thefedex87.networkresponsestateeventtest.domain.repository.CocktailRepository
import it.thefedex87.networkresponsestateeventtest.presentation.util.UiEvent
import it.thefedex87.networkresponsestateeventtest.presentation.util.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDrinkViewModel @Inject constructor(
    val drinkRepository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(SearchDrinkState())
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun onEvent(event: SearchDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is SearchDrinkEvent.OnDrinkClick -> {

                }
                is SearchDrinkEvent.OnSearchClick -> {
                    state = state.copy(isLoading = true, foundDrinks = emptyList())
                    drinkRepository
                        .searchCocktails(state.searchQuery)
                        .onSuccess {
                            state = state.copy(
                                isLoading = false,
                                searchQuery = "",
                                showSearchHint = true,
                                foundDrinks = it,
                                showNoDrinkFound = it.isEmpty()
                            )
                        }
                        .onFailure {
                            state = state.copy(
                                isLoading = false,
                                searchQuery = "",
                                showSearchHint = true,
                                showNoDrinkFound = false
                            )
                            _uiEvent.send(UiEvent.ShowSnackBar(UiText.StringResource(R.string.search_drink_error)))
                        }
                }
                is SearchDrinkEvent.OnSearchQueryChange -> {
                    state = state.copy(
                        searchQuery = event.query,
                        showSearchHint = false
                    )
                }
                is SearchDrinkEvent.OnSearchFocusChange -> {
                    state =
                        state.copy(showSearchHint = !event.isFocused && state.searchQuery.isEmpty())
                }
            }
        }
    }

    fun calcDominantColor(
        drawable: Drawable,
        onFinish: (Color) -> Unit
    ) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bmp).generate() { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}