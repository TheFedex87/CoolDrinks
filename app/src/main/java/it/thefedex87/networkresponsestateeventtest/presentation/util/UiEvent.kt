package it.thefedex87.networkresponsestateeventtest.presentation.util

sealed class UiEvent {
    data class ShowSnackBar(val message: UiText) : UiEvent()

}
