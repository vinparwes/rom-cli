package com.example.rom_cli.ui.fragment.home.analysis

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rom_cli.databinding.FragmentShoulderAnalysisBinding
import androidx.navigation.findNavController

class ShoulderAnalysisFragment : Fragment() {

    private var _binding : FragmentShoulderAnalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentShoulderAnalysisBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        _binding!!.forwardFlexionSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("Forward Flexion")
            binding.root.findNavController().navigate(action)
        }
        _binding!!.abductionSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("Abduction")
            binding.root.findNavController().navigate(action)
        }
        _binding!!.adductionSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("Adduction")
            binding.root.findNavController().navigate(action)
        }
        _binding!!.externalRotationSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("External Rotation")
            binding.root.findNavController().navigate(action)
        }
    }

}