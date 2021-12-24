package com.steven.foody.ui.fragments.ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.steven.foody.util.Constants.RECIPE_RESULT_KEY
import com.steven.foody.adapters.IngredientsAdapter
import com.steven.foody.databinding.FragmentIngredientsBinding
import com.steven.foody.models.Result

class IngredientsFragment : Fragment() {
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy { IngredientsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)
        myBundle?.extendedIngredients?.let { ingredient ->
            adapter.setData(ingredient)
        }
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvIngredients.adapter = adapter
        binding.rvIngredients.layoutManager = LinearLayoutManager(requireContext())
    }

}