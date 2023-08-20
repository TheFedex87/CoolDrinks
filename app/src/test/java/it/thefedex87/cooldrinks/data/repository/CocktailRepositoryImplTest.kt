package it.thefedex87.cooldrinks.data.repository

import com.google.common.truth.Truth.assertThat
import it.thefedex87.cooldrinks.data.local.FavoriteDrinkDao
import it.thefedex87.cooldrinks.data.local.IngredientsDao
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import it.thefedex87.cooldrinks.data.remote.*
import it.thefedex87.cooldrinks.data.utils.MockitoHelper.anyObject
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkIngredientModel
import it.thefedex87.cooldrinks.domain.preferences.PreferencesManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import java.time.LocalDate
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
        api = spy(
            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(mockWebServer.url("/"))
                .build()
                .create(TheCocktailDbApi::class.java)
        )


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
                        ),
                        DrinkEntity(
                            idDrink = 2,
                            isAlcoholic = true,
                            category = "Beer",
                            name = "Name2",
                            drinkThumb = "",
                            glass = "",
                            ingredients = "",
                            instructions = "",
                            measures = "",
                            addedDayOfMonth = 21,
                            addedMonth = 2,
                            addedYear = 2023,
                            dominantColor = 0,
                            isCustomCocktail = true,
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
    fun `check if received list of searched drinks is correctly mapped to the DrinkDomainModel list`() =
        runTest {
            `when`(drinksDao.getFavoriteDrinks()).thenReturn(
                flow {
                    emit(
                        emptyList()
                    )
                }
            )
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

    @Test
    fun `check if received list of searched drinks is recognized as favorite if the same id is in the favorites`() =
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

            assertThat(response.getOrNull()?.get(0)?.isFavorite).isEqualTo(true)
        }

    @Test
    fun `check if searchCocktailByIngredient api is called when the ingredient is passed to searchCocktail method`() =
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

            verify(api).searchCocktailByIngredient("test")
        }

    @Test
    fun `check if searchCocktailByName api is called when the name is passed to searchCocktail method`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validSearchDrinkResponse)
            )

            val response = repositoryImpl.searchCocktails(
                null,
                "test"
            )

            verify(api).searchCocktailByName("test")
        }

    @Test
    fun `check if invalid response is returned from searchCocktails if invalid json is received`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(invalidSearchDrinkResponse)
            )

            val response = repositoryImpl.searchCocktails(
                "test",
                null
            )

            assertThat(response.isFailure).isTrue()
        }

    @Test
    fun `check getIngredients returns success when a valid response is received from api`() =
        runTest {

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validGetIngredientsResponse)
            )

            val response = repositoryImpl.getIngredients()

            assertThat(response.isSuccess).isTrue()
        }

    @Test
    fun `check getIngredients returns failure when an invalid response is received from api`() =
        runTest {

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody("")
            )

            val response = repositoryImpl.getIngredients()

            assertThat(response.isFailure).isTrue()
        }

    @Test
    fun `check if received list of ingredients is correctly mapped to the IngredientDomainModel list`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(validGetIngredientsResponse)
        )

        val response = repositoryImpl.getIngredients()

        assertThat(response.getOrNull()?.size).isEqualTo(4)
        assertThat(response.getOrNull()?.get(0)?.name).isEqualTo("Ingredient 1")
        assertThat(response.getOrNull()?.get(0)?.thumbnail).isEqualTo("https://www.thecocktaildb.com/images/ingredients/Ingredient 1-Medium.png")
    }

    @Test
    fun `check invalid response is returned from getIngredients if invalid json is received`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(invalidGetIngredientsResponse)
        )

        val response = repositoryImpl.getIngredients()

        assertThat(response.isFailure).isTrue()
    }

    @Test
    fun `check getIngredientDetails returns success when a valid response is received from api`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validIngredientDetailsResponse)
            )

            val response = repositoryImpl.getIngredientDetails("test")

            assertThat(response.isSuccess).isTrue()
        }

    @Test
    fun `check getIngredientDetails returns failed when an invalid response is received from api`() =
        runTest {

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody("")
            )

            val response = repositoryImpl.getIngredientDetails("test")

            assertThat(response.isFailure).isTrue()
        }

    @Test
    fun `check getIngredientDetails is correctly mapped to ingredientDetailsDomainModel`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validIngredientDetailsResponse)
            )

            val response = repositoryImpl.getIngredientDetails("test")

            assertThat(response.getOrNull()?.id).isEqualTo(1)
            assertThat(response.getOrNull()?.name).isEqualTo("Ingredient name")
            assertThat(response.getOrNull()?.alcoholic).isEqualTo(true)
            assertThat(response.getOrNull()?.description).isEqualTo("Ingredient details")
            assertThat(response.getOrNull()?.type).isEqualTo("Ingredient type")
            assertThat(response.getOrNull()?.availableLocal).isEqualTo(false)
            assertThat(response.getOrNull()?.isPersonalIngredient).isEqualTo(false)
            assertThat(response.getOrNull()?.imagePath).isEqualTo("https://www.thecocktaildb.com/images/ingredients/Ingredient name.png")
        }

    @Test
    fun `check getIngredientDetails returns failed when an invalid json is received from api`() =
        runTest {

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(500)
                    .setBody(invalidIngredientDetailsResponse)
            )

            val response = repositoryImpl.getIngredientDetails("test")

            assertThat(response.isFailure).isTrue()
        }

    @Test
    fun `check getDrinkDetails returns success when a valid response is received from api`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(validDrinkDetailsResponse)
        )

        val response = repositoryImpl.getDrinkDetails(1)

        assertThat(response.isSuccess).isTrue()
    }

    @Test
    fun `check getDrinkDetails returns failed when an ivalid response is received from api`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("")
        )

        val response = repositoryImpl.getDrinkDetails(1)

        assertThat(response.isFailure).isTrue()
    }

    @Test
    fun `check getDrinkDetails is correctly mapped to drinkDetailsDomainModel`() =
        runTest {
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setBody(validDrinkDetailsResponse)
            )

            val response = repositoryImpl.getDrinkDetails(1)

            assertThat(response.getOrNull()?.size).isEqualTo(1)
            assertThat(response.getOrNull()?.first()?.idDrink).isEqualTo(11007)
            assertThat(response.getOrNull()?.first()?.name).isEqualTo("Margarita")
            assertThat(response.getOrNull()?.first()?.isFavorite).isEqualTo(false)
            assertThat(response.getOrNull()?.first()?.drinkThumb).isEqualTo("https://www.thecocktaildb.com/images/media/drink/5noda61589575158.jpg")
            assertThat(response.getOrNull()?.first()?.category).isEqualTo("Ordinary Drink")
            assertThat(response.getOrNull()?.first()?.addedDate).isEqualTo(null)
            assertThat(response.getOrNull()?.first()?.glass).isEqualTo("Cocktail glass")
            assertThat(response.getOrNull()?.first()?.isAlcoholic).isEqualTo(true)
            assertThat(response.getOrNull()?.first()?.instructions).isEqualTo("Rub the rim of the glass with the lime slice to make the salt stick to it. Take care to moisten only the outer rim and sprinkle the salt on it. The salt should present to the lips of the imbiber and never mix into the cocktail. Shake the other ingredients with ice, then carefully pour into the glass.")
            assertThat(response.getOrNull()?.first()?.ingredients?.filter { it.name != null }?.size).isEqualTo(4)
            assertThat(response.getOrNull()?.first()?.ingredients?.first()?.name).isEqualTo("Tequila")
            assertThat(response.getOrNull()?.first()?.ingredients?.first()?.measure).isEqualTo("1 1/2 oz ")
            assertThat(response.getOrNull()?.first()?.isCustomCocktail).isEqualTo(false)
        }

    @Test
    fun `check getDrinkDetails calls the end point drinkDetails if id is passed`() =
        runTest {
            repositoryImpl.getDrinkDetails(1)

            verify(api).drinkDetails(1)
        }

    @Test
    fun `check getDrinkDetails calls the end point randomDrink if id is not passed`() =
        runTest {
            repositoryImpl.getDrinkDetails(null)

            verify(api).randomDrink()
        }

    @Test
    fun `check if setting as favorite a custom drink the method setAsFavoriteUnfavorite of dao is called`() = runTest {
        val drink = DrinkDetailDomainModel(
            idDrink =1,
            isAlcoholic = true,
            category ="Cat 1",
            name ="Name 1",
            drinkThumb ="",
            glass = "Glass 1",
            ingredients = listOf(
                DrinkIngredientModel(
                    name = "Ingr 1",
                    measure = "Measure 1",
                    isAvailable = false
                )
            ),
            instructions = "Instructions",
            isCustomCocktail = true,
            isFavorite = false,
            addedDate = LocalDate.now(),
            dominantColor = null
        )

        repositoryImpl.insertIntoFavorite(drink)

        verify(drinksDao).setAsFavoriteUnfavorite(1, true)
    }

    @Test
    fun `check if setting as favorite a non custom drink the method setAsFavoriteUnfavorite of dao is called`() = runTest {
        val drink = DrinkDetailDomainModel(
            idDrink =1,
            isAlcoholic = true,
            category ="Cat 1",
            name ="Name 1",
            drinkThumb ="",
            glass = "Glass 1",
            ingredients = listOf(
                DrinkIngredientModel(
                    name = "Ingr 1",
                    measure = "Measure 1",
                    isAvailable = false
                )
            ),
            instructions = "Instructions",
            isCustomCocktail = false,
            isFavorite = false,
            addedDate = LocalDate.now(),
            dominantColor = null
        )

        repositoryImpl.insertIntoFavorite(drink)

        verify(drinksDao).insertDrink(anyObject())
    }

    @Test
    fun `check if removing a non custom drink from favorites calls the method deleteFavoriteDrink of drink dao`() = runTest {
        repositoryImpl.deleteOrRemoveFromFavorite(1)
        val drink = drinksDao.getFavoriteDrinks().first().firstOrNull()!!
        verify(drinksDao).deleteFavoriteDrink(drink)
    }

    @Test
    fun `check if removing a custom drink from favorites calls the method setAsFavoriteUnfavorite of drink dao`() = runTest {
        repositoryImpl.deleteOrRemoveFromFavorite(2)
        verify(drinksDao).setAsFavoriteUnfavorite(2, false)
    }
}