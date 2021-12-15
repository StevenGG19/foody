package com.steven.foody.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.steven.foody.util.Constants.DIET_TYPE
import com.steven.foody.util.Constants.DIET_TYPE_ID
import com.steven.foody.util.Constants.GLUTEN_FREE
import com.steven.foody.util.Constants.MAIN_COURSE
import com.steven.foody.util.Constants.MEAL_TYPE
import com.steven.foody.util.Constants.MEAL_TYPE_ID
import com.steven.foody.util.Constants.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext context: Context) {

    private object PreferenceKeys {
        val selectedMealType = stringPreferencesKey(MEAL_TYPE)
        val selectedMealTypeId = intPreferencesKey(MEAL_TYPE_ID)
        val selectedDietType = stringPreferencesKey(DIET_TYPE)
        val selectedDietTypeId = intPreferencesKey(DIET_TYPE_ID)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMealType] = mealType
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId
        }
    }

    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: MAIN_COURSE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: GLUTEN_FREE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )
        }
}

data class MealAndDietType(
    val selectedMealType: String,
    val selectedMealTypeId: Int,
    val selectedDietType: String,
    val selectedDietTypeId: Int
)