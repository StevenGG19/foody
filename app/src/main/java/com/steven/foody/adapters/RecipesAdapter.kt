package com.steven.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.steven.foody.R
import com.steven.foody.databinding.RecipesRowLayoutBinding
import com.steven.foody.models.FoodRecipe
import com.steven.foody.models.Result
import com.steven.foody.util.RecipesDiffUtil
import com.steven.foody.util.parseHtml

class RecipesAdapter(private val click: (Result) -> Unit) :
    RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {
    private var recipes = emptyList<Result>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding =
            RecipesRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position], click)
    }

    override fun getItemCount(): Int = recipes.size

    fun setData(newData: FoodRecipe) {
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(recipe: Result, click: (Result) -> Unit) {
            binding.txtTitle.text = recipe.title
            binding.txtDescription.text = recipe.summary.parseHtml()
            binding.txtFavorite.text = recipe.aggregateLikes.toString()
            binding.txtTime.text = recipe.readyInMinutes.toString()
            binding.imgRecipes.load(recipe.image) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
            if (recipe.vegan) {
                binding.txtLeaf.setTextColor(
                    ContextCompat.getColor(
                        binding.txtLeaf.context,
                        R.color.green
                    )
                )
                binding.imgLeaf.setColorFilter(
                    ContextCompat.getColor(
                        binding.imgLeaf.context,
                        R.color.green
                    )
                )
            }
            itemView.setOnClickListener { click(recipe) }
        }
    }
}