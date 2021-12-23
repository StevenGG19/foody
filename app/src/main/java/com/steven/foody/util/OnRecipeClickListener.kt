package com.steven.foody.util

interface OnRecipeClickListener<T> {
    fun onRecipeClick(recipe: T)
}