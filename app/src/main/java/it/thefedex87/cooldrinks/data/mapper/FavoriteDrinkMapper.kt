package it.thefedex87.cooldrinks.data.mapper

import android.annotation.SuppressLint
import it.thefedex87.cooldrinks.data.local.entity.FavoriteDrinkEntity
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import java.time.LocalDate

@SuppressLint("NewApi")
fun FavoriteDrinkEntity.toDrinkDetailDomainModel(): DrinkDetailDomainModel {
    return DrinkDetailDomainModel(
        idDrink = idDrink,
        isAlcoholic = isAlcoholic,
        category = category,
        name = name,
        drinkThumb = drinkThumb,
        glass = glass,
        ingredients = ingredients.split(",").map { it.trim() },
        instructions = instructions,
        measures = measures.split(",").map { it.trim() },
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
        ingredients = ingredients.filterNotNull().joinToString { it },
        instructions = instructions,
        measures = measures.joinToString { it ?: "" },
        addedDayOfMonth = now.dayOfMonth,
        addedMonth = now.monthValue,
        addedYear = now.year,
        dominantColor = dominantColor ?: 0
    )
}