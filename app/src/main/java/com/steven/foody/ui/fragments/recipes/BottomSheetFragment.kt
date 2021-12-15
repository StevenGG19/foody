package com.steven.foody.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.steven.foody.R
import com.steven.foody.databinding.BottomSheetBinding
import com.steven.foody.util.Constants.GLUTEN_FREE
import com.steven.foody.util.Constants.MAIN_COURSE
import com.steven.foody.viewmodels.RecipesViewModel

class BottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: BottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel
    private var mealTypeChip = MAIN_COURSE
    private var mealTypeChipId = 0
    private var dietTypeChip = GLUTEN_FREE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = BottomSheetBinding.inflate(inflater, container, false)

        viewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, { value ->
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            updateChip(value.selectedMealTypeId, binding.chipGroup)
            updateChip(value.selectedDietTypeId, binding.chipDietGroup)
        })

        binding.chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            mealTypeChip = chip.text.toString().lowercase()
            mealTypeChipId = selectedChipId
        }

        binding.chipDietGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            dietTypeChip = chip.text.toString().lowercase()
            dietTypeChipId = checkedId
        }

        binding.btnApply.setOnClickListener {
            viewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId
            )
            val action = BottomSheetFragmentDirections.actionBottomSheetFragmentToRecipesFragment(true)
            findNavController().navigate(action)
        }
        return binding.root
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                Log.d("BottomSheet", e.message.toString())
            }
        }
    }

}