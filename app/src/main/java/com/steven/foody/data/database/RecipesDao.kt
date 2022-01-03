package com.steven.foody.data.database

import androidx.room.*
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_table ORDER BY id ASC")
    fun readFavorites(): Flow<List<FavoritesEntity>>

    @Delete
    suspend fun deleteFavorite(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_table")
    suspend fun deleteAllFavorite()
}