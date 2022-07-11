package it.thefedex87.cooldrinks.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientsDao {
    @Query("SELECT * FROM IngredientEntity")
    fun getAllIngredients(): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)
}