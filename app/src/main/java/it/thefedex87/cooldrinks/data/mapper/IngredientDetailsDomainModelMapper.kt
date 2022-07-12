package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.remote.dto.IngredientDetailsDto
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel

fun IngredientDetailsDto.toIngredientDetailsDomainModel(): IngredientDetailsDomainModel {
    return IngredientDetailsDomainModel(
        id = idIngredient.toInt(),
        name = strIngredient,
        description = strDescription,
        type = strType,
        alcoholic = strAlcohol.lowercase() == "yes"
    )
}