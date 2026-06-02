package com.example.rom_cli.ui.fragment.home.analysis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {

    private var _binding : FragmentAnalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        _binding!!.shoulderSelector.setOnClickListener { binding.root.findNavController().navigate(R.id.navigateToShoulderAnalysis) }
        _binding!!.kneeSelector.setOnClickListener { Toast.makeText(context, "Not yet implemented...", Toast.LENGTH_LONG).show() }
    }

}