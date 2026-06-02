package com.example.rom_cli.ui.fragment.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentWelcomeBinding
import androidx.navigation.findNavController

class WelcomeFragment : Fragment() {

    private var _binding : FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        _binding!!.registerButton.setOnClickListener {
            binding.root.findNavController().navigate(R.id.navigateToRegistration)
        }
    }
}