package it.thefedex87.cooldrinks.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailScreen
import it.thefedex87.cooldrinks.presentation.navigaton.Route.DRINK_DETAILS
import it.thefedex87.cooldrinks.presentation.navigaton.Route.SEARCH_ONLINE_DRINK
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkScreen
import it.thefedex87.cooldrinks.presentation.ui.theme.CoolDrinksTheme

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoolDrinksTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        modifier = Modifier.fillMaxSize()
                    ) { values ->
                        NavHost(
                            navController = navController,
                            startDestination = SEARCH_ONLINE_DRINK,
                            modifier = Modifier.padding(
                                values
                            )
                        ) {
                            composable(SEARCH_ONLINE_DRINK) {
                                SearchDrinkScreen(
                                    snackbarHostState = snackbarHostState,
                                    onShowDrinkDetailsClicked = { id, color ->
                                        navController.navigate("$DRINK_DETAILS/$color/$id")
                                    }
                                )
                            }
                            composable(
                                route = "$DRINK_DETAILS/{dominantColor}/{drinkId}",
                                arguments = listOf(
                                    navArgument("dominantColor") {
                                        type = NavType.IntType
                                    },
                                    navArgument("drinkId") {
                                        type = NavType.IntType
                                    }
                                )
                            ) {
                                val dominantColor =
                                    it.arguments?.getInt("dominantColor")?.let { Color(it) }
                                        ?: Color.White
                                val drinkId = it.arguments?.getInt("drinkId")!!
                                DrinkDetailScreen(
                                    dominantColor = dominantColor,
                                    drinkId = drinkId
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
