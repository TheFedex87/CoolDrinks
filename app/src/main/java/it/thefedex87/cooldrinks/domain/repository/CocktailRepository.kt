package it.thefedex87.cooldrinks.domain.repository

import it.thefedex87.cooldrinks.domain.model.*
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow

interface CocktailRepository {
    suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>>
    suspend fun getDrinkDetails(id: Int?) : Result<List<DrinkDetailDomainModel>>
    suspend fun getIngredients(): Result<List<IngredientDomainModel>>
    suspend fun getIngredientDetails(ingredient: String): Result<IngredientDetailsDomainModel>

    val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
    suspend fun getFavoriteDrink(id: Int) : Flow<DrinkDetailDomainModel?>

    suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel): Long
    suspend fun removeFromFavorite(drinkId: Int)

    suspend fun updateVisualizationType(type: VisualizationType)
    val appPreferencesManager: Flow<AppPreferences>
}