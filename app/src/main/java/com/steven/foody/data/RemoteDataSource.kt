package com.steven.foody.data

import com.steven.foody.data.network.FoodRecipesApi
import com.steven.foody.models.FoodJoke
import com.steven.foody.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
){

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.getRecipes(queries)
    }

    suspend fun searchRecipes(queries: Map<String, String>): Response<FoodRecipe> {
        return foodRecipesApi.searchRecipes(queries)
    }

    suspend fun getFoodJoke(apiKey: String): Response<FoodJoke> {
        return foodRecipesApi.getFoodJoke(apiKey)
    }
}