package it.thefedex87.cooldrinks.presentation.util

sealed interface UiEvent {
    data class ShowSnackBar(val message: UiText) : UiEvent
    data class PopBackStack(val bundle: Map<String, String>? = null) : UiEvent
    //data class SaveBitmapLocal(val path: String) : UiEvent
    data class ScrollPagerToPage(val page: Int) : UiEvent
}
