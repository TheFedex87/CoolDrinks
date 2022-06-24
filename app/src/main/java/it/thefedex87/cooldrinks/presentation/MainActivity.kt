package it.thefedex87.cooldrinks.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
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
/*
data class AppBarState(
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null
)

@Composable
fun ScreenA(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {

    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "My Screen A",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Filter,
                            contentDescription = null
                        )
                    }
                }
            )
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Screen A"
        )
        Button(onClick = {
            navController.navigate("screen_b")
        }) {
            Text(text = "Navigate to Screen B")
        }
    }
}

@Composable
fun ScreenB(
    onComposing: (AppBarState) -> Unit,
    navController: NavController
) {

    LaunchedEffect(key1 = true) {
        onComposing(
            AppBarState(
                title = "My Screen B",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }
                }
            )
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Screen B"
        )
        Button(onClick = {
            navController.popBackStack()
        }) {
            Text(text = "Navigate back to Screen A")
        }
    }
}

@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoolDrinksTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var appBarState by remember {
                        mutableStateOf(AppBarState())
                    }

                    Scaffold(
                        topBar = {
                            SmallTopAppBar(
                                title = {
                                    Text(text = appBarState.title)
                                },
                                actions = {
                                    appBarState.actions?.invoke(this)
                                }
                            )
                        }
                    ) { values ->
                        NavHost(
                            navController = navController,
                            startDestination = "screen_a",
                            modifier = Modifier.padding(
                                values
                            )
                        ) {
                            composable("screen_a") {
                                ScreenA(
                                    onComposing = {
                                        appBarState = it
                                    },
                                    navController = navController
                                )
                            }
                            composable("screen_b") {
                                ScreenB(
                                    onComposing = {
                                        appBarState = it
                                    },
                                    navController = navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
*/