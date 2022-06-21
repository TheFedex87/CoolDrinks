package it.thefedex87.cooldrinks.data.repository

import it.thefedex87.cooldrinks.data.local.DrinkDao
import it.thefedex87.cooldrinks.data.mapper.toDrinkDetailDomainModel
import it.thefedex87.cooldrinks.data.mapper.toDrinkDomainModel
import it.thefedex87.cooldrinks.data.mapper.toFavoriteDrinkEntity
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import java.io.EOFException

class CocktailRepositoryImpl constructor(
    val cocktailDbApi: TheCocktailDbApi,
    val drinkDao: DrinkDao
) : CocktailRepository {
    override val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
        get() = drinkDao.getFavoriteDrinks().mapLatest { favoriteDrink ->
            favoriteDrink.map { it.toDrinkDetailDomainModel() }
        }

    override suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>> {
        return try {
            val drinkLstDto = cocktailDbApi.SearchCocktail(ingredient)
            Result.success(drinkLstDto.drinks.mapNotNull {
                it.toDrinkDomainModel(favoritesDrinks.first().map { it.idDrink })
            })
        } catch (e: EOFException) {
            Result.success(emptyList())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getDrinkDetails(id: Int): Result<List<DrinkDetailDomainModel>> {
        return try {
            val drinksDetailsDto = cocktailDbApi.DrinkDetails(id)
            Result.success(drinksDetailsDto.drinks.mapNotNull { it.toDrinkDetailDomainModel() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel) {
        drinkDao.insertFavoriteDrink(drink.toFavoriteDrinkEntity())
    }

    override suspend fun removeFromFavorite(drinkId: Int) {
        val drink = drinkDao.getFavoriteDrinks().first().first { it.idDrink == drinkId }
        drinkDao.deleteFavoriteDrink(drink)
    }
}