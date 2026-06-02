package com.example.rom_cli.ui.fragment

import android.Manifest
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.data.FileController
import com.example.rom_cli.data.PermissionsController
import com.example.rom_cli.data.rom_session.AppDatabase
import com.example.rom_cli.data.rom_session.RomSessionResult
import com.example.rom_cli.databinding.FragmentRomHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RomHistoryFragment : Fragment() {

    private var _binding : FragmentRomHistoryBinding? = null
    private val binding get() = _binding!!
    private val args: RomHistoryFragmentArgs by navArgs()
    private var romSession : RomSessionResult? = null

    private var bitmapBefore : Bitmap? = null
    private var bitmapAfter : Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentRomHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PermissionsController.requestPermission(
            requireActivity(),
            PermissionsController.storagePermissionRequestCode,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        )
        binding.romHistoryButtonBack.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.navigateFromRomHistoryToRomStatistics)
        }
        lifecycleScope.launch {
            romSession = withContext(Dispatchers.IO) {
                AppDatabase.get(requireContext().applicationContext)
                    .romSessionDao()
                    .getById(args.fileKey)
            }
            val session = romSession
            if (session == null) {
                Toast.makeText(requireContext(), "Session not found", Toast.LENGTH_LONG).show()
                Navigation.findNavController(binding.root)
                    .navigate(R.id.navigateFromRomHistoryToRomStatistics)
                return@launch
            }
            setupFields()
            setupImages()
            setupButtons()
        }
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
        bitmapBefore = FileController.loadSessionImage(requireContext(), romSession!!.beforeImage)
        bitmapAfter = FileController.loadSessionImage(requireContext(), romSession!!.afterImage)
        if (bitmapBefore == null || bitmapAfter == null) {
            Toast.makeText(requireContext(), "Couldn't load session images", Toast.LENGTH_LONG).show()
        }
        bitmapBefore?.let {
            binding.romHistoryImageBefore.setImageDrawable(BitmapDrawable(resources, it))
        }
        bitmapAfter?.let {
            binding.romHistoryImageAfter.setImageDrawable(BitmapDrawable(resources, it))
        }
    }

    private fun setupButtons() {
        binding.romHistoryButtonDownload.setOnClickListener {
            val before = bitmapBefore
            val after = bitmapAfter
            if (before == null || after == null) {
                Toast.makeText(requireContext(), "Images not available", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(
                FileController.saveImageToGallery(
                    requireContext(),
                    before,
                    args.fileKey + "_before")
                &&
                FileController.saveImageToGallery(
                    requireContext(),
                    after,
                    args.fileKey + "_after",
                ))
            {
                Toast.makeText(requireContext(), "Images saved to local directory", Toast.LENGTH_LONG)
            } else {
                Toast.makeText(requireContext(), "Couldn't save images", Toast.LENGTH_LONG)
            }
        }
        binding.romHistoryButtonDelete.setOnClickListener {
            val session = romSession ?: return@setOnClickListener
            lifecycleScope.launch {
                val deleted = withContext(Dispatchers.IO) {
                    try {
                        FileController.deleteSessionImages(
                            requireContext().applicationContext,
                            session.beforeImage,
                            session.afterImage,
                        )
                        AppDatabase.get(requireContext().applicationContext)
                            .romSessionDao()
                            .delete(session)
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
                if (deleted) {
                    Navigation.findNavController(binding.root)
                        .navigate(R.id.navigateFromRomHistoryToRomStatistics)
                } else {
                    Toast.makeText(requireContext(), "Couldn't delete session", Toast.LENGTH_LONG).show()
                }
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
