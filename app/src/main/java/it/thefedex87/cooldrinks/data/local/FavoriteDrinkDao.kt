package it.thefedex87.cooldrinks.data.local

import androidx.room.*
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDrinkDao {
    @Query("SELECT * FROM DrinkEntity")
    fun getFavoriteDrinks() : Flow<List<DrinkEntity>>

    @Query("SELECT * FROM DrinkEntity WHERE idDrink = :id")
    suspend fun getFavoriteDrink(id: Int) : DrinkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteDrink(drink: DrinkEntity): Long

    @Delete
    fun deleteFavoriteDrink(drink: DrinkEntity)
}