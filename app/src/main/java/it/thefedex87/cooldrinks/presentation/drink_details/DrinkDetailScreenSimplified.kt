package it.thefedex87.cooldrinks.presentation.drink_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.presentation.drink_details.components.CharacteristicSection
import it.thefedex87.cooldrinks.presentation.drink_details.components.DrinkImage
import it.thefedex87.cooldrinks.presentation.drink_details.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing

/**
 * This compose is used to show drink detail on low resolution devices
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreenSimplified(
    calculatedDominantColor: Color?,
    appBarScrollBehavior: TopAppBarScrollBehavior,
    viewModel: DrinkDetailViewModel,
    imageSize: Double
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
            .padding(horizontal = spacing.spaceSmall)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.TopCenter)
                    .padding(
                        top = 80.dp,
                        start = spacing.spaceSmall,
                        end = spacing.spaceSmall,
                        bottom = spacing.spaceSmall
                    ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = (imageSize * 0.6).dp,
                                start = spacing.spaceSmall,
                                end = spacing.spaceSmall,
                                bottom = spacing.spaceSmall
                            )
                    ) {
                        CharacteristicSection(
                            drinkGlass = viewModel.state.drinkGlass,
                            drinkCategory = viewModel.state.drinkCategory,
                            drinkAlcoholic = viewModel.state.drinkAlcoholic?.asString(
                                LocalContext.current
                            )
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = spacing.spaceSmall,
                                end = spacing.spaceSmall,
                                bottom = spacing.spaceSmall
                            ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(spacing.spaceSmall),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ingredients",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Spacer(modifier = androidx.compose.ui.Modifier.height(spacing.spaceSmall))
                            viewModel.state.drinkIngredients?.map {
                                IngredientItem(
                                    ingredientName = it.name ?: "",
                                    ingredientMeasure = it.measure ?: ""
                                )
                            }
                        }
                    }
                    viewModel.state.drinkInstructions?.let { instructions ->
                        Text(
                            modifier = androidx.compose.ui.Modifier
                                .padding(spacing.spaceMedium),
                            text = instructions
                        )
                    }
                }
            }
            DrinkImage(
                imageSize = imageSize,
                calculatedDominantColor = calculatedDominantColor,
                viewModel = viewModel
            )
        }
        /*if (drinkId == null) {
            Spacer(modifier = Modifier.height(70.dp))
        }*/
    }
}