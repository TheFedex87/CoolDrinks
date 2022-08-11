package it.thefedex87.cooldrinks.data.local

import androidx.room.*
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientsDao {
    @Query("SELECT * FROM IngredientEntity")
    fun getStoredIngredient(): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateIngredient(ingredient: IngredientEntity)
}