package it.thefedex87.cooldrinks.data.repository

import com.google.common.truth.Truth.assertThat
import it.thefedex87.cooldrinks.data.local.FavoriteDrinkDao
import it.thefedex87.cooldrinks.data.local.IngredientsDao
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import it.thefedex87.cooldrinks.data.remote.TheCocktailDbApi
import it.thefedex87.cooldrinks.data.remote.validSearchDrinkResponse
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class CocktailRepositoryImplTest {
    private lateinit var repositoryImpl: CocktailRepositoryImpl
    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var api: TheCocktailDbApi
    private lateinit var drinksDao: FavoriteDrinkDao
    private lateinit var ingredientsDao: IngredientsDao
    private lateinit var preferencesManager: PreferencesManager

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        okHttpClient = OkHttpClient.Builder()
            .writeTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build()
        api = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(mockWebServer.url("/"))
            .build()
            .create(TheCocktailDbApi::class.java)


        drinksDao = mock(FavoriteDrinkDao::class.java)
        ingredientsDao = mock(IngredientsDao::class.java)

        `when`(drinksDao.getFavoriteDrinks()).thenReturn(
            flow {
                emit(
                    listOf(
                        DrinkEntity(
                            idDrink = 1,
                            isAlcoholic = true,
                            category = "Beer",
                            name = "Name1",
                            drinkThumb = "",
                            glass = "",
                            ingredients = "",
                            instructions = "",
                            measures = "",
                            addedDayOfMonth = 21,
                            addedMonth = 2,
                            addedYear = 2023,
                            dominantColor = 0,
                            isCustomCocktail = false,
                            isFavorite = true
                        )
                    )
                )
            }
        )

        `when`(ingredientsDao.getStoredIngredient(anyString())).thenReturn(
            flow {
                emit(
                    listOf(
                        IngredientEntity(
                            id = 1,
                            name = "Ingredient 1",
                            description = "Ingredient description 1",
                            imagePath = "",
                            type = "",
                            alcoholic = true,
                            availableLocal = true,
                            isPersonalIngredient = false
                        )
                    )
                )
            }
        )

        preferencesManager = mock(PreferencesManager::class.java)

        repositoryImpl = CocktailRepositoryImpl(
            cocktailDbApi = api,
            drinkDao = drinksDao,
            ingredientDao = ingredientsDao,
            preferencesManager = preferencesManager
        )
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `check searchDrinks returns success when a valid response is received from api`() =
        runTest {

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validSearchDrinkResponse)
            )

            val response = repositoryImpl.searchCocktails(
                "test",
                null
            )

            assertThat(response.isSuccess).isTrue()
        }

    @Test
    fun `check searchDrinks returns failure when an invalid response is received from api`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody("")
            )

            val response = repositoryImpl.searchCocktails(
                "test",
                null
            )

            assertThat(response.isFailure).isTrue()
        }

    @Test
    fun `check if received list of searched drinks is correctly parsed to the DrinkDomainModel list`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validSearchDrinkResponse)
            )

            val response = repositoryImpl.searchCocktails(
                "test",
                null
            )

            assertThat(response.getOrNull()?.size).isEqualTo(4)
            assertThat(response.getOrNull()?.get(1)?.id).isEqualTo(2)
            assertThat(response.getOrNull()?.get(1)?.image).isEqualTo("Image2")
            assertThat(response.getOrNull()?.get(1)?.name).isEqualTo("Name2")
            assertThat(response.getOrNull()?.get(1)?.isFavorite).isEqualTo(false)
        }
}