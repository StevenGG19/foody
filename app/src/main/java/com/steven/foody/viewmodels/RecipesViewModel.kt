package com.steven.foody.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.steven.foody.data.DataStoreRepository
import com.steven.foody.util.Constants.API_KEY
import com.steven.foody.util.Constants.GLUTEN_FREE
import com.steven.foody.util.Constants.MAIN_COURSE
import com.steven.foody.util.Constants.QUERY_NUMBER
import com.steven.foody.util.Constants.QUERY_ADD_RECIPE_INFORMATION
import com.steven.foody.util.Constants.QUERY_API_KEY
import com.steven.foody.util.Constants.QUERY_DIET
import com.steven.foody.util.Constants.QUERY_FILL_INGREDIENTS
import com.steven.foody.util.Constants.QUERY_TYPE
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

    private var mealType = MAIN_COURSE
    private var dietType = GLUTEN_FREE
    var networkStatus = false
    var backOnline = false
    val readMealAndDietType = dataStore.readMealAndDietType
    val readBackOnline = dataStore.readBackOnline.asLiveData()

    private fun saveBackOnline(backOnline: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        dataStore.saveBackOnline(backOnline)
    }

    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
            viewModelScope.launch(Dispatchers.IO) {
                dataStore.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
            }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        viewModelScope.launch {
            readMealAndDietType.collect { value ->
                mealType = value.selectedMealType
                dietType = value.selectedDietType
            }
        }
        queries[QUERY_NUMBER] = "10"
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
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