package com.steven.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.steven.foody.R
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.databinding.RecipesRowLayoutBinding
import com.steven.foody.util.RecipesDiffUtil
import com.steven.foody.util.parseHtml

class FavoriteRecipeAdapter : RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteViewHolder>() {
    private var favoriteList = emptyList<FavoritesEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val holder = RecipesRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(holder)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteList[position])
    }

    override fun getItemCount(): Int = favoriteList.size

    fun setData(newData: List<FavoritesEntity>) {
        val recipesDiffUtil = RecipesDiffUtil(favoriteList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favoriteList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class FavoriteViewHolder(private val binding: RecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: FavoritesEntity) {
            binding.txtTitle.text = favorite.result.title
            binding.txtDescription.text = favorite.result.summary.parseHtml()
            binding.txtFavorite.text = favorite.result.aggregateLikes.toString()
            binding.txtTime.text = favorite.result.readyInMinutes.toString()
            binding.imgRecipes.load(favorite.result.image) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
            if (favorite.result.vegan) {
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
        }
    }
}