package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class BottomNavigationScreenState(
    val topBarColor: Color? = null,
    val topBarVisible: Boolean = false,
    val bottomBarVisible: Boolean = true,
    val topBarTitle: String = "",
    val topBarActions: (@Composable RowScope.() -> Unit)? = null,
    val topBarShowBack: Boolean = true,
    val topAppBarScrollBehavior: (@Composable () -> TopAppBarScrollBehavior)? = null,
    val floatingActionButtonVisible: Boolean = false,
    val floatingActionButtonLabel: String? = null,
    val floatingActionButtonPrevLabel: String? = null,
    val floatingActionButtonClicked: (() -> Unit)? = null,
)