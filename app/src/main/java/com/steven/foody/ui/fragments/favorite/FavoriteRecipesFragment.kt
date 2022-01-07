package com.steven.foody.ui.fragments.favorite

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.steven.foody.R
import com.steven.foody.adapters.FavoriteRecipeAdapter
import com.steven.foody.databinding.FragmentFavoriteRecipesBinding
import com.steven.foody.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteRecipesFragment : Fragment() {
    private var _binding: FragmentFavoriteRecipesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private val adapter by lazy { FavoriteRecipeAdapter(requireActivity(), viewModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteRecipesBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setHasOptionsMenu(true)
        viewModel.readFavorite.observe(viewLifecycleOwner, { list ->
            if (list.isNullOrEmpty()) {
                binding.imgNoData.visibility = View.VISIBLE
                binding.txtNoData.visibility = View.VISIBLE
            } else {
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

    override fun onDestroy() {
        super.onDestroy()
        adapter.clearContextualActionMode()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_recipes_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all) {
            viewModel.deleteAllFavoriteRecipes()
            showSnackBar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSnackBar() {
        Snackbar.make(binding.root, "All recipes removed.", Snackbar.LENGTH_SHORT)
            .setAction("okay") {}.show()
    }
}