package it.thefedex87.cooldrinks.presentation.bar

import com.google.common.truth.Truth.assertThat
import it.thefedex87.cooldrinks.data.repository.CocktailRepositoryFake
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkEvent
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkViewModel
import it.thefedex87.cooldrinks.presentation.util.UiEvent
import it.thefedex87.cooldrinks.utils.AndroidTestMainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class BarViewModelTest {

    @get:Rule
    var mainCoroutineRule = AndroidTestMainCoroutineRule()

    private lateinit var searchDrinkViewModel: SearchDrinkViewModel
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


    }

    @After
    fun cleanup() {
        resetFavoritesList()
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

    @Test
    fun searchDrinks_setDrinksListOnState_onRepositorySearchDrinksSuccess() = runTest {
        searchDrinkViewModel = SearchDrinkViewModel(cocktailRepositoryFake)
        cocktailRepositoryFake.shouldReturnError = false

        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnIngredientPassed("test"))
        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnSearchClick)

        advanceTimeBy(100)

        assertThat(searchDrinkViewModel.state.foundDrinks.size).isEqualTo(3)
    }

    @Test
    fun searchDrinks_setErrorMessageOnEventUi_onRepositorySearchDrinksFailed() = runTest {
        searchDrinkViewModel = SearchDrinkViewModel(cocktailRepositoryFake)
        cocktailRepositoryFake.shouldReturnError = true

        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnIngredientPassed("test"))
        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnSearchClick)

        val res = searchDrinkViewModel.uiEvent.first()
        assertThat(res).isInstanceOf(UiEvent.ShowSnackBar::class.java)
    }

    @Test
    fun searchDrinks_setLoadingOnState_onRepositorySearchDrinkLoading() = runTest {
        searchDrinkViewModel = SearchDrinkViewModel(cocktailRepositoryFake)
        cocktailRepositoryFake.shouldReturnError = false
        cocktailRepositoryFake.delayResponse = 1000

        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnIngredientPassed("test"))
        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnSearchClick)

        advanceTimeBy(500)

        assertThat(searchDrinkViewModel.state.isLoading).isTrue()

        advanceTimeBy(600)

        assertThat(searchDrinkViewModel.state.isLoading).isFalse()
    }

    @Test
    fun checkRemovingDrinkFromFavorite_updateTheElementInsideTheDrinkListInState() = runTest {
        searchDrinkViewModel = SearchDrinkViewModel(cocktailRepositoryFake)
        cocktailRepositoryFake.shouldReturnError = false

        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnIngredientPassed("test"))
        searchDrinkViewModel.onEvent(SearchDrinkEvent.OnSearchClick)

        advanceTimeBy(100)
        assertThat(searchDrinkViewModel.state.foundDrinks[2].value.isFavorite).isTrue()

        cocktailRepositoryFake.favoriteDrinks.value = listOf()

        advanceTimeBy(100)
        assertThat(searchDrinkViewModel.state.foundDrinks[2].value.isFavorite).isFalse()
    }

    @Test
    fun checkChanginVisualizationTypeOfSearchedDrinksInPreferences_updateTheVisualizationTypeInState() = runTest {
        searchDrinkViewModel = SearchDrinkViewModel(cocktailRepositoryFake)

        assertThat(searchDrinkViewModel.state.visualizationType).isEqualTo(VisualizationType.Card)
        cocktailRepositoryFake.preferencesManager.updateVisualizationType(VisualizationType.List)
        advanceTimeBy(10000)
        assertThat(searchDrinkViewModel.state.visualizationType).isEqualTo(VisualizationType.List)
    }
}