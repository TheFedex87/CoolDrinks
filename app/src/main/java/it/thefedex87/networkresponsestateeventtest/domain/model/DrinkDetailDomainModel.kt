package it.thefedex87.networkresponsestateeventtest.domain.model

data class DrinkDetailDomainModel(
    val idDrink: Int,
    val isAlcoholic: Boolean,
    val category: String,
    val name: String,
    val drinkThumb: String,
    val glass: String,
    val ingredients: List<String?>,
    val instructions: String,
    val measures: List<String?>
)