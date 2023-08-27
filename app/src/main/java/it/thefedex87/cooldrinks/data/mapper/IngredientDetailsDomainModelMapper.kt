package it.thefedex87.cooldrinks.data.mapper

import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import it.thefedex87.cooldrinks.data.remote.dto.IngredientDetailsDto
import it.thefedex87.cooldrinks.domain.model.IngredientDetailsDomainModel

fun IngredientDetailsDto.toIngredientDetailsDomainModel(): IngredientDetailsDomainModel {
    return IngredientDetailsDomainModel(
        id = idIngredient,
        name = strIngredient,
        description = strDescription,
        type = strType,
        alcoholic = strAlcohol.lowercase() == "yes",
        imagePath = "https://www.thecocktaildb.com/images/ingredients/$strIngredient.png",
        availableLocal = false,
        isPersonalIngredient = false
    )
}

fun IngredientEntity.toIngredientDetailsDomainModel(): IngredientDetailsDomainModel {
    return IngredientDetailsDomainModel(
        id = id,
        name = name,
        description = description,
        type = type,
        alcoholic = alcoholic,
        imagePath = imagePath,
        availableLocal = availableLocal,
        isPersonalIngredient = isPersonalIngredient
    )
}

fun IngredientDetailsDomainModel.toIngredientEntity(): IngredientEntity {
    return IngredientEntity(
        id = id!!,
        name = name,
        description = description,
        type = type ?: "",
        alcoholic = alcoholic,
        imagePath = imagePath,
        availableLocal = availableLocal,
        isPersonalIngredient = isPersonalIngredient
    )
}