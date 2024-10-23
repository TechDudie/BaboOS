package com.technodot.ftc.twentyfour;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class AprilTag {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private WebcamName cameraFront;

    public void setCameraStatus(Boolean active) {
        if (!active) {
            visionPortal.stopStreaming();
        } else {
            visionPortal.resumeStreaming();
        }
    }

    public void initAprilTag(HardwareMap map, Telemetry telem) {
        hardwareMap = map;
        telemetry = telem;

        // initialize AprilTag processor
        cameraFront = hardwareMap.get(WebcamName.class, "cameraFront");
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true) // draw axis on tag
                .setDrawCubeProjection(true) // draw cube projection on tag
                .setDrawTagOutline(true) // draw outline of tag
                .build();

        /*
        Adjust Image Decimation to trade-off detection-range for detection-rate.
        eg: Some typical detection data using a Logitech C920 WebCam
        Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second (default)
        Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second (default)
        Note: Decimation can be changed on-the-fly to adapt during a match.
        */

        // aprilTag.setDecimation(3);

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(cameraFront);
        builder.enableLiveView(true); // enable live view of camera

        builder.addProcessor(aprilTag);
        visionPortal = builder.build();
        visionPortal.setProcessorEnabled(aprilTag, true); // disable or re-enable the aprilTag processor
    }

    @SuppressLint("DefaultLocale")
    public void detectAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }
        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }

    public void closeAprilTag() {
        visionPortal.close();
    }
}
