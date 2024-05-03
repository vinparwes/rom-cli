package com.example.rom_cli.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.data.FileController
import com.example.rom_cli.data.RomSessionResult
import com.example.rom_cli.databinding.FragmentAnalysisBinding
import com.example.rom_cli.databinding.FragmentRegisterBinding
import com.example.rom_cli.databinding.FragmentRomHistoryBinding


class RomHistoryFragment : Fragment() {

    private var _binding : FragmentRomHistoryBinding? = null
    private val binding get() = _binding!!
    private val args: RomHistoryFragmentArgs by navArgs()
    private var romSession : RomSessionResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRomHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        romSession = FileController.getRomResult(requireContext(), args.fileKey)
        setupFields()
        setupButtons()
    }

    private fun setupFields() {
        TODO("Not yet implemented")
    }

    private fun setupButtons() {
        TODO("Not yet implemented")
    }


}