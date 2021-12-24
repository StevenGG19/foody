package com.steven.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.steven.foody.R
import com.steven.foody.databinding.IngredientsRowLayoutBinding
import com.steven.foody.models.ExtendedIngredient
import com.steven.foody.util.Constants.BASE_IMAGE_URL
import com.steven.foody.util.RecipesDiffUtil

class IngredientsAdapter : RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {
    private var ingredientList = emptyList<ExtendedIngredient>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        val itemBinding = IngredientsRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.bind(ingredientList[position])
    }

    override fun getItemCount(): Int = ingredientList.size

    fun setData(newData: List<ExtendedIngredient>) {
        val recipesDiffUtil = RecipesDiffUtil(ingredientList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        ingredientList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class IngredientsViewHolder(private val binding: IngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(ingredient: ExtendedIngredient) {
                binding.txtTitleIngredients.text = ingredient.name.replaceFirstChar { it.uppercase() }
                binding.txtAmount.text = ingredient.amount.toString()
                binding.txtIngredientUnit.text = ingredient.unit
                binding.txtIngredientOriginal.text = ingredient.original
                binding.imgIngredient.load(BASE_IMAGE_URL + ingredient.image) {
                        crossfade(600)
                        error(R.drawable.ic_error_placeholder)
                }
                binding.consistency.text = ingredient.consistency
            }

    }
}