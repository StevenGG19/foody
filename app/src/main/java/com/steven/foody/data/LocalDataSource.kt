package com.steven.foody.data

import com.steven.foody.data.database.RecipesDao
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val recipesDao: RecipesDao) {

    fun readDatabase(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    fun readFavorite(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavorites()
    }

    suspend fun insertFavorite(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavorite(favoritesEntity)
    }

    suspend fun deleteFavorite(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavorite(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavorite()
    }
}