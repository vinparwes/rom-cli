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
    private var targetPoint: Pair<Float, Float> = Pair(0.33f, 0.5f)

    var primaryPoint: Int? = null
    var secondPoint: Int? = null
    var thirdPoint: Int? = null
    private var basePoint: Pair<Float, Float>? = null
    var rangeOfMotion: Int? = null

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

        primaryPointPaint.color = Color.GREEN
        primaryPointPaint.strokeWidth = LANDMARK_STROKE_WIDTH + 30f
        primaryPointPaint.style = Paint.Style.FILL

        targetPointPaint.color = Color.RED
        targetPointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        targetPointPaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.drawCircle(
            targetPoint.first * imageWidth * scaleFactor,
            targetPoint.second * imageHeight * scaleFactor,
            0.05f * 750,
            targetPointPaint
        )
        results?.let { poseLandmarkerResult ->
            for(landmark in poseLandmarkerResult.landmarks()) {
                val startingPoint = landmark[primaryPoint!!]
                val secondPoint = landmark[secondPoint!!]

                canvas.drawCircle(
                    startingPoint.x() * imageWidth * scaleFactor,
                    startingPoint.y() * imageHeight * scaleFactor,
                    LANDMARK_CIRCLE_RADIUS,
                    primaryPointPaint
                )
                canvas.drawCircle(
                    secondPoint.x() * imageWidth * scaleFactor,
                    secondPoint.y() * imageHeight * scaleFactor,
                    LANDMARK_CIRCLE_RADIUS,
                    pointPaint
                )
                if(basePoint != null) {
                    if(thirdPoint != null) {
                        val optional = landmark[thirdPoint!!]
                        canvas.drawCircle(
                            optional.x() * imageWidth * scaleFactor,
                            optional.y() * imageHeight * scaleFactor,
                            LANDMARK_CIRCLE_RADIUS,
                            primaryPointPaint
                        )
                        rangeOfMotion = getAngle(startingPoint, secondPoint, Pair(optional.x(), optional.y())).toInt()
                    } else {
                        canvas.drawCircle(
                            basePoint!!.first * imageWidth * scaleFactor,
                            basePoint!!.second * imageHeight * scaleFactor,
                            LANDMARK_CIRCLE_RADIUS,
                            primaryPointPaint
                        )
                        rangeOfMotion = getAngle(startingPoint, secondPoint, basePoint!!).toInt()
                    }
                }
            }
        }
    }

    /**
     * Retrieve angle between three points, A
     */
    private fun getAngle(
        A: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        B: com.google.mediapipe.tasks.components.containers.NormalizedLandmark,
        C: Pair<Float, Float>
    ): Float {
        val ABx = B.x() - A.x()
        val ABy = B.y() - A.y()
        val ACx = C.first - A.x()
        val ACy = C.second - A.y()

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
        C: Pair<Float, Float>): Int {
        val value = (B.y() - A.y()) * (C.first - B.x()) - (B.x() - A.x()) * (C.second - B.y())
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
            positionLocked = (inRange(primary.x(), targetPoint.first, 0.06f)
                    && inRange(primary.y(), targetPoint.second, 0.06f))
        }
    }

    private fun inRange(base : Float, comparator: Float, range: Float) : Boolean {
        return abs(base - comparator) <= range / 2
    }

    fun markBasePosition() {
        if(results!!.landmarks().size > 0 && primaryPoint != null) {
            val mark = results!!.landmarks().first()
            val lm = mark[secondPoint!!]
            basePoint = Pair(lm.x(), lm.y())
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
        isPositionLocked()
        invalidate()
    }
    companion object {
        private const val LANDMARK_STROKE_WIDTH = 5F
        private const val LANDMARK_CIRCLE_RADIUS = 0.05f * 375
        var thirdPoint : Int? = null
    }
}