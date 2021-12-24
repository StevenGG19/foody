package com.steven.foody.ui.fragments.instructions

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.steven.foody.R
import com.steven.foody.databinding.FragmentInstructionsBinding
import com.steven.foody.models.Result
import com.steven.foody.util.Constants

class InstructionsFragment : Fragment() {
    private var _binding: FragmentInstructionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInstructionsBinding.inflate(inflater, container, false)
        val args = arguments
        val myBundle: Result? = args?.getParcelable(Constants.RECIPE_RESULT_KEY)
        binding.wvInstructions.webViewClient = object : WebViewClient() {}
        myBundle?.sourceUrl?.let { web ->
            binding.wvInstructions.loadUrl(web)
        }
        return binding.root
    }
}