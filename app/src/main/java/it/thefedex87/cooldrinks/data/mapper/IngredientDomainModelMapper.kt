package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import it.thefedex87.cooldrinks.data.remote.dto.IngredientDto
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel

fun IngredientDto.toIngredientDomainModel(): IngredientDomainModel {
    return IngredientDomainModel(
        name = this.strIngredient1,
        thumbnail = "https://www.thecocktaildb.com/images/ingredients/$strIngredient1-Medium.png",
    )
}

fun IngredientEntity.toIngredientDomainModel(): IngredientDomainModel {
    return IngredientDomainModel(
        name = name,
        thumbnail = imagePath?.replace(".png", "-Medium.png") ?: ""
    )
}