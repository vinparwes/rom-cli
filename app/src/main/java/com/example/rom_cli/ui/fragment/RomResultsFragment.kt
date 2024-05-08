package com.example.rom_cli.ui.fragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.data.FileController
import com.example.rom_cli.data.RomSessionResult
import com.example.rom_cli.data.Utility
import com.example.rom_cli.databinding.FragmentRomResultsBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RomResultsFragment : Fragment() {

    private var _binding : FragmentRomResultsBinding? = null
    private val args: RomResultsFragmentArgs by navArgs()
    private val binding get() = _binding!!

    private var bitmapBefore : Bitmap? = null
    private var bitmapAfter : Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRomResultsBinding.inflate(inflater, container, false)
        setupFields()
        setupButtons()
        return binding.root
    }

    private fun setupButtons() {
        binding.romResultsSaveButton.setOnClickListener {
            val dateFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val obj = RomSessionResult(
                dateFormatted,
                Utility.bitmapToByteArray(bitmapBefore!!),
                Utility.bitmapToByteArray(bitmapAfter!!),
                args.finalRom.toInt(),
                args.poseSelection
            )
            val fileName = dateFormatted + "_${args.poseSelection}"
            if(FileController.putObject(obj, requireContext(), fileName)) {
                val action = RomResultsFragmentDirections.navigateFromRomResultsToHomeFragment(true)
                Navigation.findNavController(binding.root).navigate(action)
            } else {
                Toast.makeText(context, "Something went wrong..", Toast.LENGTH_LONG).show()
            }
        }
        binding.romResultsBackButton.setOnClickListener {
            val action = RomResultsFragmentDirections.navigateFromRomResultsToCameraFragment(args.poseSelection, args.leftHandSide)
            Navigation.findNavController(binding.root).navigate(action)
        }
    }

    private fun setupFields() {
        val beforeBitmap = args.beforeImageBitMap
        val beforeRotation = args.beforeRotation
        val afterBitmap = args.afterImageBitMap
        val afterRotation = args.afterRotation
        val beforeMatrix = Matrix().apply {
            postRotate(beforeRotation.toFloat())
            if (args.recordedByFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    beforeBitmap.width.toFloat(),
                    beforeBitmap.height.toFloat()
                )
            }
        }
        val afterMatrix = Matrix().apply {
            postRotate(afterRotation.toFloat())
            if (args.recordedByFrontCamera) {
                postScale(
                    -1f,
                    1f,
                    afterBitmap.width.toFloat(),
                    afterBitmap.height.toFloat()
                )
            }
        }

        val rotatedBeforeBitmap = Bitmap.createBitmap(
            beforeBitmap, 0, 0, beforeBitmap.width, beforeBitmap.height,
            beforeMatrix, true
        )
        val rotatedAfterBitmap = Bitmap.createBitmap(
            afterBitmap, 0, 0, afterBitmap.width, afterBitmap.height,
            afterMatrix, true
        )
        bitmapBefore = rotatedBeforeBitmap
        bitmapAfter = rotatedAfterBitmap
        binding.beforeImageView.setImageDrawable(BitmapDrawable(resources, bitmapBefore))
        binding.afterImageView.setImageDrawable(BitmapDrawable(resources, bitmapAfter))
        binding.romResultsHeading.text = "${args.poseSelection} Results"
        binding.romResultText.text = args.finalRom
    }
}