package it.thefedex87.cooldrinks.presentation.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.components.MiniFabSpec
import it.thefedex87.cooldrinks.presentation.search_drink.components.BubblesBackGround
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState

@Composable
fun BarScreen(
    paddingValues: PaddingValues,
    onComposed: (BottomNavigationScreenState) -> Unit,
    currentBottomNavigationScreenState: BottomNavigationScreenState = BottomNavigationScreenState(),
    onMiniFabIngredientsListClicked: () -> Unit,
    onMiniFabCustomIngredientClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        onComposed(
            currentBottomNavigationScreenState.copy(
                fabState = currentBottomNavigationScreenState.fabState.copy(
                    floatingActionButtonVisible = true,
                    floatingActionButtonLabel = null,
                    floatingActionButtonMultiChoice = listOf(
                        MiniFabSpec(icon = Icons.Default.List, onClick = {
                            onMiniFabIngredientsListClicked()
                        }),
                        MiniFabSpec(icon = Icons.Default.Edit, onClick = {
                            onMiniFabCustomIngredientClicked()
                        })
                    ),
                    floatingActionButtonMultiChoiceExtended = false,
                ),
                prevFabState = currentBottomNavigationScreenState.fabState.copy(),
                topBarVisible = false,
                bottomBarVisible = true
            )
        )
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.bar_bg_5_b
            ),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 0.3f
        )
        /*BubblesBackGround(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            bottomPadding = paddingValues.calculateBottomPadding()
        )*/
        Text(
            text = "Your bar seems to be empty...add your first liquor",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center
        )
    }
}