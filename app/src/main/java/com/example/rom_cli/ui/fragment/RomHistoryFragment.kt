package com.example.rom_cli.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.data.FileController
import com.example.rom_cli.data.PermissionsController
import com.example.rom_cli.data.RomSessionResult
import com.example.rom_cli.data.Utility
import com.example.rom_cli.databinding.FragmentRomHistoryBinding


class RomHistoryFragment : Fragment() {

    private var _binding : FragmentRomHistoryBinding? = null
    private val binding get() = _binding!!
    private val args: RomHistoryFragmentArgs by navArgs()
    private var romSession : RomSessionResult? = null

    private var bitmapBefore : Bitmap? = null
    private var bitmapAfter : Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRomHistoryBinding.inflate(inflater, container, false)
        romSession = FileController.getRomResult(requireContext(), args.fileKey)
        setupFields()
        setupImages()
        setupButtons()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionsController.requestPermission(
            requireActivity(),
            PermissionsController.storagePermissionRequestCode,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
    }

    private fun setupFields() {
        binding.romHistoryTextHeader.text = romSession!!.poseIdentifier
        binding.romHistoryTextDate.text = romSession!!.dateRecorded
        val value = romSession!!.recordedROM.toString()
        binding.romHistoryTextRom.text = "$value degrees"
    }

    private fun setupImages() {
        when(romSession!!.poseIdentifier) {
            "Abduction" -> binding.romHistoryImageExample.setImageDrawable(resources.getDrawable(R.drawable.abduction))
            "Adduction" -> binding.romHistoryImageExample.setImageDrawable(resources.getDrawable(R.drawable.adduction))
            "External Rotation" -> binding.romHistoryImageExample.setImageDrawable(resources.getDrawable(R.drawable.external_rotation))
            "Forward Flexion" -> binding.romHistoryImageExample.setImageDrawable(resources.getDrawable(R.drawable.forward_flexion))
        }
        bitmapBefore = Utility.byteArrayToBitmap(romSession!!.beforeImage)
        bitmapAfter = Utility.byteArrayToBitmap(romSession!!.afterImage)
        binding.romHistoryImageBefore.setImageDrawable(BitmapDrawable(resources, bitmapBefore))
        binding.romHistoryImageAfter.setImageDrawable(BitmapDrawable(resources, bitmapAfter))
    }

    private fun setupButtons() {
        binding.romHistoryButtonDownload.setOnClickListener {
            if(
                FileController.saveImageToGallery(
                    requireContext(),
                    bitmapBefore!!,
                    args.fileKey + "_before")
                &&
                FileController.saveImageToGallery(
                    requireContext(),
                    bitmapAfter!!,
                    args.fileKey + "_after",
                ))
            {
                Toast.makeText(requireContext(), "Images saved to local directory", Toast.LENGTH_LONG)
            } else {
                Toast.makeText(requireContext(), "Couldn't save images", Toast.LENGTH_LONG)
            }
        }
        binding.romHistoryButtonDelete.setOnClickListener {
            FileController.delete(requireContext(), args.fileKey)
            Navigation.findNavController(binding.root).navigate(R.id.navigateFromRomHistoryToRomStatistics)
        }
        binding.romHistoryButtonBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.navigateFromRomHistoryToRomStatistics)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i("PERMISSION RESULT", permissions.toString())
        when (requestCode) {
            PermissionsController.storagePermissionRequestCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permission Granted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}