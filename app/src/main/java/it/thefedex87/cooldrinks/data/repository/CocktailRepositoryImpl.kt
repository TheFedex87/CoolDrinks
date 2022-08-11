package it.thefedex87.cooldrinks.data.repository

import it.thefedex87.cooldrinks.data.local.FavoriteDrinkDao
import it.thefedex87.cooldrinks.data.local.IngredientsDao
import it.thefedex87.cooldrinks.data.mapper.*
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.domain.model.*
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import java.io.EOFException

class CocktailRepositoryImpl constructor(
    private val cocktailDbApi: TheCocktailDbApi,
    private val drinkDao: FavoriteDrinkDao,
    private val ingredientDao: IngredientsDao,
    private val preferencesManager: PreferencesManager
) : CocktailRepository {
    override val appPreferencesManager: Flow<AppPreferences>
        get() = preferencesManager.preferencesFlow()

    override suspend fun updateVisualizationType(type: VisualizationType) {
        preferencesManager.updateVisualizationType(type)
    }

    override val favoritesDrinks: Flow<List<DrinkDetailDomainModel>>
        get() = drinkDao.getFavoriteDrinks().mapLatest { favoriteDrink ->
            favoriteDrink.map { it.toDrinkDetailDomainModel() }
        }

    override suspend fun getFavoriteDrink(id: Int): Flow<DrinkDetailDomainModel?> = flow {
        emit(drinkDao.getFavoriteDrink(id)?.toDrinkDetailDomainModel())
    }

    override suspend fun searchCocktails(
        ingredient: String
    ): Result<List<DrinkDomainModel>> {
        return try {
            val drinkLstDto = cocktailDbApi.searchCocktail(
                ingredient = ingredient
            )

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

    override suspend fun getIngredients(): Result<List<IngredientDomainModel>> {
        return try {
            val ingredients = cocktailDbApi.ingredients().drinks

            Result.success(ingredients.map {
                it.toIngredientDomainModel()
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getIngredientDetails(ingredient: String): Result<IngredientDetailsDomainModel> {
        return try {
            val ingredient = cocktailDbApi
                .ingredientDetail(ingredient = ingredient)
                .ingredients
                .first()

            return Result.success(ingredient.toIngredientDetailsDomainModel())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getDrinkDetails(id: Int?): Result<List<DrinkDetailDomainModel>> {
        return try {
            val drinksDetailsDto =
                if (id != null)
                    cocktailDbApi.drinkDetails(id)
                else
                    cocktailDbApi.randomDrink()

            Result.success(drinksDetailsDto.drinks.mapNotNull { it.toDrinkDetailDomainModel() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel): Long {
        return drinkDao.insertFavoriteDrink(drink.toFavoriteDrinkEntity())
    }

    override suspend fun removeFromFavorite(drinkId: Int) {
        val drink = drinkDao.getFavoriteDrinks().first().first { it.idDrink == drinkId }
        drinkDao.deleteFavoriteDrink(drink)
    }

    override val storedLiquors: Flow<List<IngredientDetailsDomainModel>>
        get() = ingredientDao.getStoredIngredient().mapLatest { ingredients ->
            ingredients.map { i -> i.toIngredientDetailsDomainModel() }
        }

    override suspend fun storeIngredients(ingredients: List<IngredientDetailsDomainModel>) {
        try {
            ingredientDao.insertIngredient(ingredients = ingredients.map { it.toIngredientEntity() })
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }

    override suspend fun updateIngredient(ingredient: IngredientDetailsDomainModel) {
        try {
            ingredientDao.updateIngredient(ingredient.toIngredientEntity())
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        }
    }
}