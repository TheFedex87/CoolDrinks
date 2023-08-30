package it.thefedex87.cooldrinks.domain.utils

interface BitmapManager {
    suspend fun saveBitmapLocal(source: String, destination: String): String
    fun deleteBitmap(path: String)
    fun createBitmapBk(source: String, destination: String)
}