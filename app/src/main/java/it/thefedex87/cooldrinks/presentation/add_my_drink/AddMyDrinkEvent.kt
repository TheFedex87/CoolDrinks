package it.thefedex87.cooldrinks.presentation.add_my_drink

sealed class AddMyDrinkEvent {
    data class OnMyDrinkNameChanged(val name: String) : AddMyDrinkEvent()
    data class OnMyDrinkInstructionsChanged(val instructions: String) : AddMyDrinkEvent()
    data class OnMyDrinkGlassChanged(val glass: String) : AddMyDrinkEvent()
    data class OnMyDrinkCategoryChanged(val category: String) : AddMyDrinkEvent()
    data class OnMyDrinkIsAlcoholicChanged(val isAlcoholic: Boolean) : AddMyDrinkEvent()

    object AddDrinkIngredientRequested : AddMyDrinkEvent()

    object OnSaveClicked : AddMyDrinkEvent()
}
