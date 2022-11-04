package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel

fun IngredientEntity.toDrinkIngredientDomainModel(): DrinkIngredientModel {
    return DrinkIngredientModel(
        name = this.name,
        measure = null,
        isAvailable = this.availableLocal
    )
}