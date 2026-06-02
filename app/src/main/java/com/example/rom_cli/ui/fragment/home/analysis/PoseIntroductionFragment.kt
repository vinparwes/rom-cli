package com.example.rom_cli.ui.fragment.home.analysis

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.data.PermissionsController
import com.example.rom_cli.databinding.FragmentPoseIntroductionBinding
import androidx.navigation.findNavController

class PoseIntroductionFragment : Fragment() {

    private val args: PoseIntroductionFragmentArgs by navArgs()
    private var _binding : FragmentPoseIntroductionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPoseIntroductionBinding.inflate(inflater, container, false)
        setupButtons()
        setupFields()
        return binding.root
    }

    private fun setupFields() {
        binding.poseIntroductionHeading.text = args.poseSelection
        when(args.poseSelection) {
            "Abduction" -> binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.abduction))
            "Adduction" -> binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.adduction))
            "External Rotation" -> binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.external_rotation))
            "Forward Flexion" -> binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.forward_flexion))
        }
        when(args.poseSelection) {
            "Abduction" -> binding.poseIntroductionExplanation.text = resources.getText(R.string.instruction_abduction)
            "Adduction" -> binding.poseIntroductionExplanation.text = resources.getText(R.string.instruction_adduction)
            "External Rotation" -> binding.poseIntroductionExplanation.text = resources.getText(R.string.instruction_external_rotation)
            "Forward Flexion" -> binding.poseIntroductionExplanation.text = resources.getText(R.string.instruction_forward_flexion)
        }
    }

    private fun setupButtons() {
        binding.startVision.setOnClickListener {
            if(PermissionsController.checkPermission(requireContext(), Manifest.permission.CAMERA)) {
                val action = PoseIntroductionFragmentDirections.navigateToCamera(args.poseSelection, !binding.leftRadio.isChecked)
                binding.root.findNavController().navigate(action)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionsController.requestPermission(
            requireActivity(),
            PermissionsController.cameraPermissionRequestCode,
            Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionsController.cameraPermissionRequestCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}