package it.thefedex87.cooldrinks.domain.repository

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import kotlinx.coroutines.flow.Flow

interface CocktailRepository {
    suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>>
    suspend fun getDrinkDetails(id: Int) : Result<List<DrinkDetailDomainModel>>

    val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
    suspend fun getFavoriteDrink(id: Int) : DrinkDetailDomainModel

    suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel)
    suspend fun removeFromFavorite(drinkId: Int)
}