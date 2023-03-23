package it.thefedex87.cooldrinks.data.local

import androidx.room.*
import it.thefedex87.cooldrinks.data.local.entity.DrinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDrinkDao {
    @Query("SELECT * FROM DrinkEntity WHERE isFavorite = 1")
    fun getFavoriteDrinks() : Flow<List<DrinkEntity>>

    @Query("SELECT * FROM DrinkEntity")
    fun getAllStoredDrinks() : Flow<List<DrinkEntity>>

    @Query("SELECT * FROM DrinkEntity WHERE isCustomCocktail = 1")
    fun getMyDrinks() : Flow<List<DrinkEntity>>

    @Query("SELECT * FROM DrinkEntity WHERE idDrink = :id")
    suspend fun getDrink(id: Int) : DrinkEntity?

    @Query("UPDATE DrinkEntity SET isFavorite = :favorite WHERE idDrink = :id")
    suspend fun setAsFavoriteUnfavorite(id: Int, favorite: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrink(drink: DrinkEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDrink(drink: DrinkEntity)

    @Delete
    suspend fun deleteFavoriteDrink(drink: DrinkEntity)
}