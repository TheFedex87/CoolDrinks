package it.thefedex87.cooldrinks.presentation.favorite_drink

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.thefedex87.cooldrinks.domain.model.DrinkDetailDomainModel
import it.thefedex87.cooldrinks.domain.repository.CocktailRepository
import it.thefedex87.cooldrinks.util.Consts.TAG
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteDrinkViewModel @Inject constructor(
    private val repository: CocktailRepository
) : ViewModel() {
    var state by mutableStateOf(FavoriteDrinkState())
        private set

    init {
        viewModelScope.launch {
            repository.favoritesDrinks.collect {
                val glass = mutableListOf("None")
                glass.addAll(it.map { g -> g.glass }.distinct())
                state = state.copy(
                    drinks = filterDrinks(
                        alcoholFilter = state.alcoholFilter,
                        glassFilter = state.glassFilter,
                        drinks = it
                    ),
                    glasses = glass
                )
            }
        }
    }

    fun onEvent(event: FavoriteDrinkEvent) {
        viewModelScope.launch {
            when (event) {
                is FavoriteDrinkEvent.AlcoholFilterValueChanged -> {
                    Log.d(TAG, "Alcohol filter ${event.filter}")
                    state = state.copy(
                        alcoholFilter = event.filter,
                        alcoholMenuExpanded = false,
                        drinks = filterDrinks(
                            alcoholFilter = event.filter,
                            glassFilter = state.glassFilter,
                            drinks = repository.favoritesDrinks.first()
                        )
                    )
                }
                is FavoriteDrinkEvent.CollapseAlcoholMenu -> {
                    state = state.copy(alcoholMenuExpanded = false)
                }
                is FavoriteDrinkEvent.ExpandeAlcoholMenu -> {
                    state = state.copy(alcoholMenuExpanded = true)
                }
                is FavoriteDrinkEvent.GlassFilterValueChanged -> {
                    Log.d(TAG, "Glass filter ${event.filter}")
                    state = state.copy(
                        glassFilter = event.filter,
                        glassMenuExpanded = false,
                        drinks = filterDrinks(
                            alcoholFilter = state.alcoholFilter,
                            glassFilter = event.filter,
                            drinks = repository.favoritesDrinks.first()
                        )
                    )
                }
                is FavoriteDrinkEvent.CollapseGlassMenu -> {
                    state = state.copy(glassMenuExpanded = false)
                }
                is FavoriteDrinkEvent.ExpandeGlassMenu -> {
                    state = state.copy(glassMenuExpanded = true)
                }
            }
        }
    }

    private fun filterDrinks(
        alcoholFilter: AlcoholFilter,
        glassFilter: GlassFilter,
        drinks: List<DrinkDetailDomainModel>
    ): List<DrinkDetailDomainModel> {
        val filterByAlcohol = when (alcoholFilter) {
            AlcoholFilter.NONE -> {
                drinks
            }
            AlcoholFilter.ALCOHOLIC -> {
                drinks.filter { it.isAlcoholic }
            }
            AlcoholFilter.NOT_ALCOHOLIC -> {
                drinks.filter { !it.isAlcoholic }
            }
        }

        val filterByGlass = if (glassFilter == GlassFilter.NONE) {
            filterByAlcohol
        } else {
            filterByAlcohol.filter { it.glass == glassFilter.toString() }
        }

        return  filterByGlass
    }
}