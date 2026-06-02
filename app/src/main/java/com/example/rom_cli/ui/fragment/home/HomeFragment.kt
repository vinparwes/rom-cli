package com.example.rom_cli.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(args.romRecorded) {
            Toast.makeText(context, "ROM Recorded and accessible in history", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupButtons() {
        binding.analysisButton.setOnClickListener { binding.root.findNavController().navigate(R.id.navigateToAnalysis) }
        binding.statisticsButton.setOnClickListener { binding.root.findNavController().navigate(R.id.navigateToStatistics) }
    }
}