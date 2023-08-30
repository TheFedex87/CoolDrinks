package it.thefedex87.cooldrinks.data.utils

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import it.thefedex87.cooldrinks.domain.utils.BitmapManager
import it.thefedex87.cooldrinks.presentation.components.saveToLocalStorage
import it.thefedex87.cooldrinks.presentation.util.toBitmap
import java.io.File

class BitmapManagerImpl constructor(
    private val context: Context
): BitmapManager {
    override suspend fun saveBitmapLocal(source: String, destination: String): String {
        val bitmap = Uri.parse(source).toBitmap(context)
        context.filesDir.path
        bitmap.saveToLocalStorage(
            context,
            "$destination.jpg"
        )
        return "${context.filesDir.path}/$destination.jpg"
    }

    override fun deleteBitmap(path: String) {
        File(path).delete()
    }

    override fun createBitmapBk(source: String, destination: String) {
        File(source).copyTo(
            target = File(destination),
            overwrite = true
        )
    }
}