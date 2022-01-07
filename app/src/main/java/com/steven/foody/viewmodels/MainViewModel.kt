package com.steven.foody.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.steven.foody.data.Repository
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.data.database.entities.RecipesEntity
import com.steven.foody.models.FoodRecipe
import com.steven.foody.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
        private val repository: Repository,
        application: Application
) : AndroidViewModel(application) {

    /** Room */
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()
    val readFavorite: LiveData<List<FavoritesEntity>> = repository.local.readFavorite().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
            viewModelScope.launch(Dispatchers.IO) {
                repository.local.insertRecipes(recipesEntity)
            }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavorite(favoritesEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavorite(favoritesEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    /** Retrofit */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(queries: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(queries)
    }

    private suspend fun searchRecipesSafeCall(queries: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.searchRecipes(queries = queries)
                searchRecipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                searchRecipesResponse.value = NetworkResult.Error("Recipes not found")
            }
        } else {
            searchRecipesResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries = queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe != null) {
                    offlineCacheRecipes(foodRecipe)
                }

            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found")
            }
        } else {
            recipesResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun offlineCacheRecipes(data: FoodRecipe) {
        val recipesEntity = RecipesEntity(data)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        return when {
            response.message().toString().contains("timeout") -> NetworkResult.Error("Timeout")
            response.code() == 402 -> NetworkResult.Error("API key limited")
            response.body()!!.results.isNullOrEmpty() -> NetworkResult.Error("Recipes not found")
            response.isSuccessful -> NetworkResult.Success(response.body()!!)
            else -> NetworkResult.Error(response.message())
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
                getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}