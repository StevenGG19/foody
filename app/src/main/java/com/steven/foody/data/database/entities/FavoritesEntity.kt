package com.steven.foody.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.steven.foody.models.Result
import com.steven.foody.util.Constants.FAVORITE_RECIPE_TABLE

@Entity(tableName = FAVORITE_RECIPE_TABLE)
class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)