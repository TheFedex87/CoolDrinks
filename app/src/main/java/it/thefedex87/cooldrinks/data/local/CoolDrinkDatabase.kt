package it.thefedex87.cooldrinks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import it.thefedex87.cooldrinks.data.local.entity.FavoriteDrinkEntity

@Database(
    entities = [
        FavoriteDrinkEntity::class
    ],
    version = 1
)
abstract class CoolDrinkDatabase : RoomDatabase() {
    abstract val drinkDao: DrinkDao
}