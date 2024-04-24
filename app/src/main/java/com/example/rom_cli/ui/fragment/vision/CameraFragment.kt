package com.example.rom_cli.ui.fragment.vision

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.data.PoseLandmarkerHelper
import com.example.rom_cli.databinding.FragmentCameraBinding
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.lang.IllegalStateException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment(), PoseLandmarkerHelper.LandmarkerListener {

    private var tag = "Pose Landmarker"
    private var _binding : FragmentCameraBinding? = null

    private val args: CameraFragmentArgs by navArgs()
    private val binding get() = _binding!!
    private lateinit var backgroundExecutor: ExecutorService
    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer : ImageAnalysis? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraFacing = CameraSelector.LENS_FACING_FRONT

    private var LEFT_SHOULDER_POINT: Int = 11
    private var RIGHT_SHOULDER_POINT: Int = 12
    private var LEFT_ELBOW_POINT: Int = 13
    private var RIGHT_ELBOW_POINT: Int = 14
    private var LEFT_HAND_BASE_POINT: Int = 15
    private var RIGHT_HAND_BASE_POINT: Int = 16
    private var LEFT_WAIST_POINT: Int = 23
    private var RIGHT_WAIST_POINT: Int = 24
    private var LEFT_WRIST_POINT: Int = 15
    private var RIGHT_WRIST_POINT: Int = 16

    private var runningFlag: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        binding.startStopButton.setOnClickListener { toggleRecording() }
        binding.startStopButton.isClickable = false
        //binding.overlay.targetPoint = Point(0.33, 0.5)
        return binding.root
    }

    private fun calculateScreenCenter() : Point {
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val centerX = screenWidth / 2
        val centerY = screenHeight / 2
        return Point(centerX, centerY)
    }

    private fun toggleRecording() {
        if (!runningFlag) {
            binding.startStopButton.text = resources.getString(R.string.stop)
            runningFlag = true
        } else if(runningFlag) {
            binding.startStopButton.text = resources.getString(R.string.start)
            runningFlag = false
        }
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runningFlag = false
        val overlayView = view.findViewById<OverlayView>(R.id.overlay)
        assignPoseMarkings(overlayView, args.poseName)
        backgroundExecutor = Executors.newSingleThreadExecutor()
        binding.viewFinder.post {
            setUpCamera()
        }
        //TODO Hard-coded values?
        backgroundExecutor.execute {
            poseLandmarkerHelper = PoseLandmarkerHelper(
                context = requireContext(),
                runningMode = RunningMode.LIVE_STREAM,
                minPoseDetectionConfidence = 0.5f,
                minPoseTrackingConfidence = 0.5f,
                minPosePresenceConfidence = 0.5f,
                currentDelegate = 0,
                poseLandmarkerHelperListener = this
            )
        }
    }

    private fun assignPoseMarkings(overlayView: OverlayView, poseName: String) {
        if(args.leftJoint) {
            when(poseName) {
                "Abduction" -> {
                    overlayView!!.primaryPoint = LEFT_SHOULDER_POINT
                    overlayView!!.secondPoint = LEFT_ELBOW_POINT
                    overlayView!!.basePoint = LEFT_WAIST_POINT
                }
                "Adduction" -> {
                    overlayView!!.primaryPoint = LEFT_SHOULDER_POINT
                    overlayView!!.secondPoint = LEFT_WRIST_POINT
                }
                "External Rotation" -> {
                    overlayView!!.primaryPoint = LEFT_ELBOW_POINT
                    overlayView!!.secondPoint = LEFT_WRIST_POINT
                }
                "Forward Flexion" -> {
                    overlayView!!.primaryPoint = LEFT_ELBOW_POINT
                    overlayView!!.secondPoint = LEFT_WRIST_POINT
                }
            }
        } else {
            when(poseName) {
                "Abduction" -> {
                    overlayView!!.primaryPoint = RIGHT_SHOULDER_POINT
                    overlayView!!.secondPoint = RIGHT_ELBOW_POINT
                    overlayView!!.basePoint = RIGHT_WAIST_POINT
                }
                "Adduction" -> {
                    overlayView!!.primaryPoint = RIGHT_SHOULDER_POINT
                    overlayView!!.secondPoint = RIGHT_WRIST_POINT
                }
                "External Rotation" -> {
                    overlayView!!.primaryPoint = RIGHT_ELBOW_POINT
                    overlayView!!.secondPoint = RIGHT_WRIST_POINT
                }
                "Forward Flexion" -> {
                    overlayView!!.primaryPoint = RIGHT_HAND_BASE_POINT
                    overlayView!!.secondPoint = RIGHT_ELBOW_POINT
                }
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            }, ContextCompat.getMainExecutor(requireContext())
        )
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: throw IllegalStateException("Initialization Failed")
        val cameraSelector = CameraSelector.Builder().requireLensFacing(cameraFacing).build()

        preview = Preview.Builder()
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetRotation(binding.viewFinder.display.rotation)
            .build()

        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetRotation(binding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                .also {
                    it.setAnalyzer(backgroundExecutor) {
                        image -> detectPose(image)
                    }
                }
        cameraProvider.unbindAll()
        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer
            )

            preview?.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(tag, "Use case binding failed", exc)
        }
    }

    private fun detectPose(imageProxy: ImageProxy) {
        if(this::poseLandmarkerHelper.isInitialized) {
            poseLandmarkerHelper.detectLiveStream(
                imageProxy = imageProxy,
                isFrontCamera = cameraFacing == CameraSelector.LENS_FACING_FRONT
            )
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation =
            binding.viewFinder.display.rotation
        setUpCamera()
    }

    override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        activity?.runOnUiThread {
            if (binding != null) {
                binding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )
                if(binding.overlay.positionLocked!!) {
                    binding.cameraLayout.setBackgroundColor(Color.YELLOW)
                    binding.instructionView.text = resources.getString(R.string.instruction_two)
                    binding.startStopButton.isClickable = true
                } else {
                    binding.instructionView.text = resources.getString(R.string.instruction_one)
                    binding.cameraLayout.setBackgroundColor(Color.RED)
                    binding.startStopButton.isClickable = false
                }

                binding.overlay.invalidate()
            }
        }
    }

    override fun onError(error: String, errorCode: Int) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            if (errorCode == PoseLandmarkerHelper.GPU_ERROR) {

            }
        }
    }
}