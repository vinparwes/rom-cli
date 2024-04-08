package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.rom_cli.databinding.FragmentShoulderAnalysisBinding

class ShoulderAnalysisFragment : Fragment() {

    private var _binding : FragmentShoulderAnalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShoulderAnalysisBinding.inflate(inflater, container, false)
        _binding!!.forwardFlexionSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("Forward Flexion")
            Navigation.findNavController(binding.root).navigate(action)
        }
        _binding!!.abductionSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("Abduction")
            Navigation.findNavController(binding.root).navigate(action)
        }
        _binding!!.adductionSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("Adduction")
            Navigation.findNavController(binding.root).navigate(action)
        }
        _binding!!.externalRotationSelector.setOnClickListener {
            val action = ShoulderAnalysisFragmentDirections.navigateToPoseIntroduction("External Rotation")
            Navigation.findNavController(binding.root).navigate(action)
        }
        return binding.root
    }

}