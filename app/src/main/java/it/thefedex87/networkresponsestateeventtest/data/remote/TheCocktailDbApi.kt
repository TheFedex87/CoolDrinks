package it.thefedex87.networkresponsestateeventtest.data.remote

import it.thefedex87.networkresponsestateeventtest.data.remote.dto.DrinkListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TheCocktailDbApi {

    @GET("filter.php?")
    suspend fun SearchCocktail(
        @Query("i") ingredient: String
    ): DrinkListDto

    companion object {
        const val BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1/"
    }
}