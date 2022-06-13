package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailScreen
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkScreen

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun BottomNavigationScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { values ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Search.route,
            modifier = modifier.padding(
                values
            )
        ) {
            composable(BottomNavScreen.Search.route) {
                SearchDrinkScreen(
                    snackbarHostState = snackbarHostState,
                    onShowDrinkDetailsClicked = { id, color ->
                        navController.navigate("${Route.DRINK_DETAILS}/$color/$id")
                    }
                )
            }
            composable(BottomNavScreen.Favorite.route) {
                Text(text = "Favorite")
            }
            composable(
                route = "${Route.DRINK_DETAILS}/{dominantColor}/{drinkId}",
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

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavScreen.Search,
        BottomNavScreen.Favorite
    )

    NavigationBar(
        //containerColor = Color(0xFF191C1D),
        //tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }

}

@Composable
fun RowScope.AddItem(
    screen: BottomNavScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    NavigationBarItem(
        icon = {
            Icon(imageVector = screen.icon, contentDescription = "Teams screen")
        },
        label = {
            Text(text = screen.title)
        },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id)
                launchSingleTop = true
            }
        }
    )
}