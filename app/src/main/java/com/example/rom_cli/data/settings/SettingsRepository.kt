package com.example.rom_cli.data.settings

import android.content.Context
import androidx.core.content.edit

class SettingsRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun get(): Settings {
        val defaults = Settings.defaults()
        return Settings(
            minPoseDetectionConfidence = prefs.getFloat(
                KEY_MIN_POSE_DETECTION_CONFIDENCE,
                defaults.minPoseDetectionConfidence
            ),
            minPoseTrackingConfidence = prefs.getFloat(
                KEY_MIN_POSE_TRACKING_CONFIDENCE,
                defaults.minPoseTrackingConfidence
            ),
            minPosePresenceConfidence = prefs.getFloat(
                KEY_MIN_POSE_PRESENCE_CONFIDENCE,
                defaults.minPosePresenceConfidence
            ),
            currentModel = prefs.getInt(KEY_CURRENT_MODEL, defaults.currentModel),
        )
    }

    fun save(settings: Settings) {
        prefs.edit {
            putFloat(KEY_MIN_POSE_DETECTION_CONFIDENCE, settings.minPoseDetectionConfidence)
                .putFloat(KEY_MIN_POSE_TRACKING_CONFIDENCE, settings.minPoseTrackingConfidence)
                .putFloat(KEY_MIN_POSE_PRESENCE_CONFIDENCE, settings.minPosePresenceConfidence)
                .putInt(KEY_CURRENT_MODEL, settings.currentModel)
        }
    }

    companion object {
        private const val PREFS_NAME = "rom_settings"
        private const val KEY_MIN_POSE_DETECTION_CONFIDENCE = "min_pose_detection_confidence"
        private const val KEY_MIN_POSE_TRACKING_CONFIDENCE = "min_pose_tracking_confidence"
        private const val KEY_MIN_POSE_PRESENCE_CONFIDENCE = "min_pose_presence_confidence"
        private const val KEY_CURRENT_MODEL = "current_model"
    }
}
