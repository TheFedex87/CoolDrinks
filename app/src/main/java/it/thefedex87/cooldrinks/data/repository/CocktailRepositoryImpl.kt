package it.thefedex87.cooldrinks.data.repository

import com.squareup.moshi.JsonDataException
import it.thefedex87.cooldrinks.data.local.FavoriteDrinkDao
import it.thefedex87.cooldrinks.data.local.IngredientsDao
import it.thefedex87.cooldrinks.data.mapper.*
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.data.remote.dto.DrinkListDto
import it.thefedex87.cooldrinks.domain.model.*
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import kotlinx.coroutines.flow.*
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
            favoriteDrink.map {
                it.toDrinkDetailDomainModel(
                    ingredientDao.getStoredIngredient().first().filter { it.availableLocal }
                        .map { it.toIngredientDomainModel() }
                )
            }
        }

    override suspend fun getDrinkById(id: Int): Flow<DrinkDetailDomainModel?> = flow {
        emit(drinkDao.getDrink(id)?.toDrinkDetailDomainModel(
            ingredientDao.getStoredIngredient().first().filter { it.availableLocal }
                .map { it.toIngredientDomainModel() }
        ))
    }

    override val myDrinks: Flow<List<DrinkDetailDomainModel>>
        get() = drinkDao.getMyDrinks().mapLatest { myDrink ->
            myDrink.map {
                it.toDrinkDetailDomainModel(
                    ingredientDao.getStoredIngredient().first().filter { it.availableLocal }
                        .map { it.toIngredientDomainModel() }
                )
            }
        }

    override suspend fun searchCocktails(
        ingredient: String?,
        name: String?
    ): Result<List<DrinkDomainModel>> {
        return try {
            val drinkLstDto: DrinkListDto = if (ingredient != null) {
                cocktailDbApi.searchCocktailByIngredient(
                    ingredient = ingredient
                )
            } else {
                cocktailDbApi.searchCocktailByName(
                    name = name!!
                )
            }

            Result.success(drinkLstDto.drinks.mapNotNull {
                it.toDrinkDomainModel(favoritesDrinks.first().map { it.idDrink })
            })
        } catch (e: EOFException) {
            Result.success(emptyList())
        } catch (e: JsonDataException) {
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

    override suspend fun queryLocalIngredients(ingredientName: String): Result<List<DrinkIngredientModel>> {
        return try {
            val ingredients = ingredientDao.getStoredIngredient(ingredientName).first()

            Result.success(ingredients.map {
                it.toDrinkIngredientDomainModel()
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun getIngredientDetails(ingredientName: String): Result<IngredientDetailsDomainModel> {
        return try {
            val ingredient = cocktailDbApi
                .ingredientDetail(ingredient = ingredientName)
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

            Result.success(drinksDetailsDto.drinks.mapNotNull {
                it.toDrinkDetailDomainModel(
                    ingredientDao.getStoredIngredient().first().filter { it.availableLocal }
                        .map { it.toIngredientDomainModel() },
                    favoritesDrinks.first().map { it.idDrink }
                )
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun insertIntoFavorite(drink: DrinkDetailDomainModel): Long {
        return if (!drink.isCustomCocktail)
            drinkDao.insertDrink(drink.toDrinkEntity(true))
        else {
            drinkDao.setAsFavoriteUnfavorite(drink.idDrink, true)
            drink.idDrink.toLong()
        }
    }

    override suspend fun insertMyDrink(drink: DrinkDetailDomainModel): Long {
        return drinkDao.insertDrink(drink.toDrinkEntity(false))
    }

    override suspend fun deleteOrRemoveFromFavorite(drinkId: Int) {
        val drink = drinkDao.getFavoriteDrinks().first().firstOrNull { it.idDrink == drinkId }
        drink?.let {
            if (!drink.isCustomCocktail) {
                drinkDao.deleteFavoriteDrink(drink)
            } else {
                drinkDao.setAsFavoriteUnfavorite(drink.idDrink, false)
            }
        }
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