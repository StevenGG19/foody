package com.steven.foody.ui.fragments.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import coil.load
import com.steven.foody.R
import com.steven.foody.databinding.FragmentOverviewBinding
import com.steven.foody.models.Result
import com.steven.foody.util.Constants.RECIPE_RESULT_KEY
import com.steven.foody.util.parseHtml

class OverviewFragment : Fragment() {
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        binding.imgMain.load(myBundle?.image)
        binding.txtTitle.text = myBundle?.title
        binding.txtLikes.text = myBundle?.aggregateLikes.toString()
        binding.txtTime.text = myBundle?.readyInMinutes.toString()
        binding.txtSummary.text = myBundle?.summary.parseHtml()

        setColors(binding.imgCheckMark, binding.txtVegetable, myBundle?.vegetarian ?: false)
        setColors(binding.imgCheckMarkVegan, binding.txtVegan, myBundle?.vegan ?: false)
        setColors(binding.imgCheckMarkGluten, binding.txtGluten, myBundle?.glutenFree ?: false)
        setColors(binding.imgCheckMarkDairy, binding.txtDairy, myBundle?.dairyFree ?: false)
        setColors(binding.imgCheckMarkHealthy, binding.txtHealthy, myBundle?.veryHealthy ?: false)
        setColors(binding.imgCheckMarkCheap, binding.txtCheap, myBundle?.cheap ?: false)

        return binding.root
    }

    private fun setColors(imgCheckMark: ImageView, textView: TextView, stateIsOn: Boolean) {
        if (stateIsOn) {
            imgCheckMark.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }
}