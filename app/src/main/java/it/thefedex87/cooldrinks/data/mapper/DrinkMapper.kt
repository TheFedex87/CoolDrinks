package it.thefedex87.cooldrinks.data.mapper

import android.annotation.SuppressLint
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import java.time.LocalDate

@SuppressLint("NewApi")
fun DrinkEntity.toDrinkDetailDomainModel(
    availableIngredients: List<IngredientDomainModel>
): DrinkDetailDomainModel {
    val measures = measures.split(",").map { it.trim() }
    val drinkIngredients =
        ingredients.split(",").filter { it.isNotBlank() }.mapIndexed { index, i ->
            DrinkIngredientModel(
                i.trim(),
                measures[index],
                availableIngredients.any {
                    it.name.lowercase() == i.trim().lowercase()
                }
            )
        }

    return DrinkDetailDomainModel(
        idDrink = idDrink,
        isAlcoholic = isAlcoholic,
        category = category,
        name = name,
        drinkThumb = drinkThumb,
        glass = glass,
        ingredients = drinkIngredients,
        instructions = instructions,
        addedDate = LocalDate.of(addedYear, addedMonth, addedDayOfMonth),
        dominantColor = dominantColor,
        isCustomCocktail = isCustomCocktail,
        isFavorite = isFavorite
    )
}

@SuppressLint("NewApi")
fun DrinkDetailDomainModel.toDrinkEntity(isFavorite: Boolean): DrinkEntity {
    var now = LocalDate.now()
    return DrinkEntity(
        idDrink = idDrink,
        isAlcoholic = isAlcoholic,
        category = category,
        name = name,
        drinkThumb = drinkThumb,
        glass = glass,
        ingredients = ingredients.joinToString { it.name ?: "" },
        instructions = instructions,
        measures = ingredients.joinToString { it.measure ?: "" },
        addedDayOfMonth = now.dayOfMonth,
        addedMonth = now.monthValue,
        addedYear = now.year,
        dominantColor = dominantColor ?: 0,
        isCustomCocktail = isCustomCocktail,
        isFavorite = isFavorite
    )
}