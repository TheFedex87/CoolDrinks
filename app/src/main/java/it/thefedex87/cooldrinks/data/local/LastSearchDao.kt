package it.thefedex87.cooldrinks.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import it.thefedex87.cooldrinks.data.local.entity.LastSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastSearchDao {
    @Query("SELECT * FROM LastSearchEntity")
    fun getLastSearch(): Flow<List<LastSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastSearch(drinks: List<LastSearchEntity>)

    @Query("DELETE FROM LastSearchEntity")
    suspend fun removeLastSearch()
}