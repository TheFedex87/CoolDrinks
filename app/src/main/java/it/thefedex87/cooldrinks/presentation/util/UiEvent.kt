package it.thefedex87.cooldrinks.presentation.util

sealed class UiEvent {
    data class ShowSnackBar(val message: UiText) : UiEvent()
}
