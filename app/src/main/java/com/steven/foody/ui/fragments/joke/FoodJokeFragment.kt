package com.steven.foody.ui.fragments.joke

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.steven.foody.R
import com.steven.foody.databinding.FragmentFoodJokeBinding
import com.steven.foody.util.Constants.API_KEY
import com.steven.foody.util.NetworkResult
import com.steven.foody.util.hide
import com.steven.foody.util.show
import com.steven.foody.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FoodJokeFragment : Fragment() {
    private var _binding: FragmentFoodJokeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<MainViewModel>()
    private var foodJoke = "No food joke"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodJokeBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        viewModel.getJoke(API_KEY)
        viewModel.jokeResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
                is NetworkResult.Loading -> {
                    binding.progressBar.show()
                }
                is NetworkResult.Success -> {
                    binding.progressBar.hide()
                    binding.cdText.show()
                    binding.txtFoodJoke.text = response.data?.text
                    if (response.data != null) {
                        foodJoke = response.data.text
                    }
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    binding.txtError.text = response.message.toString()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        })
        return binding.root
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.readFoodJoke.observe(viewLifecycleOwner, { dataBase ->
                if (!dataBase.isNullOrEmpty()) {
                    binding.progressBar.hide()
                    binding.cdText.show()
                    binding.txtFoodJoke.text = dataBase[0].foodJoke.text
                    foodJoke = dataBase[0].foodJoke.text
                } else {
                    binding.txtError.show()
                    binding.imgError.show()
                }
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.joke_menu) {
            val intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                this.type = "text/plain"
            }
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}