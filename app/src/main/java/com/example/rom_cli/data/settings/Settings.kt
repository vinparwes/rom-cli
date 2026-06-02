package com.example.rom_cli.data.settings

data class Settings(
    val minPoseDetectionConfidence: Float,
    val minPoseTrackingConfidence: Float,
    val minPosePresenceConfidence: Float,
    val currentModel: Int,
)

suspend fun get(): PoseSettings
suspend fun update(block: (PoseSettings) -> PoseSettings)
