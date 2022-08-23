package it.thefedex87.cooldrinks.data.mapper

import android.annotation.SuppressLint
import it.thefedex87.cooldrinks.data.local.entity.FavoriteDrinkEntity
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import java.time.LocalDate

@SuppressLint("NewApi")
fun FavoriteDrinkEntity.toDrinkDetailDomainModel(availableIngredients: List<IngredientDomainModel>): DrinkDetailDomainModel {
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
        dominantColor = dominantColor
    )
}

@SuppressLint("NewApi")
fun DrinkDetailDomainModel.toFavoriteDrinkEntity(): FavoriteDrinkEntity {
    var now = LocalDate.now()
    return FavoriteDrinkEntity(
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
        dominantColor = dominantColor ?: 0
    )
}