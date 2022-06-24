package it.thefedex87.cooldrinks.presentation.ui.bottomnavigationscreen

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

data class BottomNavigationScreenState(
    val topBarVisible: Boolean = false,
    val bottomBarVisible: Boolean = true,
    val topBarTitle: String = "",
    val topAppBarScrollBehavior: (@Composable () -> TopAppBarScrollBehavior)? = null
)