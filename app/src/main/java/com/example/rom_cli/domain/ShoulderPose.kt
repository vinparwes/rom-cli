package com.example.rom_cli.domain

enum class ShoulderPose(primary: LandmarkSelector, secondary: LandmarkSelector) {

    LeftAbduction(LandmarkSelector.LEFT_SHOULDER_POINT, LandmarkSelector.LEFT_ELBOW_POINT),
    LeftAdduction(LandmarkSelector.LEFT_SHOULDER_POINT, LandmarkSelector.LEFT_WRIST_POINT),
    LeftExternalRotation(LandmarkSelector.LEFT_ELBOW_POINT, LandmarkSelector.LEFT_WRIST_POINT),
    LeftForwardFlexion(LandmarkSelector.LEFT_ELBOW_POINT, LandmarkSelector.LEFT_WRIST_POINT),

    RightAbduction(LandmarkSelector.RIGHT_SHOULDER_POINT, LandmarkSelector.RIGHT_ELBOW_POINT),
    RightAdduction(LandmarkSelector.RIGHT_SHOULDER_POINT, LandmarkSelector.RIGHT_WRIST_POINT),
    RightExternalRotation(LandmarkSelector.RIGHT_ELBOW_POINT, LandmarkSelector.RIGHT_WRIST_POINT),
    RightForwardFlexion(LandmarkSelector.RIGHT_ELBOW_POINT, LandmarkSelector.RIGHT_WRIST_POINT)

}