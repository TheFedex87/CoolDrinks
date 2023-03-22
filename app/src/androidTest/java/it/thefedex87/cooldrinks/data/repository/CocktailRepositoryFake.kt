package it.thefedex87.cooldrinks.data.repository

import it.thefedex87.cooldrinks.data.preferences.PreferencesManagerFake
import it.thefedex87.cooldrinks.domain.model.*
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CocktailRepositoryFake : CocktailRepository {
    var shouldReturnError: Boolean = false
    var delayResponse: Long = 0

    val preferencesManager: PreferencesManagerFake = PreferencesManagerFake()

    var searchDrinkResult = listOf<DrinkDomainModel>()
    var favoriteDrinks = MutableStateFlow<List<DrinkDetailDomainModel>>(
        listOf()
    )

    override suspend fun searchCocktails(
        ingredient: String?,
        name: String?
    ): Result<List<DrinkDomainModel>> {
        delay(delayResponse)
        return if(shouldReturnError) {
            Result.failure(Throwable("Error"))
        } else {
            Result.success(searchDrinkResult)
        }
    }

    override suspend fun getDrinkDetails(id: Int?): Result<List<DrinkDetailDomainModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getIngredients(): Result<List<IngredientDomainModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getIngredientDetails(ingredientName: String): Result<IngredientDetailsDomainModel> {
        TODO("Not yet implemented")
    }

    override suspend fun queryLocalIngredients(ingredientName: String): Result<List<DrinkIngredientModel>> {
        TODO("Not yet implemented")
    }

    override val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
        get() = favoriteDrinks

    override suspend fun getDrinkById(id: Int): Flow<DrinkDetailDomainModel?> {
        TODO("Not yet implemented")
    }

    override val myDrinks: Flow<List<DrinkDetailDomainModel>>
        get() = TODO("Not yet implemented")

    override suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertMyDrink(drink: DrinkDetailDomainModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOrRemoveFromFavorite(drinkId: Int) {
        TODO("Not yet implemented")
    }

    override val storedLiquors: Flow<List<IngredientDetailsDomainModel>>
        get() = TODO("Not yet implemented")

    override suspend fun storeIngredients(ingredients: List<IngredientDetailsDomainModel>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateIngredient(ingredient: IngredientDetailsDomainModel) {
        TODO("Not yet implemented")
    }

    override suspend fun updateVisualizationType(type: VisualizationType) {
        TODO("Not yet implemented")
    }

    override val appPreferencesManager: Flow<AppPreferences>
        get() = preferencesManager.preferencesFlow()
}