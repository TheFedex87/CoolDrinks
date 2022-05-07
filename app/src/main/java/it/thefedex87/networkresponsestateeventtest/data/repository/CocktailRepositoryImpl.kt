package it.thefedex87.networkresponsestateeventtest.data.repository

import it.thefedex87.networkresponsestateeventtest.data.mapper.toDrinkDomainModel
import it.thefedex87.networkresponsestateeventtest.data.remote.TheCocktailDbApi
import it.thefedex87.networkresponsestateeventtest.domain.model.DrinkDomainModel
import it.thefedex87.networkresponsestateeventtest.domain.repository.CocktailRepository

class CocktailRepositoryImpl constructor(
    val cocktailDbApi: TheCocktailDbApi
) : CocktailRepository {

    override suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>> {
        return try {
            val drinkLstDto = cocktailDbApi.SearchCocktail(ingredient)
            Result.success(drinkLstDto.drinks.mapNotNull { it.toDrinkDomainModel() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}