package it.thefedex87.cooldrinks.domain.repository

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.domain.model.IngredientDomainModel
import kotlinx.coroutines.flow.Flow

interface CocktailRepository {
    suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>>
    suspend fun getDrinkDetails(id: Int) : Result<List<DrinkDetailDomainModel>>
    suspend fun getIngredients(): Result<List<IngredientDomainModel>>

    val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
    suspend fun getFavoriteDrink(id: Int) : Flow<DrinkDetailDomainModel?>

    suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel): Long
    suspend fun removeFromFavorite(drinkId: Int)
}