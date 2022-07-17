package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import it.thefedex87.cooldrinks.presentation.navigaton.Route

sealed class BottomNavScreen(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Search : BottomNavScreen(route = Route.SEARCH_ONLINE_DRINK, icon = Icons.Default.Search, title = "Search")
    object Favorite : BottomNavScreen(route = Route.FAVORITES, icon = Icons.Default.Favorite, title = "Favorite")
    object RandomDrink : BottomNavScreen(route = Route.RANDOM_DRINK, icon = Icons.Default.Casino, title = "Feel lucky")
}