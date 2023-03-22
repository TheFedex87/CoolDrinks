package it.thefedex87.cooldrinks.presentation.bar

import com.google.common.truth.Truth.assertThat
import it.thefedex87.cooldrinks.data.repository.CocktailRepositoryFake
import it.thefedex87.cooldrinks.domain.model.DrinkDomainModel
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkViewModel
import it.thefedex87.cooldrinks.utils.AndroidTestMainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BarViewModelTest {

    @get:Rule
    var mainCoroutineRule = AndroidTestMainCoroutineRule()

    private lateinit var searchDrinkViewModel: SearchDrinkViewModel
    private lateinit var cocktailRespotioryFake: CocktailRepositoryFake

    @Before
    fun setup() {
        cocktailRespotioryFake = CocktailRepositoryFake()
        cocktailRespotioryFake.searchDrinkResult = listOf(
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

    @Test
    fun searchDrinks_setDrinksListAndNullErrorMessageOnState_onSuccessRepositorySearchDrinksSuccess() = runTest {
        searchDrinkViewModel = SearchDrinkViewModel(cocktailRespotioryFake)

        assertThat(searchDrinkViewModel.state.visualizationType).isEqualTo(VisualizationType.Card)

        cocktailRespotioryFake.preferencesManager.updateVisualizationType(VisualizationType.List)

        advanceTimeBy(100)

        assertThat(searchDrinkViewModel.state.visualizationType).isEqualTo(VisualizationType.List)
    }
}