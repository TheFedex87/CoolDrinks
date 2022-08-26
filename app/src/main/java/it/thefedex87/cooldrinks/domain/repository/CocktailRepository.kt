package it.thefedex87.cooldrinks.domain.repository

import it.thefedex87.cooldrinks.domain.model.*
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import kotlinx.coroutines.flow.Flow

interface CocktailRepository {
    suspend fun searchCocktails(ingredient: String): Result<List<DrinkDomainModel>>
    suspend fun getDrinkDetails(id: Int?) : Result<List<DrinkDetailDomainModel>>
    suspend fun getIngredients(): Result<List<IngredientDomainModel>>
    suspend fun getIngredientDetails(ingredientName: String): Result<IngredientDetailsDomainModel>

    val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
    suspend fun getFavoriteDrink(id: Int) : Flow<DrinkDetailDomainModel?>

    val myDrinks: Flow<List<DrinkDetailDomainModel>>

    suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel): Long
    suspend fun removeFromFavorite(drinkId: Int)

    val storedLiquors: Flow<List<IngredientDetailsDomainModel>>
    suspend fun storeIngredients(ingredients: List<IngredientDetailsDomainModel>)
    suspend fun updateIngredient(ingredient: IngredientDetailsDomainModel)

    suspend fun updateVisualizationType(type: VisualizationType)
    val appPreferencesManager: Flow<AppPreferences>
}