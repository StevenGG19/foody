package com.steven.foody.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.steven.foody.data.Repository
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.data.database.entities.FoodJokeEntity
import com.steven.foody.data.database.entities.RecipesEntity
import com.steven.foody.models.FoodJoke
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
    val readFoodJoke: LiveData<List<FoodJokeEntity>> = repository.local.readFoodJoke().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
            viewModelScope.launch(Dispatchers.IO) {
                repository.local.insertRecipes(recipesEntity)
            }

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavorite(favoritesEntity)
        }

    private fun insertFoodJoke(foodJokeEntity: FoodJokeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFoodJoke(foodJokeEntity)
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
    var jokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(queries: Map<String, String>) = viewModelScope.launch {
        searchRecipesSafeCall(queries)
    }

    fun getJoke(apiKey: String) = viewModelScope.launch {
        getJokeSafeCall(apiKey)
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

    private suspend fun getJokeSafeCall(apiKey: String) {
        jokeResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getFoodJoke(apiKey)
                jokeResponse.value = handleJokeResponse(response)
                val joke = jokeResponse.value!!.data
                if(joke != null) {
                    offlineCacheJoke(joke)
                }
            } catch (e: Exception) {
                jokeResponse.value = NetworkResult.Error("Recipes not found")
            }
        } else {
            jokeResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun offlineCacheRecipes(data: FoodRecipe) {
        val recipesEntity = RecipesEntity(data)
        insertRecipes(recipesEntity)
    }

    private fun offlineCacheJoke(data: FoodJoke) {
        val recipesEntity = FoodJokeEntity(data)
        insertFoodJoke(recipesEntity)
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

    private fun handleJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke> {
        return when {
            response.message().toString().contains("timeout") -> NetworkResult.Error("Timeout")
            response.code() == 402 -> NetworkResult.Error("API key limited")
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