package com.example.rom_cli.ui.fragment.vision

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import kotlin.math.max
import kotlin.math.min

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: PoseLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    private var LEFT_SHOULDER_POINT: Int = 11
    private var RIGHT_SHOULDER_POINT: Int = 12

    private var LEFT_ELBOW_POINT: Int = 13
    private var RIGHT_ELBOW_POINT: Int = 14

    private var LEFT_HAND_BASE_POINT: Int = 15
    private var RIGHT_HAND_BASE_POINT: Int = 16

    init {
        //initPaints()
    }

    fun clear() {
        results = null
        pointPaint.reset()
        linePaint.reset()
        invalidate()
        //initPaints()
    }
    /*
    private fun initPaints() {
        linePaint.color =
            ContextCompat.getColor(context!!, R.color.mp_color_primary)
        linePaint.strokeWidth = LANDMARK_STROKE_WIDTH
        linePaint.style = Paint.Style.STROKE

        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = LANDMARK_STROKE_WIDTH
        pointPaint.style = Paint.Style.FILL
    }

     */

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 75f
            textAlign = Paint.Align.CENTER
        }
        results?.let { poseLandmarkerResult ->
            for(landmark in poseLandmarkerResult.landmarks()) {
                /*
                for(normalizedLandmark in landmark) {
                    canvas.drawPoint(
                        normalizedLandmark.x() * imageWidth * scaleFactor,
                        normalizedLandmark.y() * imageHeight * scaleFactor,
                        pointPaint
                    )
                }

                 */
                val leftShoulderPoint = landmark[LEFT_SHOULDER_POINT]
                val leftHandPoint = landmark[LEFT_HAND_BASE_POINT]
                val rightShoulderPoint = landmark[RIGHT_SHOULDER_POINT];
                val rightHandPoint = landmark[RIGHT_HAND_BASE_POINT];


                /*
                val distance = sqrt(
                    (leftShoulderPoint.x() - leftHandPoint.x()).toDouble().pow(2.0)
                    + (leftShoulderPoint.y() - leftHandPoint.y()).toDouble().pow(2.0)
                )
                canvas.drawText(
                    distance.toString().subSequence(0, 4).toString(),
                    500f,
                    500f,
                    paint
                )
                 */
                canvas.drawText(
                    leftShoulderPoint.z().toString(),
                    leftShoulderPoint.x() * imageWidth * scaleFactor,
                    leftShoulderPoint.y() * imageHeight * scaleFactor,
                    paint
                )
                canvas.drawText(
                    leftHandPoint.z().toString(),
                    leftHandPoint.x() * imageWidth * scaleFactor,
                    leftHandPoint.y() * imageHeight * scaleFactor,
                    paint
                )
                canvas.drawText(
                    rightShoulderPoint.z().toString(),
                    rightShoulderPoint.x() * imageWidth * scaleFactor,
                    rightShoulderPoint.y() * imageHeight * scaleFactor,
                    paint
                )
                canvas.drawText(
                    rightHandPoint.z().toString(),
                    rightHandPoint.x() * imageWidth * scaleFactor,
                    rightHandPoint.y() * imageHeight * scaleFactor,
                    paint
                )
                /*
                PoseLandmarker.POSE_LANDMARKS.forEach {
                    canvas.drawLine(
                        poseLandmarkerResult.landmarks().get(0).get(it!!.start()).x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.start()).y() * imageHeight * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.end()).x() * imageWidth * scaleFactor,
                        poseLandmarkerResult.landmarks().get(0).get(it.end()).y() * imageHeight * scaleFactor,
                        linePaint)
                }

                 */
            }
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