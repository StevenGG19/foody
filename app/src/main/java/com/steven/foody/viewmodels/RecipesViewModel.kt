package com.steven.foody.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.steven.foody.data.DataStoreRepository
import com.steven.foody.data.MealAndDietType
import com.steven.foody.util.Constants.API_KEY
import com.steven.foody.util.Constants.GLUTEN_FREE
import com.steven.foody.util.Constants.MAIN_COURSE
import com.steven.foody.util.Constants.QUERY_NUMBER
import com.steven.foody.util.Constants.QUERY_ADD_RECIPE_INFORMATION
import com.steven.foody.util.Constants.QUERY_API_KEY
import com.steven.foody.util.Constants.QUERY_DIET
import com.steven.foody.util.Constants.QUERY_FILL_INGREDIENTS
import com.steven.foody.util.Constants.QUERY_TYPE
import com.steven.foody.util.Constants.SEARCH_QUERY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    application: Application,
    private val dataStore: DataStoreRepository
) : AndroidViewModel(application) {

    var networkStatus = false
    var backOnline = false
    val readMealAndDietType = dataStore.readMealAndDietType
    val readBackOnline = dataStore.readBackOnline.asLiveData()
    private var mealAndDietType: MealAndDietType? = null

    private fun saveBackOnline(backOnline: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStore.saveBackOnline(backOnline)
    }

    fun saveMealAndDietType() =
        viewModelScope.launch(Dispatchers.IO) {
            mealAndDietType?.let {
                dataStore.saveMealAndDietType(
                    it.selectedMealType,
                    it.selectedMealTypeId,
                    it.selectedDietType,
                    it.selectedDietTypeId
                )
            }
        }

    fun saveMealAndDietTypeTemp(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        mealAndDietType = MealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[QUERY_NUMBER] = "10"
        queries[QUERY_API_KEY] = API_KEY
        mealAndDietType?.let {
            queries[QUERY_TYPE] = it.selectedMealType
            queries[QUERY_DIET] = it.selectedDietType
        }
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    fun applySearchQueries(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        queries[SEARCH_QUERY] = searchQuery
        queries[QUERY_NUMBER] = "10"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No internet connection", Toast.LENGTH_SHORT).show()
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We're back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

}