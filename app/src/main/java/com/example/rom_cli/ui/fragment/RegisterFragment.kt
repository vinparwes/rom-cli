package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentRegisterBinding
import androidx.navigation.findNavController

class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        _binding!!.registerButton.setOnClickListener { binding.root.findNavController().navigate(R.id.navigateToHome) }
        _binding!!.loginBUtton.setOnClickListener { binding.root.findNavController().navigate(R.id.navigateToHome) }
    }

}