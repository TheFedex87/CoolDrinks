package it.thefedex87.cooldrinks.presentation.search_drink.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BubblesBackGround(
    color: Color,
    bottomPadding: Dp
) {
    val configuration = LocalConfiguration.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .offset(y = (-270).dp, x = 120.dp)
                .blur(3.dp)
                .padding(20.dp)
                .border(
                    width = 6.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        bottomStart = (configuration.screenWidthDp / 2).dp,
                        bottomEnd = (configuration.screenWidthDp / 2).dp
                    )
                )
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .height(399.dp)
                .fillMaxWidth()
                .offset(y = (-271).dp, x = 120.dp)
                .padding(20.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        bottomStart = (configuration.screenWidthDp / 2).dp,
                        bottomEnd = (configuration.screenWidthDp / 2).dp
                    )
                )
                .align(Alignment.TopEnd)
        )


        Box(
            modifier = Modifier
                .height(120.dp)
                .width(120.dp)
                .offset(x = (50).dp, y = 200.dp)
                .blur(3.dp)
                .padding(5.dp)
                .border(
                    width = 4.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .height(119.dp)
                .width(119.dp)
                .offset(x = (50).dp, y = 200.dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.TopEnd)
        )

        Box(
            modifier = Modifier
                .height(20.dp)
                .width(20.dp)
                .offset(x = 40.dp, y = 120.dp)
                .blur(2.dp)
                .padding(5.dp)
                .border(
                    width = 2.dp,
                    color = color,
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .height(19.dp)
                .width(19.dp)
                .offset(x = 40.dp, y = 120.dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .height(30.dp)
                .width(30.dp)
                .offset(x = 180.dp, y = 300.dp)
                .blur(3.dp)
                .padding(5.dp)
                .border(
                    width = 4.dp,
                    color = color,
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .height(29.dp)
                .width(29.dp)
                .offset(x = 180.dp, y = 300.dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .offset(x = 40.dp, y = 60.dp)
                .blur(3.dp)
                .padding(5.dp)
                .border(
                    width = 5.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.CenterStart)
        )
        Box(
            modifier = Modifier
                .height(77.dp)
                .width(77.dp)
                .offset(x = 41.dp, y = 61.dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.CenterStart)
        )

        Box(
            modifier = Modifier
                .height(10.dp)
                .width(10.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .blur(3.dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.BottomEnd)
        )
        Box(
            modifier = Modifier
                .height(9.dp)
                .width(9.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.BottomEnd)
        )

        Box(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .offset(x = (-20).dp, y = 60.dp)
                .blur(3.dp)
                .padding(5.dp)
                .border(
                    width = 3.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.BottomEnd)
        )
        Box(
            modifier = Modifier
                .height(99.dp)
                .width(99.dp)
                .offset(x = (-20).dp, y = 60.dp)
                .padding(5.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = CircleShape
                )
                .align(Alignment.BottomEnd)
        )

        Box(
            modifier = Modifier
                .height(400.dp)
                .fillMaxWidth()
                .offset(y = (300).dp - bottomPadding, x = (-200).dp)
                .blur(3.dp)
                .padding(20.dp)
                .border(
                    width = 6.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        topStart = (configuration.screenWidthDp / 2).dp,
                        topEnd = (configuration.screenWidthDp / 2).dp
                    )
                )
                .align(Alignment.BottomStart)
        )
        Box(
            modifier = Modifier
                .height(399.dp)
                .fillMaxWidth()
                .offset(y = (301).dp - bottomPadding, x = (-202).dp)
                .padding(20.dp)
                .border(
                    width = 1.dp,
                    color = color,
                    shape = RoundedCornerShape(
                        topStart = (configuration.screenWidthDp / 2).dp,
                        topEnd = (configuration.screenWidthDp / 2).dp
                    )
                )
                .align(Alignment.BottomStart)
        )
    }
}