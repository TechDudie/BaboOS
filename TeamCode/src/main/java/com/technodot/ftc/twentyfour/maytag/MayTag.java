package com.technodot.ftc.twentyfour.maytag;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

public class MayTag {
    public MayTagDetector detector = new MayTagDetector();
    public MayTagTracker tracker = new MayTagTracker();

    private Telemetry telemetry;

    public void setCameraStatus(Boolean active) {
        detector.setCameraStatus(active);
    }

    public void init(HardwareMap map, Telemetry telem) {
        detector.initAprilTag(map, telem);
        telemetry = telem;
    }

    @SuppressLint("DefaultLocale")
    public void update() {
        double[][] data = detector.detectAprilTag();
        tracker.updateRobotPositions(data);

        telemetry.addLine(String.format("Position: (%.2f, %.2f)", tracker.x, tracker.y));
    }

    public void close() {
        detector.closeAprilTag();
    }
}
