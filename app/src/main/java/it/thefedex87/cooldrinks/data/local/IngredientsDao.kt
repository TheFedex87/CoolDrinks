package it.thefedex87.cooldrinks.data.local

import androidx.room.*
import it.thefedex87.cooldrinks.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientsDao {
    @Query("SELECT * FROM IngredientEntity WHERE name LIKE  '%' || :name || '%'")
    fun getStoredIngredient(name: String = ""): Flow<List<IngredientEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIngredient(ingredients: List<IngredientEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertIngredients(ingredients: List<IngredientEntity>)

    @Update(onConflict = OnConflictStrategy.ABORT)
    suspend fun updateIngredient(ingredient: IngredientEntity)

    @Delete
    suspend fun removeIngredient(ingredient: IngredientEntity)
}