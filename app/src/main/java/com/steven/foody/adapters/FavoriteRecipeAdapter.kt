package com.steven.foody.adapters

import android.view.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.steven.foody.R
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.databinding.RecipesRowLayoutBinding
import com.steven.foody.ui.fragments.favorite.FavoriteRecipesFragmentDirections
import com.steven.foody.util.RecipesDiffUtil
import com.steven.foody.util.parseHtml
import com.steven.foody.viewmodels.MainViewModel

class FavoriteRecipeAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
) :
    RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteViewHolder>(),
    ActionMode.Callback {
    private var multiSelection = false
    private lateinit var mActionMode: ActionMode
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolder = arrayListOf<FavoriteViewHolder>()
    private var favoriteList = emptyList<FavoritesEntity>()
    private lateinit var rootView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val holder =
            RecipesRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(holder)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val recipe = favoriteList[position]
        holder.bind(recipe)
        rootView = holder.itemView.rootView
        myViewHolder.add(holder)
        saveItemStateOnScroll(recipe, holder)
        /**
         * Single click listener
         */
        holder.itemView.setOnClickListener {
            if (multiSelection) {
                applySelection(holder, recipe)
            } else {
                val action =
                    FavoriteRecipesFragmentDirections.actionFavoriteRecipesFragmentToDetailsActivity(
                        recipe.result
                    )
                holder.itemView.findNavController().navigate(action)
            }
        }

        /**
         * Long click listener
         */
        holder.itemView.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, recipe)
                true
            } else {
                applySelection(holder, recipe)
                true
            }
        }
    }

    override fun getItemCount(): Int = favoriteList.size

    private fun saveItemStateOnScroll(currentRecipe: FavoritesEntity, holder: FavoriteViewHolder) {
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.lightBackgroundColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackground, R.color.strokeColor)
            }
    }

    fun setData(newData: List<FavoritesEntity>) {
        val recipesDiffUtil = RecipesDiffUtil(favoriteList, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favoriteList = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    inner class FavoriteViewHolder(val binding: RecipesRowLayoutBinding) :
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

    private fun applySelection(holder: FavoriteViewHolder, currentRecipe: FavoritesEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            applyActionModeTitle()
            changeRecipeStyle(holder, R.color.cardBackground, R.color.strokeColor)
        } else {
            selectedRecipes.add(currentRecipe)
            applyActionModeTitle()
            changeRecipeStyle(holder, R.color.lightBackgroundColor, R.color.colorPrimary)
        }
    }

    private fun changeRecipeStyle(
        holder: FavoriteViewHolder,
        backgroundColor: Int,
        strokeColor: Int
    ) {
        holder.binding.recipesRow.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity,
                backgroundColor
            )
        )
        holder.binding.rowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favorite_contextual_menu, menu)
        mActionMode = mode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, menu: MenuItem?): Boolean {
        if (menu?.itemId == R.id.delete_favorite_menu) {
            selectedRecipes.forEach { recipes ->
                mainViewModel.deleteFavoriteRecipe(recipes)
            }
            showSnackBar("${selectedRecipes.size} recipe/s removed")
            multiSelection = false
            selectedRecipes.clear()
            mode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolder.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackground, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                multiSelection = false
                mActionMode.finish()
            }
            1 -> mActionMode.title = "1 item selected"
            else -> mActionMode.title = "${selectedRecipes.size} items selected"
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).setAction("okay") {}.show()
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }
}