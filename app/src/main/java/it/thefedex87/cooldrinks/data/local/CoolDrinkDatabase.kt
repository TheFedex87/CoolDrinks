package it.thefedex87.cooldrinks.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import it.thefedex87.cooldrinks.data.local.entity.FavoriteDrinkEntity
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import it.thefedex87.cooldrinks.data.local.entity.LastSearchEntity

@Database(
    entities = [
        FavoriteDrinkEntity::class,
        IngredientEntity::class
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)
    ]
)
abstract class CoolDrinkDatabase : RoomDatabase() {
    abstract val favoriteDrinkDao: FavoriteDrinkDao
    abstract val ingredientDao: IngredientsDao
}