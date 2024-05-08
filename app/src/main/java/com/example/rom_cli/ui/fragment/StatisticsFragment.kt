package com.example.rom_cli.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
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
        val sb : StringBuilder = StringBuilder()
        for(romResult in romResults!!) {
            val view : View = layoutInflater.inflate(R.layout.card_holder, null)
            val dateView = view.findViewById<TextView>(R.id.cardHolderDateView)
            val poseView = view.findViewById<TextView>(R.id.cardHolderPoseView)
            val romView = view.findViewById<TextView>(R.id.cardHolderRomView)
            dateView.text = romResult.dateRecorded
            poseView.text = romResult.poseIdentifier
            romView.text = romResult.recordedROM.toString()

            val selectButton = view.findViewById<Button>(R.id.romSessionEnterButton)
            selectButton.setOnClickListener {
                sb.append(romResult.dateRecorded).append("_").append(romResult.poseIdentifier)
                val action = StatisticsFragmentDirections.navigateFromStatisticsToRomHistory(sb.toString())
                Navigation.findNavController(binding.root).navigate(action)
            }
            sb.clear()
            container.addView(view)
        }
    }
}