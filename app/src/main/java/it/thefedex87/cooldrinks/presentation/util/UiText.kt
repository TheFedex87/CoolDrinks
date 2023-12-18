package it.thefedex87.cooldrinks.presentation.util

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class UiText: Parcelable {
    data class DynamicString(val text: String): UiText()
    data class StringResource(val resId: Int): UiText()
    object Empty: UiText()

    fun asString(context: Context): String {
        return when(this) {
            is DynamicString -> text
            is StringResource -> context.getString(resId)
            is Empty -> ""
        }
    }
}