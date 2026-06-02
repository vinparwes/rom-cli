package com.example.rom_cli.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.rom_cli.R
import com.example.rom_cli.data.rom_session.AppDatabase
import com.example.rom_cli.data.rom_session.RomSessionResult
import com.example.rom_cli.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsFragment : Fragment() {

    private var _binding : FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var romResults : List<RomSessionResult>? = null
    private var containerLayout : LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        containerLayout = binding.statisticsButtonContainer
        binding.statisticsBackButton.setOnClickListener {
            val action = StatisticsFragmentDirections.navigateFromStatisticsToHome(false)
            Navigation.findNavController(binding.root).navigate(action)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadRomResults()
    }

    private fun loadRomResults() {
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                AppDatabase.get(requireContext().applicationContext).romSessionDao().getAll()
            }
            if (results.isEmpty()) {
                romResults = null
                binding.statisticsButtonContainer.removeAllViews()
                return@launch
            }
            romResults = results
            setUpButtons()
        }
    }

    private fun setUpButtons() {
        val container = binding.statisticsButtonContainer
        container.removeAllViews()
        for (romResult in romResults!!) {
            val view : View = layoutInflater.inflate(R.layout.card_holder, container, false)
            val dateView = view.findViewById<TextView>(R.id.cardHolderDateView)
            val poseView = view.findViewById<TextView>(R.id.cardHolderPoseView)
            val romView = view.findViewById<TextView>(R.id.cardHolderRomView)

            dateView.text = romResult.dateRecorded
            poseView.text = romResult.poseIdentifier
            romView.text = romResult.recordedROM.toString()

            view.setOnClickListener {
                val action = StatisticsFragmentDirections.navigateFromStatisticsToRomHistory(romResult.uid)
                Navigation.findNavController(binding.root).navigate(action)
            }
            container.addView(view)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
