package com.steven.foody.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.steven.foody.models.FoodRecipe
import com.steven.foody.util.Constants.RECIPES_TABLE

@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}