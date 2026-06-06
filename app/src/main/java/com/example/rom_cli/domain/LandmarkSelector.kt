package com.example.rom_cli.domain

enum class LandmarkSelector(selection: Int) {
    LEFT_SHOULDER_POINT(11),
    RIGHT_SHOULDER_POINT(12),

    LEFT_ELBOW_POINT(13),
    RIGHT_ELBOW_POINT(14),

    LEFT_HAND_BASE_POINT(15),
    RIGHT_HAND_BASE_POINT(16),

    LEFT_WAIST_POINT(23),
    RIGHT_WAIST_POINT(24),

    LEFT_WRIST_POINT(15),
    RIGHT_WRIST_POINT(16)
}