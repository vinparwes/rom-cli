package com.example.rom_cli.data.settings

import com.example.rom_cli.data.PoseLandmarkerHelper

data class Settings(
    val minPoseDetectionConfidence: Float,
    val minPoseTrackingConfidence: Float,
    val minPosePresenceConfidence: Float,
    val currentModel: Int,
) {
    companion object {
        fun defaults() = Settings(
            minPoseDetectionConfidence = PoseLandmarkerHelper.DEFAULT_POSE_DETECTION_CONFIDENCE,
            minPoseTrackingConfidence = PoseLandmarkerHelper.DEFAULT_POSE_TRACKING_CONFIDENCE,
            minPosePresenceConfidence = PoseLandmarkerHelper.DEFAULT_POSE_PRESENCE_CONFIDENCE,
            currentModel = PoseLandmarkerHelper.MODEL_POSE_LANDMARKER_FULL,
        )
    }
}
