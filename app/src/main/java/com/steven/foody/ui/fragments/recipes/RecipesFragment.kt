package com.steven.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.steven.foody.R
import com.steven.foody.viewmodels.MainViewModel
import com.steven.foody.adapters.RecipesAdapter
import com.steven.foody.databinding.FragmentRecipesBinding
import com.steven.foody.util.NetworkResult
import com.steven.foody.util.observeOnce
import com.steven.foody.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { RecipesAdapter() }
    private val args by navArgs<RecipesFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        readDatabase()
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_recipesFragment_to_bottomSheetFragment)
        }
        return binding.root
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            viewModel.readRecipes.observeOnce(viewLifecycleOwner, { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet) {
                    Log.d("recipes", "database")
                    adapter.setData(database.first().foodRecipe)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            })
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.readRecipes.observe(viewLifecycleOwner, { database ->
                if (database.isNotEmpty()) {
                    adapter.setData(database[0].foodRecipe)
                }
            })
        }
    }

    private fun requestApiData() {
        Log.d("recipes", "Api")
        viewModel.getRecipes(recipesViewModel.applyQueries())
        viewModel.recipesResponse.observe(viewLifecycleOwner, { response ->
            when(response) {
                is NetworkResult.Loading -> showShimmerEffect()
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let { adapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    messageError(response.message.toString())
                }
            }
        })
    }

    private fun messageError(message: String) {
        binding.imgError.visibility = View.VISIBLE
        binding.txtError.visibility = View.VISIBLE
        binding.txtError.text = message
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffect()
    }

    private fun showShimmerEffect() {
        binding.recyclerView.showShimmer()
    }

    private fun hideShimmerEffect() {
        binding.recyclerView.hideShimmer()
    }
}