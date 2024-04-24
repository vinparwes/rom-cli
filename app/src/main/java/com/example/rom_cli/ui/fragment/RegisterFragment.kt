package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        _binding!!.registerButton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToHome) }
        //TODO
        _binding!!.loginBUtton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToHome) }
        return binding.root
    }

}