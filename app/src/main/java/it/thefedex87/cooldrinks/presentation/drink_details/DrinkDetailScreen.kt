package it.thefedex87.cooldrinks.presentation.drink_details

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.material3.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import it.thefedex87.cooldrinks.presentation.navigaton.Route
import it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen.BottomNavigationScreenState
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
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
        onComposed(
            BottomNavigationScreenState(
                topBarVisible = true,
                bottomBarVisible = false,
                topAppBarScrollBehavior = {
                    appBarScrollBehavior
                }
            )
        )
    }

    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.spaceSmall)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(drink.drinkThumb)
                    .crossfade(true)
                    .build(),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .clip(RoundedCornerShape(12.dp)),
                onLoading = {
                    it.thefedex87.cooldrinks.R.drawable.drink
                },
                onError = {
                    it.thefedex87.cooldrinks.R.drawable.drink
                },
                contentDescription = drink.name,
                contentScale = ContentScale.FillWidth
            )
        }
    }
}