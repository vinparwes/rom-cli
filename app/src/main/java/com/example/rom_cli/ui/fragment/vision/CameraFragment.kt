package com.example.rom_cli.ui.fragment.vision

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.rom_cli.R
import com.example.rom_cli.data.PoseLandmarkerHelper
import com.example.rom_cli.databinding.FragmentCameraBinding
import com.google.mediapipe.tasks.vision.core.RunningMode
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
    private var LEFT_WRIST_POINT: Int = 15
    private var RIGHT_WRIST_POINT: Int = 16

    private var runningFlag: Boolean = false

    private var currentImage : Bitmap? = null
    private var beforeImage : Bitmap? = null
    private var rotation : Int? = null
    private var beforeRotation : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        binding.startStopButton.setOnClickListener { toggleRecording() }
        binding.startStopButton.isClickable = false
        binding.switchCameraButton.setOnClickListener{ switchCamera() }
        return binding.root
    }

    private fun switchCamera() : Int {
        if(cameraFacing == CameraSelector.LENS_FACING_FRONT) {
            cameraFacing = CameraSelector.LENS_FACING_BACK
            setUpCamera()
            return 1
        } else if(cameraFacing == CameraSelector.LENS_FACING_BACK){
            cameraFacing = CameraSelector.LENS_FACING_FRONT
            setUpCamera()
            return 0
        }
        return 42
    }

    private fun toggleRecording() {
        if (!runningFlag) {
            binding.startStopButton.text = resources.getString(R.string.stop)
            runningFlag = true
            binding.overlay.markBasePosition()

            if(currentImage != null) {
                beforeImage = currentImage!!
            }
            if(rotation != null) {
                beforeRotation = rotation
            }
        } else if(runningFlag) {
            runningFlag = false
            if(currentImage != null) {
                val afterImage = currentImage!!
                val afterRotation : Int = rotation!!
                navigateToROMScreen(afterImage, afterRotation)
            }
        }
    }

    private fun navigateToROMScreen(afterImage: Bitmap, afterRotation: Int) {
        val action = CameraFragmentDirections.navigateToRomResults(
            beforeImage!!,
            afterImage,
            binding.overlay.rangeOfMotion.toString(),
            beforeRotation!!,
            afterRotation,
            args.poseName,
            cameraFacing == CameraSelector.LENS_FACING_FRONT,
            args.leftJoint)
        Navigation.findNavController(binding.root).navigate(action)
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
        Log.i("INFO", "SETTING leftHandRecording TO: ${!args.leftJoint}")
        binding.overlay.leftHandRecording = !args.leftJoint
        Log.i("INFO", "OVERLAY leftHandRecording IS: ${binding.overlay.leftHandRecording}")
        if(args.leftJoint) {
            when(poseName) {
                "Abduction" -> {
                    overlayView.originPoint = LEFT_SHOULDER_POINT
                    overlayView.secondPoint = LEFT_ELBOW_POINT
                }
                "Adduction" -> {
                    overlayView.originPoint = LEFT_SHOULDER_POINT
                    overlayView.secondPoint = LEFT_WRIST_POINT
                }
                "External Rotation" -> {
                    overlayView.originPoint = LEFT_ELBOW_POINT
                    overlayView.secondPoint = LEFT_WRIST_POINT
                    overlayView.externalRotation = true
                }
                "Forward Flexion" -> {
                    overlayView.originPoint = LEFT_SHOULDER_POINT
                    overlayView.secondPoint = LEFT_WRIST_POINT
                    overlayView.thirdPoint = RIGHT_WRIST_POINT
                }
            }
        } else {
            when(poseName) {
                "Abduction" -> {
                    overlayView.originPoint = RIGHT_SHOULDER_POINT
                    overlayView.secondPoint = RIGHT_ELBOW_POINT
                }
                "Adduction" -> {
                    overlayView.originPoint = RIGHT_SHOULDER_POINT
                    overlayView.secondPoint = RIGHT_WRIST_POINT
                }
                "External Rotation" -> {
                    overlayView.originPoint = RIGHT_ELBOW_POINT
                    overlayView.secondPoint = RIGHT_WRIST_POINT
                    overlayView.externalRotation = true
                }
                "Forward Flexion" -> {
                    overlayView.originPoint = RIGHT_SHOULDER_POINT
                    overlayView.secondPoint = RIGHT_WRIST_POINT
                    overlayView.thirdPoint = LEFT_WRIST_POINT
                }
            }
        }
    }

    private fun setUpCamera() {
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                cameraProvider = cameraProviderFuture.get()
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
                        image ->
                        saveImageFrame(image.toBitmap(), image.imageInfo.rotationDegrees)
                        detectPose(image)
                        image.close()
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

    @OptIn(ExperimentalGetImage::class) private fun saveImageFrame(
        frame: Bitmap,
        rotationDegrees: Int
    ) {
        currentImage = frame
        rotation = rotationDegrees
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
        val instructionText = if (binding.overlay.positionLocked!!) {
            resources.getString(R.string.instruction_two)
        } else {
            resources.getString(R.string.instruction_one)
        }
        activity?.runOnUiThread {
            if (binding != null) {
                binding.overlay.setResults(
                    resultBundle.results.first(),
                    resultBundle.inputImageHeight,
                    resultBundle.inputImageWidth,
                    RunningMode.LIVE_STREAM
                )
                if(!runningFlag) {
                    binding.instructionView.text = instructionText
                    if(binding.overlay.positionLocked!!) {
                        binding.cameraLayout.setBackgroundColor(Color.YELLOW)
                        binding.startStopButton.isClickable = true
                    } else {
                        binding.cameraLayout.setBackgroundColor(Color.RED)
                        binding.startStopButton.isClickable = false
                    }
                } else {
                    binding.cameraLayout.setBackgroundColor(Color.GREEN)
                    if(binding.overlay.rangeOfMotion != null)
                        binding.instructionView.text = binding.overlay.rangeOfMotion.toString()
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