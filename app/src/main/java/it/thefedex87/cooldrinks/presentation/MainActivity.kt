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
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreen
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
                    BottomNavigationScreen(
                        snackbarHostState = snackbarHostState,
                        navController = navController
                    )
                }
            }
        }
    }
}
