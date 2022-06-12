package it.thefedex87.cooldrinks.data.repository

import it.thefedex87.cooldrinks.data.mapper.toDrinkDetailDomainModel
import it.thefedex87.cooldrinks.data.mapper.toDrinkDomainModel
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import java.io.EOFException

class CocktailRepositoryImpl constructor(
    val cocktailDbApi: TheCocktailDbApi
) : CocktailRepository {

    override suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>> {
        return try {
            val drinkLstDto = cocktailDbApi.SearchCocktail(ingredient)
            Result.success(drinkLstDto.drinks.mapNotNull { it.toDrinkDomainModel() })
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
}