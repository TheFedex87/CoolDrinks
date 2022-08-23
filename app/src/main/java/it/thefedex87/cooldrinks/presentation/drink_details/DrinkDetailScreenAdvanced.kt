package it.thefedex87.cooldrinks.presentation.drink_details

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import it.thefedex87.cooldrinks.R
import it.thefedex87.cooldrinks.presentation.drink_details.components.CharacteristicSection
import it.thefedex87.cooldrinks.presentation.drink_details.components.DrinkImage
import it.thefedex87.cooldrinks.presentation.drink_details.components.IngredientItem
import it.thefedex87.cooldrinks.presentation.ui.theme.LocalSpacing
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * This compose is used to show drink detail on high resolution devices
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrinkDetailScreenAdvanced(
    calculatedDominantColor: Color?,
    viewModel: DrinkDetailViewModel,
    imageSize: Double
) {
    val spacing = LocalSpacing.current
    val closeCardHeight = 62.dp
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
                .align(Alignment.TopStart)
        ) {
            var expandedIndex by remember {
                mutableStateOf(0)
            }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxSize(),
                onClick = {
                    expandedIndex = 0
                },
                shape = RoundedCornerShape(31.dp)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                        .padding(top = (imageSize * 0.6).dp)
                ) {
                    val maxHeight = this.maxHeight
                    val expandedHeight = maxHeight - closeCardHeight * 3

                    val anim1 = remember {
                        Animatable(1f)
                    }
                    val anim2 = remember {
                        Animatable(0f)
                    }
                    val anim3 = remember {
                        Animatable(0f)
                    }

                    LaunchedEffect(key1 = expandedIndex) {
                        when (expandedIndex) {
                            0 -> {
                                awaitAll(
                                    async { anim1.animateTo(1f) },
                                    async { anim2.animateTo(0f) },
                                    async { anim3.animateTo(0f) }
                                )
                            }
                            1 -> {
                                awaitAll(
                                    async { anim1.animateTo(0f) },
                                    async { anim2.animateTo(1f) },
                                    async { anim3.animateTo(0f) }
                                )
                            }
                            else -> {
                                awaitAll(
                                    async { anim1.animateTo(0f) },
                                    async { anim2.animateTo(0f) },
                                    async { anim3.animateTo(1f) }
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .height(closeCardHeight - 16.dp)
                        )
                        CharacteristicSection(
                            drinkGlass = viewModel.state.drinkGlass,
                            drinkCategory = viewModel.state.drinkCategory,
                            drinkAlcoholic = viewModel.state.drinkAlcoholic?.asString(
                                LocalContext.current
                            ),
                            modifier = Modifier
                                .height(
                                    expandedHeight * anim1.value
                                )
                        )
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                expandedIndex = 1
                            },
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(31.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp, horizontal = 16.dp)
                                        .height(closeCardHeight - 16.dp)
                                )
                                //Spacer(modifier = Modifier.height(spacing.spaceSmall))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .height(
                                            expandedHeight * anim2.value
                                        )
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .let {
                                                if (expandedIndex == 1) {
                                                    it.verticalScroll(rememberScrollState())
                                                } else it
                                            }

                                    ) {
                                        Text(
                                            text = buildAnnotatedString {
                                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                    append(
                                                        "${
                                                            viewModel.state.drinkIngredients?.filter { it.name != null }
                                                                ?.count { it.isAvailable == false }
                                                        }"
                                                    )
                                                }
                                                append(
                                                    " ${
                                                        stringResource(
                                                            id = R.string.missing_ingredients
                                                        )
                                                    }"
                                                )
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(spacing.spaceMedium))
                                        val ingredients = viewModel.state.drinkIngredients?.filter { it.name != null }
                                            ?.sortedBy { it.isAvailable == false }
                                        ingredients?.map {
                                            IngredientItem(
                                                ingredient = it,
                                                showHorizontalDivider = ingredients.indexOf(it) < ingredients.lastIndex
                                            )
                                        }
                                    }
                                }

                                ElevatedCard(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    onClick = {
                                        expandedIndex = 2
                                    },
                                    colors = CardDefaults.elevatedCardColors(
                                        containerColor = MaterialTheme.colorScheme.inverseSurface
                                    ),
                                    shape = RoundedCornerShape(31.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Instructions",
                                            style = MaterialTheme.typography.headlineSmall,
                                            modifier = Modifier
                                                .padding(
                                                    vertical = 8.dp,
                                                    horizontal = 16.dp
                                                )
                                                .height(closeCardHeight - 16.dp)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .padding(8.dp)
                                                .height(expandedHeight * anim3.value)
                                        ) {
                                            viewModel.state.drinkInstructions?.let {
                                                Text(
                                                    modifier = Modifier
                                                        .height(
                                                            expandedHeight * anim3.value
                                                        )
                                                        .let {
                                                            if (expandedIndex == 2) {
                                                                it.verticalScroll(state = rememberScrollState())
                                                            } else it
                                                        },
                                                    text = it
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        DrinkImage(
            imageSize = imageSize,
            calculatedDominantColor = calculatedDominantColor,
            viewModel = viewModel
        )
    }
}