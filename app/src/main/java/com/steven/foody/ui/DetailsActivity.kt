package com.steven.foody.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.steven.foody.R
import com.steven.foody.adapters.PagerAdapter
import com.steven.foody.data.database.entities.FavoritesEntity
import com.steven.foody.databinding.ActivityDetailsBinding
import com.steven.foody.ui.fragments.ingredients.IngredientsFragment
import com.steven.foody.ui.fragments.instructions.InstructionsFragment
import com.steven.foody.ui.fragments.overview.OverviewFragment
import com.steven.foody.util.Constants.RECIPE_RESULT_KEY
import com.steven.foody.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val args by navArgs<DetailsActivityArgs>()
    private val viewModel: MainViewModel by viewModels()
    private var recipeSaved = false
    private var saveRecipeId = 0
    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        binding.toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = arrayListOf(
            OverviewFragment(),
            IngredientsFragment(),
            InstructionsFragment()
        )
        val titles = arrayListOf("Overview", "Ingredients", "Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        //binding.viewPager.isUserInputEnabled = false
        binding.viewPager.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.save_to_favorite)
        checkSavedRecipes(menuItem)
        return true
    }

    private fun checkSavedRecipes(menu: MenuItem) {
        viewModel.readFavorite.observe(this, { favorites ->
            try {
                for(savedFavorite in favorites) {
                    if (savedFavorite.result.id == args.result.id) {
                        changeMenuItemColor(menu, R.color.yellow)
                        saveRecipeId = savedFavorite.id
                        recipeSaved = true
                        break
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.save_to_favorite && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.save_to_favorite && recipeSaved) {
            removeFromFavorite(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToFavorites(item: MenuItem) {
        viewModel.insertFavoriteRecipe(FavoritesEntity(0, args.result))
        //changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Recipe save.")
        //recipeSaved = true
    }

    private fun removeFromFavorite(item: MenuItem) {
        viewModel.deleteFavoriteRecipe(FavoritesEntity(saveRecipeId, args.result))
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from favorites.")
        recipeSaved = false
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(binding.detailsLayout, message, Snackbar.LENGTH_SHORT)
            .setAction("Okay") {}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(this, color))
    }

   override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem, R.color.white)
    }
}