package com.steven.foody.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.steven.foody.R
import com.steven.foody.adapters.PagerAdapter
import com.steven.foody.databinding.ActivityDetailsBinding
import com.steven.foody.ui.fragments.ingredients.IngredientsFragment
import com.steven.foody.ui.fragments.instructions.InstructionsFragment
import com.steven.foody.ui.fragments.overview.OverviewFragment
import com.steven.foody.util.Constants.RECIPE_RESULT_KEY

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private val args by navArgs<DetailsActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        binding.toolBar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = arrayListOf(OverviewFragment(),
                IngredientsFragment(),
                InstructionsFragment())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}