package com.example.rom_cli.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.marginLeft
import com.example.rom_cli.R
import com.example.rom_cli.data.FileController
import com.example.rom_cli.data.RomSessionResult
import com.example.rom_cli.databinding.FragmentStatisticsBinding

class StatisticsFragment : Fragment() {

    private var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var romResults : List<RomSessionResult>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        if(loadRomResults()) {
            setUpButtons()
        }
        return binding.root
    }

    private fun loadRomResults() : Boolean {
        romResults = FileController.getRomResults(requireContext());
        if (romResults != null) {
            if(romResults!!.isEmpty()) {
                Log.i("INFO", "List is empty..")
                //TODO Add element to inform user of no recorded activities
                return false
            } else {
                return true
            }
        }
        return false
    }
    private fun setUpButtons() {
        val container = binding.statisticsButtonContainer
        val context = requireContext()
        for(romResult in romResults!!) {
            val button = Button(context)
            button.setTextAppearance(R.style.statisticsFragmentButtonText)
            button.setBackgroundResource(R.drawable.custom_button)
            button.text = "${romResult.poseIdentifier} ${romResult.recordedROM}"
            button.setOnClickListener {
                Log.i("HEYEY", romResult.recordedROM.toString())
            }
            container.addView(button)
        }
    }
}