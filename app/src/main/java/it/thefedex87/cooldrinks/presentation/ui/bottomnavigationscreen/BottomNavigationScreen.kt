package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import android.os.Bundle
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
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
import it.thefedex87.cooldrinks.presentation.ingredients.IngredientsScreen
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.search_drink.SearchDrinkScreen
import it.thefedex87.cooldrinks.util.Consts.TAG

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun BottomNavigationScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val topBarColor = MaterialTheme.colorScheme.surface
    var bottomNavigationScreenState by remember {
        mutableStateOf(BottomNavigationScreenState(topBarColor = topBarColor))
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
                title = bottomNavigationScreenState.topBarTitle,
                topBarVisible = bottomNavigationScreenState.topBarVisible,
                scrollBehavior = bottomNavigationScreenState.topAppBarScrollBehavior?.invoke(),
                navController = navController,
                actions = bottomNavigationScreenState.topBarActions ?: {},
                color = bottomNavigationScreenState.topBarColor ?: MaterialTheme.colorScheme.surface
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
            composable(
                route = BottomNavScreen.Search.route
            ) {
                val ingredient = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("ingredient")

                Log.d(TAG, "Passed ingredient is: $ingredient")

                SearchDrinkScreen(
                    snackbarHostState = snackbarHostState,
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    onIngredientListClicked = {
                        navController.navigate(Route.INGREDIENTS)
                    },
                    ingredient = ingredient,
                    onDrinkClicked = { id, color, name ->
                        navController.navigate("${Route.DRINK_DETAILS}/$color/$id/$name")
                    }
                )

                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.remove<String>("ingredient")
            }
            composable(BottomNavScreen.Favorite.route) {
                FavoriteDrinkScreen(
                    snackbarHostState = snackbarHostState,
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
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
                    onComposed = { state ->
                        bottomNavigationScreenState =
                            state.copy(topBarTitle = it.arguments?.getString("drinkName")!!)
                    }
                )
            }
            composable(
                route = Route.INGREDIENTS
            ) {
                /*Button(onClick = {
                    /*navController.navigate(
                        route = "${BottomNavScreen.Search.route}?ingredient=lime"
                    ) {
                        popUpTo("${BottomNavScreen.Search.route}?ingredient={ingredient}") { inclusive = true }
                    }*/
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("ingredient", "lime")
                    navController.popBackStack()
                }) {
                    Text(text = "TEST BACK")
                }*/
                IngredientsScreen(
                    onComposed = { state ->
                        bottomNavigationScreenState =
                            state
                    },
                    onItemClick = {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("ingredient", it)
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun TopAppBar(
    title: String,
    topBarVisible: Boolean,
    scrollBehavior: TopAppBarScrollBehavior?,
    navController: NavHostController,
    actions: @Composable RowScope.() -> Unit = { },
    color: Color
) {
    if (topBarVisible)
        SmallTopAppBar(
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = color
            ),
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
            scrollBehavior = scrollBehavior,
            actions = actions
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    onComposed: (BottomNavigationScreenState) -> Unit
) {
    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())
    val topBarColor = MaterialTheme.colorScheme.surface

    onComposed(
        BottomNavigationScreenState(
            topBarVisible = true,
            bottomBarVisible = false,
            topAppBarScrollBehavior = {
                appBarScrollBehavior
            },
            topBarColor = topBarColor
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) {
        items(50) {
            Text(text = this.toString())
        }
    }
}