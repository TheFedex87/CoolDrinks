package it.thefedex87.cooldrinks.data.local

import androidx.room.*
import it.thefedex87.cooldrinks.data.local.entity.FavoriteDrinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDrinkDao {
    @Query("SELECT * FROM FavoriteDrinkEntity")
    fun getFavoriteDrinks() : Flow<List<FavoriteDrinkEntity>>

    @Query("SELECT * FROM FavoriteDrinkEntity WHERE idDrink = :id")
    suspend fun getFavoriteDrink(id: Int) : FavoriteDrinkEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteDrink(drink: FavoriteDrinkEntity)

    @Delete
    fun deleteFavoriteDrink(drink: FavoriteDrinkEntity)
}