package com.technodot.ftc.twentyfour.maytag;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * MayTag class for handling camera and tracking functionalities.
 */
public class MayTag {
    public MayTagDetector detector = new MayTagDetector();
    public MayTagTracker tracker = new MayTagTracker();

    private Telemetry telemetry;

    /**
     * Sets the status of the camera.
     *
     * @param active true to activate the camera, false to deactivate
     */
    public void setCameraStatus(Boolean active) {
        detector.setCameraStatus(active);
    }

    /**
     * Initializes the MayTag system with the given hardware map and telemetry.
     *
     * @param map   the hardware map to initialize the detector
     * @param telem the telemetry to use for logging
     */
    public void init(HardwareMap map, Telemetry telem) {
        detector.initAprilTag(map, telem);
        telemetry = telem;
    }

    /**
     * Updates the robot's position and logs it to telemetry.
     */
    @SuppressLint("DefaultLocale")
    public void update() {
        double[][] data = detector.detectAprilTag();
        tracker.updateRobotPositions(data);

        telemetry.addLine(String.format("Position: (%.2f, %.2f, %.2f)", tracker.x, tracker.y, tracker.h));
    }

    /**
     * Closes the MayTag system and releases resources.
     */
    public void close() {
        detector.closeAprilTag();
    }
}
