package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentStatisticsBinding
import com.example.rom_cli.databinding.FragmentWelcomeBinding

class StatisticsFragment : Fragment() {

    private var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        return binding.root
    }

}