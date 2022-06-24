package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailScreen
import it.thefedex87.cooldrinks.presentation.favorite_drink.FavoriteDrinkScreen
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkScreen
import it.thefedex87.cooldrinks.util.Consts.TAG

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun BottomNavigationScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    navController: NavHostController
) {
    var bottomNavigationScreenState by remember {
        mutableStateOf(BottomNavigationScreenState())
    }

    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())

    navController.addOnDestinationChangedListener { _, destination, arguments ->
        if (destination.route?.startsWith(Route.SEARCH_ONLINE_DRINK) == true) {
            appBarScrollBehavior.state.offset = 0f
            appBarScrollBehavior.state.offsetLimit = 0f
            appBarScrollBehavior.state.contentOffset = 0f

            bottomNavigationScreenState = bottomNavigationScreenState.copy(
                bottomBarVisible = true,
                topBarVisible = false,
                topBarTitle = ""
            )
        } else if (destination.route?.startsWith(Route.FAVORITES) == true) {
            appBarScrollBehavior.state.offset = 0f
            appBarScrollBehavior.state.offsetLimit = 0f
            appBarScrollBehavior.state.contentOffset = 0f

            bottomNavigationScreenState = bottomNavigationScreenState.copy(
                bottomBarVisible = true,
                topBarVisible = false,
                topBarTitle = ""
            )
        } else if (destination.route?.startsWith(Route.DRINK_DETAILS) == true) {
            bottomNavigationScreenState = bottomNavigationScreenState.copy(
                bottomBarVisible = false,
                topBarVisible = true,
                topBarTitle = arguments?.getString("drinkName")!!
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = bottomNavigationScreenState.topBarTitle,
                topBarVisible = bottomNavigationScreenState.topBarVisible,
                scrollBehavior = appBarScrollBehavior,
                navController = navController
            )
        },
        bottomBar = {
            BottomBar(
                bottomBarVisible = bottomNavigationScreenState.bottomBarVisible,
                navController = navController
            )
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
                    onDrinkClicked = { id, color, name ->
                        navController.navigate("${Route.DRINK_DETAILS}/$color/$id/$name")
                    }
                )
            }
            composable(BottomNavScreen.Favorite.route) {
                FavoriteDrinkScreen(
                    snackbarHostState = snackbarHostState,
                    onDrinkClicked = { id, color, name ->
                        navController.navigate("${Route.DRINK_DETAILS}/$color/$id/$name")
                    }
                )
            }
            composable(
                route = "${Route.DRINK_DETAILS}/{dominantColor}/{drinkId}/{drinkName}",
                arguments = listOf(
                    navArgument("dominantColor") {
                        type = NavType.IntType
                    },
                    navArgument("drinkId") {
                        type = NavType.IntType
                    },
                    navArgument("drinkName") {
                        type = NavType.StringType
                    }
                )
            ) {
                val dominantColor =
                    it.arguments?.getInt("dominantColor")?.let { Color(it) }
                        ?: Color.White
                val drinkId = it.arguments?.getInt("drinkId")!!
                DrinkDetailScreen(
                    dominantColor = dominantColor,
                    drinkId = drinkId,
                    appBarScrollBehavior = appBarScrollBehavior,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun TopAppBar(
    title: String,
    topBarVisible: Boolean,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController
) {
    if (topBarVisible)
        SmallTopAppBar(
            title = {
                Text(text = title)
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Navigate back"
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )

}

@Composable
fun BottomBar(
    bottomBarVisible: Boolean,
    navController: NavHostController
) {
    val screens = listOf(
        BottomNavScreen.Search,
        BottomNavScreen.Favorite
    )

    AnimatedVisibility(
        visible = bottomBarVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
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