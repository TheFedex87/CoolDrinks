package it.thefedex87.cooldrinks.data.remote

import it.thefedex87.cooldrinks.data.remote.dto.DrinkListDto
import it.thefedex87.cooldrinks.data.remote.dto.DrinksDetailDto
import it.thefedex87.cooldrinks.data.remote.dto.IngredientDto
import it.thefedex87.cooldrinks.data.remote.dto.IngredientsDto
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

    @GET("list.php?i=list")
    suspend fun ingredients(): IngredientsDto

    companion object {
        const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }
}