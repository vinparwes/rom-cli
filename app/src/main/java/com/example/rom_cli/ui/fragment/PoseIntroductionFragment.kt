package com.example.rom_cli.ui.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.databinding.FragmentPoseIntroductionBinding

class PoseIntroductionFragment : Fragment() {

    private val PERMISSION_REQUEST_CODE = 200

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
    }

    private fun setupButtons() {
        binding.startVision.setOnClickListener {
            if(getPermission()) {
                val action = PoseIntroductionFragmentDirections.navigateToCamera(args.poseSelection, !binding.leftRadio.isChecked)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
    }

    private fun getPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}