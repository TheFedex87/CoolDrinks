package it.thefedex87.cooldrinks.presentation.util

sealed class UiEvent {
    data class ShowSnackBar(val message: UiText) : UiEvent()
    object PopBackStack : UiEvent()
    data class SaveBitmapLocal(val path: String) : UiEvent()
}
