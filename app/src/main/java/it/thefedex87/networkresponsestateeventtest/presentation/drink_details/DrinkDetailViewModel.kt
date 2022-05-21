package it.thefedex87.networkresponsestateeventtest.presentation.drink_details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DrinkDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    init {
        Log.d("drinkApp", savedStateHandle.get<Int>("drinkId")?.toString() ?: "Empty")
    }
}