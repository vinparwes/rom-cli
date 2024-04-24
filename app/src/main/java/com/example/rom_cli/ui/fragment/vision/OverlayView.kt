package com.example.rom_cli.ui.fragment.vision

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.lang.Math.toDegrees
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var primaryPointPaint = Paint()
    private var pointPaint = Paint()
    private var linePaint = Paint()
    private var targetPointPaint = Paint()
    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    var primaryPoint: Int? = null
    var secondPoint: Int? = null
    var basePoint: Int? = 1

    private var targetPoint: Pair<Float, Float> = Pair(0.33f, 0.5f)

    var positionLocked: Boolean? = null

    init {
        positionLocked = false
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
        linePaint.color = Color.WHITE
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.WHITE
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH + 30f
        pointPaint.style = Paint.Style.FILL

        primaryPointPaint.color = Color.RED
        primaryPointPaint.strokeWidth = LANDMARK_STROKE_WIDTH + 30f
        primaryPointPaint.style = Paint.Style.FILL

        targetPointPaint.color = Color.GREEN
        targetPointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        targetPointPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawCircle(
            targetPoint!!.first * imageWidth * scaleFactor,
            targetPoint!!.second * imageHeight * scaleFactor,
            0.05f * 750,
            targetPointPaint
        )
        results?.let { poseLandmarkerResult ->
            for(landmark in poseLandmarkerResult.landmarks()) {
                val startingPoint = landmark[primaryPoint!!]
                val secondPoint = landmark[secondPoint!!]
                val thirdPoint = landmark[basePoint!!]

                canvas.drawCircle(
                    startingPoint.x() * imageWidth * scaleFactor,
                    startingPoint.y() * imageHeight * scaleFactor,
                    0.05f * 375,
                    primaryPointPaint
                )
                canvas.drawCircle(
                    secondPoint.x() * imageWidth * scaleFactor,
                    secondPoint.y() * imageHeight * scaleFactor,
                    0.05f * 375,
                    pointPaint
                )
                canvas.drawCircle(
                    thirdPoint.x() * imageWidth * scaleFactor,
                    thirdPoint.y() * imageHeight * scaleFactor,
                    0.05f * 375,
                    pointPaint
                )
                /*
                val angle = getAngle(startingPoint, secondPoint, thirdPoint).toInt()
                canvas.drawText(
                    "Angle: $angle",
                    (imageWidth / 2).toFloat(),
                    500f,
                    paint
                )

                 */
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

    private fun isPositionLocked() {
        if(results!!.landmarks().size > 0) {
            val landmark = results!!.landmarks().first()
            val primary = landmark[primaryPoint!!]

            if(inRange(primary.x(), targetPoint!!.first, 0.03f)
                && inRange(primary.y(), targetPoint!!.second, 0.03f)) {
                Log.i("INFO", "IN RANGE")
                positionLocked = true
            } else {
                positionLocked = false
            }
        }
    }

    private fun inRange(base : Float, comparator: Float, range: Float) : Boolean {
        return abs(base - comparator) <= range / 2
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
        isPositionLocked()
        invalidate()
    }
    companion object {
        private const val LANDMARK_STROKE_WIDTH = 5F
    }
}