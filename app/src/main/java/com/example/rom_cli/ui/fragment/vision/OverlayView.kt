package com.example.rom_cli.ui.fragment.vision

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.lang.Math.toDegrees
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()
    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    var primaryPoint: Int? = null
    var secondPoint: Int? = null
    var thirdPoint: Int? = null

    init {
        initPaints()
    }

    fun clear() {
        results = null
        pointPaint.reset()
        linePaint.reset()
        invalidate()
        initPaints()
    }

    private fun initPaints() {
        linePaint.color = Color.RED
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE
        pointPaint.color = Color.RED
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH + 30f
        pointPaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 75f
            textAlign = Paint.Align.CENTER
        }
        results?.let { poseLandmarkerResult ->
            for(landmark in poseLandmarkerResult.landmarks()) {
                val startingPoint = landmark[primaryPoint!!]
                val secondPoint = landmark[secondPoint!!]
                val thirdPoint = landmark[thirdPoint!!]
                val arr = arrayOf(
                    startingPoint,
                    secondPoint,
                    thirdPoint
                )
                for(normalizedLandmark in arr) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }
                val angle = getAngle(startingPoint, secondPoint, thirdPoint).toInt()
                canvas.drawText(
                    "Angle: $angle",
                    (imageWidth / 2).toFloat(),
                    500f,
                    paint
                )
            }
        }
    }

    /**
     * Retrieve angle between three points, A
     */
    private fun getAngle(
        A: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        B: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        C: com.google.mediapipe.tasks.components.containers.NormalizedLandmark
    ): Float {
        val ABx = B.x() - A.x()
        val ABy = B.y() - A.y()
        val ACx = C.x() - A.x()
        val ACy = C.y() - A.y()

        val dotProduct = ABx * ACx + ABy * ACy
        val magnitudeAB = sqrt(ABx * ABx + ABy * ABy)
        val magnitudeAC = sqrt(ACx * ACx + ACy * ACy)
        val cosineAngle = dotProduct / (magnitudeAB * magnitudeAC)
        var angle = acos(cosineAngle)

        angle = toDegrees(angle.toDouble()).toFloat()

        val orientation = orientation(A, B, C)
        if (orientation == -1) {
            angle = 360 - angle
        }
        return angle
    }

    // Calculate orientation using cross product of vectors AB and AC
    private fun orientation(
        A: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        B: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        C: com.google.mediapipe.tasks.components.containers.NormalizedLandmark): Int {
        val value = (B.y() - A.y()) * (C.x() - B.x()) - (B.x() - A.x()) * (C.y() - B.y())
        return when {
            value.toInt() == 0 -> 0  // Collinear
            value > 0 -> 1   // Clockwise orientation
            else -> -1       // Counterclockwise orientation
        }
    }

    fun setResults(
        poseLandmarkerResults: PoseLandmarkerResult,
        imageHeight: Int,
        imageWidth: Int,
        runningMode: RunningMode = RunningMode.IMAGE
    ) {
        results = poseLandmarkerResults

        this.imageHeight = imageHeight
        this.imageWidth = imageWidth

        scaleFactor = when (runningMode) {
            RunningMode.IMAGE,
            RunningMode.VIDEO -> {
                min(width * 1f / imageWidth, height * 1f / imageHeight)
            }
            RunningMode.LIVE_STREAM -> {
                // PreviewView is in FILL_START mode. So we need to scale up the
                // landmarks to match with the size that the captured images will be
                // displayed.
                max(width * 1f / imageWidth, height * 1f / imageHeight)
            }
        }
        invalidate()
    }
    companion object {
        private const val LANDMARK_STROKE_WIDTH = 12F
    }
}