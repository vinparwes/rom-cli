package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {

    private var _binding : FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        _binding!!.shoulderSelector.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToShoulderAnalysis) }
        _binding!!.kneeSelector.setOnClickListener { Toast.makeText(context, "Not yet implemented...", Toast.LENGTH_LONG).show() }
        return binding.root
    }

}