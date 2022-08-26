package it.thefedex87.cooldrinks.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity

@Database(
    entities = [
        DrinkEntity::class,
        IngredientEntity::class
    ],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5, spec = CoolDrinkDatabase.MigrationFrom4To5::class)
    ]
)
abstract class CoolDrinkDatabase : RoomDatabase() {
    @RenameTable(fromTableName = "FavoriteDrinkEntity", toTableName = "DrinkEntity")
    class MigrationFrom4To5 : AutoMigrationSpec

    abstract val favoriteDrinkDao: FavoriteDrinkDao
    abstract val ingredientDao: IngredientsDao
}