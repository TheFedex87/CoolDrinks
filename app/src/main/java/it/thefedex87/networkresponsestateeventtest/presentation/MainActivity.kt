package it.thefedex87.networkresponsestateeventtest.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import it.thefedex87.networkresponsestateeventtest.presentation.navigaton.Route.SEARCH_ONLINE_DRINK
import it.thefedex87.networkresponsestateeventtest.presentation.search_drink.SearchDrinkScreen
import it.thefedex87.networkresponsestateeventtest.presentation.ui.theme.NetworkResponseStateEventTestTheme

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetworkResponseStateEventTestTheme {
                val navController = rememberNavController()
                val scaffoldState = rememberScaffoldState()

                Scaffold(
                    scaffoldState = scaffoldState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = SEARCH_ONLINE_DRINK
                    ) {
                        composable(SEARCH_ONLINE_DRINK) {
                            SearchDrinkScreen(scaffoldState = scaffoldState, onShowDrinkDetailsClicked = {})
                        }
                    }
                }
            }
        }
    }
}
