package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.remote.dto.IngredientDto
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel

fun IngredientDto.toIngredientDomainModel(): IngredientDomainModel {
    return IngredientDomainModel(
        name = this.strIngredient1
    )
}