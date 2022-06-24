package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.util.Consts.TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreen(
    dominantColor: Color,
    drinkId: Int,
    navController: NavHostController,
    onComposed: (BottomNavigationScreenState) -> Unit,
    viewModel: DrinkDetailViewModel = hiltViewModel()
) {
    val appBarScrollBehavior =
        TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarScrollState())

    LaunchedEffect(key1 = true) {
        Log.d(TAG, "SETTING bottomNavigationScreenState on Detail")
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = true,
                bottomBarVisible = false,
                topAppBarScrollBehavior = {
                    appBarScrollBehavior
                },
                topAppBarActions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Api, contentDescription = null)
                    }
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null)
                    }
                }
            )
        )
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) {
        item {
            Button(onClick = {
                navController.navigate(Route.TEST)
            }) {
                Text(text = "Navigate to test screen")
            }
        }
        items(50) {
            Text(text = this.toString())
        }
    }
}