package it.thefedex87.cooldrinks.data.remote

import it.thefedex87.cooldrinks.BuildConfig
import it.thefedex87.cooldrinks.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCocktailDbApi {

    @GET("filter.php?")
    suspend fun searchCocktail(
        @Query("i") ingredient: String
    ): DrinkListDto

    @GET("lookup.php?")
    suspend fun drinkDetails(
        @Query("i") id: Int
    ): DrinksDetailDto

    @GET("random.php")
    suspend fun randomDrink(): DrinksDetailDto

    @GET("list.php?i=list")
    suspend fun ingredients(): IngredientsDto

    @GET("search.php?")
    suspend fun ingredientDetail(
        @Query("i")ingredient: String
    ): IngredientsDetailsDto

    companion object {
        const val BASE_URL = "https://www.thecocktaildb.com/api/json/v2/${BuildConfig.THE_COCKTAIL_DB_API}/"
    }
}