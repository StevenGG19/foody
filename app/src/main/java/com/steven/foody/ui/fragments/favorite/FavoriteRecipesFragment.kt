package com.steven.foody.ui.fragments.favorite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.steven.foody.R
import com.steven.foody.adapters.FavoriteRecipeAdapter
import com.steven.foody.databinding.FragmentFavoriteRecipesBinding
import com.steven.foody.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {
    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { FavoriteRecipeAdapter() }
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        viewModel.readFavorite.observe(viewLifecycleOwner, { list->
            if (list.isNullOrEmpty()) {
                binding.imgNoData.visibility = View.VISIBLE
                binding.txtNoData.visibility = View.VISIBLE
            }else {
                binding.imgNoData.visibility = View.GONE
                binding.txtNoData.visibility = View.GONE
            }
            adapter.setData(list)
        })
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvFavorites.adapter = adapter
        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
    }

}