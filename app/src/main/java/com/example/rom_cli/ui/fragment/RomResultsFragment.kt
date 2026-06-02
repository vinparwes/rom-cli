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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.data.FileController
import com.example.rom_cli.data.rom_session.AppDatabase
import com.example.rom_cli.data.rom_session.RomSessionResult
import com.example.rom_cli.databinding.FragmentRomResultsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import androidx.navigation.findNavController
import androidx.core.graphics.drawable.toDrawable

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
            val before = bitmapBefore
            val after = bitmapAfter
            if (before == null || after == null) {
                Toast.makeText(context, "Images not ready", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            binding.romResultsSaveButton.isEnabled = false
            lifecycleScope.launch {
                val success = withContext(Dispatchers.IO) {
                    saveSession(before, after)
                }
                binding.romResultsSaveButton.isEnabled = true
                if (success) {
                    val action = RomResultsFragmentDirections.navigateFromRomResultsToHomeFragment(true)
                    binding.root.findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "Something went wrong..", Toast.LENGTH_LONG).show()
                }
            }
        }
        binding.romResultsBackButton.setOnClickListener {
            val action = RomResultsFragmentDirections.navigateFromRomResultsToCameraFragment(args.poseSelection, args.leftHandSide)
            binding.root.findNavController().navigate(action)
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
        binding.beforeImageView.setImageDrawable(bitmapBefore?.toDrawable(resources))
        binding.afterImageView.setImageDrawable(bitmapAfter?.toDrawable(resources))
        binding.romResultsHeading.text = "${args.poseSelection} Results"
        binding.romResultText.text = args.finalRom
    }

    private suspend fun saveSession(before: Bitmap, after: Bitmap): Boolean {
        val context = requireContext().applicationContext
        val dao = AppDatabase.get(context).romSessionDao()
        val uid = UUID.randomUUID().toString()
        val dateFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val beforePath = FileController.saveSessionImage(context, before, uid, "before")
        val afterPath = FileController.saveSessionImage(context, after, uid, "after")
        if (beforePath == null || afterPath == null) {
            beforePath?.let { File(context.filesDir, it).delete() }
            afterPath?.let { File(context.filesDir, it).delete() }
            return false
        }
        return try {
            dao.insert(
                RomSessionResult(
                    uid = uid,
                    dateRecorded = dateFormatted,
                    beforeImage = beforePath,
                    afterImage = afterPath,
                    recordedROM = args.finalRom.toInt(),
                    poseIdentifier = args.poseSelection,
                )
            )
            true
        } catch (e: Exception) {
            FileController.deleteSessionImages(context, beforePath, afterPath)
            false
        }
    }
}