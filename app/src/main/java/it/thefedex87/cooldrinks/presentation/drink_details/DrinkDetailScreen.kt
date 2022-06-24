package it.thefedex87.cooldrinks.presentation.drink_details

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun DrinkDetailScreen(
    dominantColor: Color,
    drinkId: Int,
    navController: NavHostController,
    appBarScrollBehavior: TopAppBarScrollBehavior,
    viewModel: DrinkDetailViewModel = hiltViewModel()
) {
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