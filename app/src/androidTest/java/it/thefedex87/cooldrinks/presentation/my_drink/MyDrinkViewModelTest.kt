package it.thefedex87.cooldrinks.presentation.my_drink

import it.thefedex87.cooldrinks.data.repository.CocktailRepositoryFake
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.utils.AndroidTestMainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class MyDrinkViewModelTest {
    @get:Rule
    var mainCoroutineRule = AndroidTestMainCoroutineRule()

    private lateinit var myDrinkViewModel: MyDrinkViewModel
    private lateinit var cocktailRepositoryFake: CocktailRepositoryFake

    @Before
    fun setup() {
        cocktailRepositoryFake = CocktailRepositoryFake()
        cocktailRepositoryFake.searchDrinkResult = listOf(
            DrinkDomainModel(
                name = "Drink 1",
                image = "Drink 1 image path",
                id = 1,
                isFavorite = false
            ),
            DrinkDomainModel(
                name = "Drink 2",
                image = "Drink 2 image path",
                id = 2,
                isFavorite = false
            ),
            DrinkDomainModel(
                name = "Drink 3",
                image = "Drink 3 image path",
                id = 3,
                isFavorite = true
            )
        )

        resetFavoritesList()
    }

    @After
    fun cleanup() {
        resetFavoritesList()
        cocktailRepositoryFake.shouldReturnError = false
        cocktailRepositoryFake.delayResponse = 0
    }

    private fun resetFavoritesList() {
        cocktailRepositoryFake.favoriteDrinks.value = listOf(
            DrinkDetailDomainModel(
                idDrink = 3,
                isAlcoholic = true,
                category = "Category 1",
                name = "Drink 3",
                drinkThumb = "",
                glass = "Glass 1",
                ingredients = listOf(),
                instructions = "",
                isCustomCocktail = false,
                isFavorite = true,
                addedDate = LocalDate.now(),
                dominantColor = null
            )
        )
    }
}