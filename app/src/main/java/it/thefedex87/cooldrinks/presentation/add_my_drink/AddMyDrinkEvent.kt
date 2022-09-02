package it.thefedex87.cooldrinks.presentation.add_my_drink

sealed class AddMyDrinkEvent {
    data class OnMyDrinkNameChanged(val name: String) : AddMyDrinkEvent()
    data class OnMyDrinkInstructionsChanged(val instructions: String) : AddMyDrinkEvent()

    object OnSaveClicked : AddMyDrinkEvent()
}
