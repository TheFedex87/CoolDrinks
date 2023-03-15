package it.thefedex87.cooldrinks.presentation.components.cocktail

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.*
import it.thefedex87.cooldrinks.domain.model.VisualizationType
import it.thefedex87.cooldrinks.presentation.components.cocktail.model.DrinkUiModel
import it.thefedex87.cooldrinks.presentation.search_drink.components.VisualizationTypeSelector
import it.thefedex87.cooldrinks.presentation.util.calcDominantColor
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ColumnScope.CocktailView(
    maxHeight: Dp,
    maxWidth: Dp,
    isLoading: Boolean,
    drinks: List<MutableState<DrinkUiModel>>,
    visualizationType: VisualizationType,
    onDrinkClicked: (Int, Int, String) -> Unit,
    onFavoriteClicked: (DrinkUiModel) -> Unit,
    onVisualizationTypeChanged: (VisualizationType) -> Unit,
    onSelectDrinkDrawableChanged: (Drawable?) -> Unit
) {
    /*val defaultDominantColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    val animatedDominantColor = remember {
        Animatable(defaultDominantColor)
    }

    LaunchedEffect(key1 = dominantColor) {
        animatedDominantColor.animateTo(dominantColor)
    }

    BubblesBackGround(
        color = animatedDominantColor.value,
        bottomPadding = paddingValues.calculateBottomPadding()
    )*/


    val pagerState = rememberPagerState()
    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect {
            if (drinks.isNotEmpty()) {
                //dominantColor =
                //    Color(drinks[it].value.dominantColor).copy(alpha = 0.8f)
                //selectedDrinkDrawable = drinks[it].value.imageDrawable
                onSelectDrinkDrawableChanged(drinks[it].value.imageDrawable)
            }
        }
    }

    val columnState = rememberLazyListState()

    if (maxHeight > 500.dp &&
        visualizationType == VisualizationType.Card
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            count = drinks.size,
            contentPadding = PaddingValues(
                if (maxWidth > 600.dp) {
                    128.dp
                } else {
                    64.dp
                }
            ),
            state = pagerState
        ) { page ->
            if (page <= drinks.lastIndex) {
                val drink = drinks[page]
                PagerDrinkItem(
                    drink = drink.value,
                    onItemClick = onDrinkClicked,
                    pageOffset = calculateCurrentOffsetForPage(page).absoluteValue,
                    onFavoriteClick = onFavoriteClicked,
                    calcDominantColor = { drawable, onFinish ->
                        calcDominantColor(drawable, drink, onFinish)
                    },
                    onImageLoaded = {
                        drink.value = drink.value.copy(imageDrawable = it)
                        if (page == pagerState.currentPage) {
                            onSelectDrinkDrawableChanged(it)
                        }
                    }
                )
            }
        }
    } else if (maxHeight <= 500.dp ||
        visualizationType == VisualizationType.List
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = columnState
        ) {
            items(drinks) { drink ->
                DrinkItem(
                    drink = drink.value,
                    onItemClick = { id, color, name ->
                        onDrinkClicked(id, color, name)
                    },
                    onFavoriteClick = {
                        onFavoriteClicked(it)
                    },
                    calcDominantColor = { drawable, onFinish ->
                        calcDominantColor(drawable, drink, onFinish)
                    }
                )
            }
        }
    }

    if (maxHeight > 500.dp) {
        VisualizationTypeSelector(
            selectedVisualizationType = visualizationType,
            onClick = {
                onVisualizationTypeChanged(it)
            }
        )
    }
}