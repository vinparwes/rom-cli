package com.example.rom_cli.ui.fragment

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.databinding.FragmentRomResultsBinding
import com.example.rom_cli.ui.fragment.vision.CameraFragmentArgs

class RomResultsFragment : Fragment() {

    private var _binding : FragmentRomResultsBinding? = null

    private val args: RomResultsFragmentArgs by navArgs()
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRomResultsBinding.inflate(inflater, container, false)
        binding.beforeImageView.setImageDrawable(BitmapDrawable(resources, args.beforeImageBitMap))
        binding.afterImageView.setImageDrawable(BitmapDrawable(resources, args.afterImageBitMap))
        binding.romResultText.text = args.finalRom
        return binding.root
    }

}