package it.thefedex87.cooldrinks.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameTable
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity

@Database(
    entities = [
        DrinkEntity::class,
        IngredientEntity::class
    ],
    version = 6,
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

    companion object {
        val MigrationFrom5To6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // create new temp table
                database.execSQL("CREATE TABLE IF NOT EXISTS `IngredientEntityTemp` (`id` INTEGER, `name` TEXT NOT NULL, `description` TEXT, `imagePath` TEXT, `type` TEXT NOT NULL, `alcoholic` INTEGER NOT NULL, `availableLocal` INTEGER NOT NULL, `isPersonalIngredient` INTEGER NOT NULL DEFAULT false, PRIMARY KEY(`name`))")

                // copy data from old table to new
                database.execSQL("INSERT INTO IngredientEntityTemp (id, name, description, imagePath, type, alcoholic, availableLocal, isPersonalIngredient) SELECT id, name, description, imagePath, type, alcoholic, availableLocal, isPersonalIngredient FROM IngredientEntity")

                // delete old playlist_entry table
                database.execSQL("DROP TABLE IngredientEntity")

                // rename new table to playlist_entry
                database.execSQL("ALTER TABLE IngredientEntityTemp RENAME TO IngredientEntity")
            }

        }
    }



    abstract val favoriteDrinkDao: FavoriteDrinkDao
    abstract val ingredientDao: IngredientsDao
}