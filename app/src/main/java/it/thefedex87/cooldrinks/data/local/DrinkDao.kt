package it.thefedex87.cooldrinks.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.thefedex87.cooldrinks.data.local.entity.FavoriteDrinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrinkDao {
    @Query("SELECT * FROM FavoriteDrinkEntity")
    fun getFavoriteDrinks() : Flow<List<FavoriteDrinkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteDrink(drink: FavoriteDrinkEntity)
}