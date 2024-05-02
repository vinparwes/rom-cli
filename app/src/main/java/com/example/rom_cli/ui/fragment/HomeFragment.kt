package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        _binding!!.analysisButton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToAnalysis) }
        _binding!!.profileButton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToProfile) }
        _binding!!.statisticsButton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToStatistics) }
        _binding!!.aboutButton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToAbout) }
    }

}