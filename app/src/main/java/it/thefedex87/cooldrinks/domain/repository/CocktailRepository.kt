package it.thefedex87.cooldrinks.domain.repository

import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel

interface CocktailRepository {
    suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>>
    suspend fun getDrinkDetails(id: Int) : Result<List<DrinkDetailDomainModel>>
}