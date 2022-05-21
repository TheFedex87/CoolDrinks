package it.thefedex87.networkresponsestateeventtest.data.remote

import it.thefedex87.networkresponsestateeventtest.data.remote.dto.DrinkListDto
import it.thefedex87.networkresponsestateeventtest.data.remote.dto.DrinksDetailDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCocktailDbApi {

    @GET("filter.php?")
    suspend fun SearchCocktail(
        @Query("i") ingredient: String
    ): DrinkListDto

    @GET("lookup.php?")
    suspend fun DrinkDetails(
        @Query("i") id: Int
    ): DrinksDetailDto

    companion object {
        const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }
}