package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.add_ingredient.AddIngredientScreen
import it.thefedex87.cooldrinks.presentation.add_my_drink.AddMyDrinkScreen
import it.thefedex87.cooldrinks.presentation.bar.BarScreen
import it.thefedex87.cooldrinks.presentation.cocktail.CocktailTabScreen
import it.thefedex87.cooldrinks.presentation.components.MultiChoiceActionButton
import it.thefedex87.cooldrinks.presentation.components.MultiFabState
import it.thefedex87.cooldrinks.presentation.drink_details.DrinkDetailScreen
import it.thefedex87.cooldrinks.presentation.favorite_drink.FavoriteDrinkScreen
import it.thefedex87.cooldrinks.presentation.ingredients.IngredientsScreen
import it.thefedex87.cooldrinks.presentation.navigaton.Route
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
        floatingActionButton = {
            MyFloatingActionButton(
                fabState = bottomNavigationScreenState.fabState,
                prevFabState = bottomNavigationScreenState.prevFabState
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        topBar = {
            TopAppBar(
                title = bottomNavigationScreenState.topBarTitle,
                topBarVisible = bottomNavigationScreenState.topBarVisible,
                onBackPressed = bottomNavigationScreenState.topBarBackPressed,
                showBack = bottomNavigationScreenState.topBarShowBack,
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
            startDestination = BottomNavScreen.Bar.route,
            modifier = modifier.padding(
                values
            )
        ) {
            composable(
                route = BottomNavScreen.Bar.route
            ) {
                BarScreen(
                    currentBottomNavigationScreenState = bottomNavigationScreenState,
                    onComposed = {
                        bottomNavigationScreenState = it
                    },
                    paddingValues = values,
                    onMiniFabCustomIngredientClicked = {
                        navController.navigate(Route.ADD_INGREDIENT)
                    },
                    onMiniFabIngredientsListClicked = {
                        navController.navigate("${Route.INGREDIENTS}/false")
                    },
                    onSearchDrinkClicked = {
                        //navController.navigate("${BottomNavScreen.Search.route}?ingredient=$it")
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("ingredient", it)
                        Log.d(TAG, "Ingredient sent: $it")
                        navController.navigate(BottomNavScreen.Cocktail.route)
                    }
                )
            }
            composable(
                route = BottomNavScreen.Cocktail.route
            ) {
                // Ingredient which is got when click search from bar screen
                val ingredientForSearch = navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("ingredient")

                Log.d(TAG, "Ingredient received: $ingredientForSearch")
                CocktailTabScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    ingredientForSearch = ingredientForSearch,
                    onComposed = {
                        bottomNavigationScreenState = it
                    },
                    currentBottomNavigationScreenState = bottomNavigationScreenState
                )
            }
            /*composable(
                route = Route.SEARCH_ONLINE_DRINK,
                arguments = listOf(
                    navArgument("ingredientForSearch") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                // Ingredient which is got when ingredient is selected from the whole list of ingredients
                val ingredient = navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.get<String>("ingredient")

                // Ingredient which is got when click search from bar screen
                val ingredientForSearch = it.arguments?.getString("ingredientForSearch")
                Log.d(TAG, "Passed ingredient is: $ingredientForSearch")

                SearchDrinkScreen(
                    snackbarHostState = snackbarHostState,
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    //paddingValues = values,
                    currentBottomNavigationScreenState = bottomNavigationScreenState,
                    onIngredientListClicked = {
                        navController.navigate("${Route.INGREDIENTS}/true")
                    },
                    ingredient = ingredient ?: ingredientForSearch,
                    onDrinkClicked = { id, color, name ->
                        navController.navigate("${Route.DRINK_DETAILS}/$color/$id/$name")
                    }
                )

                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.remove<String>("ingredient")
            }*/
            composable(BottomNavScreen.Favorite.route) {
                FavoriteDrinkScreen(
                    snackbarHostState = snackbarHostState,
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    currentBottomNavigationScreenState = bottomNavigationScreenState,
                    onDrinkClicked = { id, color, name ->
                        navController.navigate("${Route.DRINK_DETAILS}/$color/$id/$name")
                    }
                )
            }
            composable(BottomNavScreen.RandomDrink.route) {
                DrinkDetailScreen(
                    calculatedDominantColor = null,
                    drinkId = null,
                    onDrinkLoaded = {
                        bottomNavigationScreenState = bottomNavigationScreenState.copy(
                            topBarTitle = it
                        )
                    },
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    currentBottomNavigationScreenState = bottomNavigationScreenState
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
                    calculatedDominantColor = dominantColor,
                    drinkId = drinkId,
                    onDrinkLoaded = null,
                    onComposed = { state ->
                        bottomNavigationScreenState =
                            state.copy(topBarTitle = it.arguments?.getString("drinkName")!!)
                    }
                )
            }
            composable(
                route = "${Route.INGREDIENTS}/{ingredientForSearch}",
                arguments = listOf(
                    navArgument("ingredientForSearch") {
                        type = NavType.BoolType
                    }
                )
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
                val ingredientForSearch = it.arguments?.getBoolean("ingredientForSearch") ?: true

                IngredientsScreen(
                    snackbarHostState = snackbarHostState,
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    onItemClick = {
                        if (ingredientForSearch) {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("ingredient", it)
                            navController.popBackStack()
                        } else {
                            //TODO: implement when we this screen has been opened from bar screen
                        }
                    },
                    isSelectionEnabled = !ingredientForSearch,
                    currentBottomNavigationScreenState = bottomNavigationScreenState,
                    navController = navController
                )
            }
            composable(
                route = Route.ADD_INGREDIENT
            ) {
                AddIngredientScreen(
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    currentBottomNavigationScreenState = bottomNavigationScreenState
                )
            }
            composable(
                route = Route.ADD_MY_DRINK
            ) {
                AddMyDrinkScreen(
                    onComposed = { state ->
                        bottomNavigationScreenState = state
                    },
                    currentBottomNavigationScreenState = bottomNavigationScreenState,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    topBarVisible: Boolean,
    showBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior?,
    navController: NavHostController,
    actions: @Composable RowScope.() -> Unit = { },
    onBackPressed: (() -> Unit)? = { navController.popBackStack() },
    color: Color
) {
    if (topBarVisible)
        SmallTopAppBar(
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = color
            ),
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                if (showBack) {
                    IconButton(
                        onClick = {
                            if(onBackPressed != null) onBackPressed.invoke() else navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
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
        BottomNavScreen.Bar,
        BottomNavScreen.Cocktail,
        BottomNavScreen.Favorite,
        BottomNavScreen.RandomDrink
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
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title.asString(LocalContext.current)
            )
        },
        label = {
            Text(text = screen.title.asString(LocalContext.current))
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MyFloatingActionButton(
    fabState: BottomNavigationScreenFabState,
    prevFabState: BottomNavigationScreenFabState
) {
    AnimatedVisibility(
        visible = fabState.floatingActionButtonVisible,
        enter = scaleIn(initialScale = 0f),
        exit = scaleOut(targetScale = 0f),
    ) {
        if (if (fabState.floatingActionButtonVisible) fabState.floatingActionButtonMultiChoice?.isEmpty() != false else prevFabState.floatingActionButtonMultiChoice?.isEmpty() != false) {
            ExtendedFloatingActionButton(
                onClick = fabState.floatingActionButtonClicked ?: {}
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = (if (fabState.floatingActionButtonVisible) fabState.floatingActionButtonIcon else prevFabState.floatingActionButtonIcon)
                            ?: Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.get_random_cocktail)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = (if (fabState.floatingActionButtonVisible) fabState.floatingActionButtonLabel else prevFabState.floatingActionButtonLabel)
                            ?: ""
                    )
                }
            }
        } else {
            var multiFabState by remember {
                mutableStateOf(MultiFabState.COLLAPSED)
            }
            MultiChoiceActionButton(
                onClick = {
                    multiFabState = it
                },
                state = multiFabState,
                miniFabs = if (fabState.floatingActionButtonVisible) fabState.floatingActionButtonMultiChoice!! else prevFabState.floatingActionButtonMultiChoice!!,
                isExtendedFab = if (fabState.floatingActionButtonVisible) fabState.floatingActionButtonMultiChoiceExtended else prevFabState.floatingActionButtonMultiChoiceExtended,
                extendedLabel = if (fabState.floatingActionButtonVisible) fabState.floatingActionButtonLabel else prevFabState.floatingActionButtonLabel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    onComposed: (BottomNavigationScreenState) -> Unit
) {
    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
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