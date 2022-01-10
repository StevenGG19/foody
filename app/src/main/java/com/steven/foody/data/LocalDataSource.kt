package com.steven.foody.data

import com.steven.foody.data.database.RecipesDao
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.data.database.entities.FoodJokeEntity
import com.steven.foody.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val recipesDao: RecipesDao) {

    fun readDatabase(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    fun readFavorite(): Flow<List<FavoritesEntity>> {
        return recipesDao.readFavorites()
    }

    fun readFoodJoke(): Flow<List<FoodJokeEntity>> {
        return recipesDao.readFoodJoke()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity) {
        recipesDao.insertRecipes(recipesEntity)
    }

    suspend fun insertFavorite(favoritesEntity: FavoritesEntity) {
        recipesDao.insertFavorite(favoritesEntity)
    }

    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) {
        recipesDao.insertFoodJoke(foodJokeEntity)
    }

    suspend fun deleteFavorite(favoritesEntity: FavoritesEntity) {
        recipesDao.deleteFavorite(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes() {
        recipesDao.deleteAllFavorite()
    }
}