package com.technodot.ftc.twentyfour.maytag;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MayTagDetector {
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
    public double[][] detectAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());
        int validDetectionCount = 0;

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format("ID %d %s RYB %6.1f %6.1f %6.1f", detection.id, detection.metadata.name, detection.ftcPose.range, detection.ftcPose.yaw, detection.ftcPose.bearing));
                validDetectionCount += 1;
            }
        }

        double[][] tagDataArray = new double[currentDetections.size()][4];
        for (int i = 0; i < currentDetections.size(); i++) {
            AprilTagDetection detection = currentDetections.get(i);
            tagDataArray[i][0] = detection.id;
            tagDataArray[i][1] = detection.ftcPose.range;
            tagDataArray[i][2] = detection.ftcPose.yaw;
            tagDataArray[i][3] = detection.ftcPose.bearing;
        }
        return tagDataArray;
    }

    public void closeAprilTag() {
        visionPortal.close();
    }
}
