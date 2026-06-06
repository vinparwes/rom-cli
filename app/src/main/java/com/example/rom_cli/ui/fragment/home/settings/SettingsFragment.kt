package com.example.rom_cli.ui.fragment.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.rom_cli.R
import com.example.rom_cli.domain.PoseLandmarkerHelper
import com.example.rom_cli.data.settings.Settings
import com.example.rom_cli.data.settings.SettingsRepository
import com.example.rom_cli.databinding.FragmentSettingsBinding
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsRepository: SettingsRepository
    private var isBindingUi = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsRepository = SettingsRepository(requireContext())
        bindSettingsToUi(settingsRepository.get())
        setupListeners()
    }

    private fun bindSettingsToUi(settings: Settings) {
        isBindingUi = true
        binding.detectionConfidenceSlider.value = settings.minPoseDetectionConfidence
        binding.trackingConfidenceSlider.value = settings.minPoseTrackingConfidence
        binding.presenceConfidenceSlider.value = settings.minPosePresenceConfidence
        when (settings.currentModel) {
            PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_LITE -> binding.modelLite.isChecked = true
            PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_HEAVY -> binding.modelHeavy.isChecked = true
            else -> binding.modelFull.isChecked = true
        }
        if (settings.facingFront) {
            binding.cameraFacingFront.isChecked = true
        } else {
            binding.cameraFacingBack.isChecked = true
        }
        updateConfidenceLabels(settings)
        isBindingUi = false
    }

    private fun setupListeners() {
        val sliderListener = Slider.OnChangeListener { _, _, _ ->
            if (!isBindingUi) {
                updateConfidenceLabels(readSettingsFromUi())
            }
        }
        binding.detectionConfidenceSlider.addOnChangeListener(sliderListener)
        binding.trackingConfidenceSlider.addOnChangeListener(sliderListener)
        binding.presenceConfidenceSlider.addOnChangeListener(sliderListener)

        binding.settingsSaveButton.setOnClickListener {
            val settings = readSettingsFromUi()
            settingsRepository.save(settings)
            updateConfidenceLabels(settings)
            Snackbar.make(binding.root, R.string.settings_saved, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun readSettingsFromUi(): Settings = Settings(
        minPoseDetectionConfidence = binding.detectionConfidenceSlider.value,
        minPoseTrackingConfidence = binding.trackingConfidenceSlider.value,
        minPosePresenceConfidence = binding.presenceConfidenceSlider.value,
        currentModel = selectedModel(),
        facingFront = binding.cameraFacingFront.isChecked,
    )

    private fun selectedModel(): Int = when (binding.modelRadioGroup.checkedRadioButtonId) {
        R.id.modelLite -> PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_LITE
        R.id.modelHeavy -> PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_HEAVY
        else -> PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_FULL
    }

    private fun updateConfidenceLabels(settings: Settings) {
        binding.detectionConfidenceLabel.text = getString(
            R.string.settings_detection_confidence_label,
            settings.minPoseDetectionConfidence
        )
        binding.trackingConfidenceLabel.text = getString(
            R.string.settings_tracking_confidence_label,
            settings.minPoseTrackingConfidence
        )
        binding.presenceConfidenceLabel.text = getString(
            R.string.settings_presence_confidence_label,
            settings.minPosePresenceConfidence
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
