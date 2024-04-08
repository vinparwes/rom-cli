package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding : FragmentWelcomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        _binding!!.registerButton.setOnClickListener { Navigation.findNavController(binding.root).navigate(R.id.navigateToRegistration) }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}