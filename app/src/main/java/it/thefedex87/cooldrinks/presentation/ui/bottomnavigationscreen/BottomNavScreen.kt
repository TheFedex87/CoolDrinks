package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.util.UiText

sealed class BottomNavScreen(
    val route: String,
    val icon: ImageVector,
    val title: UiText
) {
    object Bar : BottomNavScreen(
        route = Route.BAR,
        icon = Icons.Default.Liquor,
        title = UiText.StringResource(
            R.string.bar
        )
    )

    object Cocktail : BottomNavScreen(
        route = Route.COCKTAIL,
        icon = Icons.Default.LocalBar,
        title = UiText.StringResource(
            R.string.cocktails
        )
    )

    object Favorite :
        BottomNavScreen(
            route = Route.FAVORITES,
            icon = Icons.Default.Favorite,
            title = UiText.StringResource(R.string.favorites)
        )

    object RandomDrink : BottomNavScreen(
        route = Route.RANDOM_DRINK,
        icon = Icons.Default.Casino,
        title = UiText.StringResource(R.string.feel_lucky)
    )
}