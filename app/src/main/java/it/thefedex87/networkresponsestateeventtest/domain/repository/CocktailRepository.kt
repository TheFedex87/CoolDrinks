package it.thefedex87.networkresponsestateeventtest.domain.repository

import it.thefedex87.networkresponsestateeventtest.domain.model.DrinkDomainModel

interface CocktailRepository {
    suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>>
}