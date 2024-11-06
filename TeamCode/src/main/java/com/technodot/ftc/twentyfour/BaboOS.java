package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.technodot.ftc.twentyfour.maytag.MayTag;

@TeleOp(name="BaboOS", group="TechnoCode")
public class BaboOS extends LinearOpMode {
    // motors
    private DcMotor driveLeft;
    private DcMotor driveRight;
    private DcMotor slideMain;
    private Servo clawMain;

    // variables
    private int slideStatus = 0;
    private long slideTime = 0;
    private long slideTimer = 0;

    // AprilTag controller
    MayTag mayTag = new MayTag();

    /**
     * Updates the drive motors with controller input
     *
     * @param accelX        left joystick X
     * @param accelY        left joystick Y
     * @param directionX    right joystick X
     * @param directionY    right joystick Y
     */
    @SuppressWarnings("UnusedParameters")
    private void updateDrive(float accelX, float accelY, float directionX, float directionY) {
        float[] driveData = DriveTrain.calculateDrive(accelX, accelY, directionX, directionY);

        driveLeft.setPower(driveData[0]);
        driveRight.setPower(driveData[1]);
    }

    /**
     * Updates the claw servo with controller input
     *
     * @param clawOpenPressed   button A pressed
     * @param clawClosePressed  button B pressed
     */
    private void updateClaw(boolean clawOpenPressed, boolean clawClosePressed) {
        if (clawOpenPressed) {
            clawMain.setPosition(RobotConstants.clawOpenPosition);
        } else if (clawClosePressed) {
            clawMain.setPosition(RobotConstants.clawClosePosition);
        }
    }

    /**
     * Updates the slide motor with controller input
     *
     * @param slideExtendPressed    left bumper pressed
     * @param slideRetractPressed   right bumper pressed
     */
    @SuppressWarnings("ConstantConditions")
    private void updateSlide(boolean slideExtendPressed, boolean slideRetractPressed) {
        long currentTime = System.currentTimeMillis();

        // TODO: buggy, period

        if (slideExtendPressed) { // extend slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime < RobotConstants.slideConstant) { // slide can move
                    slideMain.setPower(RobotConstants.slideDirection * RobotConstants.slideSpeedMultiplier);
                    slideTimer = currentTime;
                    slideStatus = 1;
                }
            } else { // slide is moving
                if (currentTime >= slideTimer - slideTime + RobotConstants.slideConstant) { // slide can't move
                    slideMain.setPower(RobotConstants.slideIdleSpeed);
                    slideTime = RobotConstants.slideConstant;
                    slideStatus = 0;
                    slideTimer = 0;
                } else if (slideStatus != 0) { // slide is moving
                    slideMain.setPower(RobotConstants.slideDirection * RobotConstants.slideSpeedMultiplier);
                }
            }
        } else if (slideRetractPressed) { // retract slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime > 0) { // slide can move
                    slideMain.setPower(-1.0 * RobotConstants.slideDirection * RobotConstants.slideSpeedMultiplier * RobotConstants.slideRetractMultiplier);
                    slideTimer = currentTime;
                    slideStatus = -1;
                }
            } else { // slide is moving
                if (currentTime >= slideTimer + slideTime) { // slide can't move
                    slideMain.setPower(RobotConstants.slideIdleSpeed);
                    slideTime = 0;
                    slideStatus = 0;
                    slideTimer = 0;
                } else if (slideStatus != 0) { // slide is moving
                    slideMain.setPower(-1.0 * RobotConstants.slideDirection * RobotConstants.slideSpeedMultiplier * RobotConstants.slideRetractMultiplier);
                }
            }
        } else { // neither buttons pressed
            slideMain.setPower(RobotConstants.slideIdleSpeed);
            if (slideStatus == 1) {
                slideTime += currentTime - slideTimer;
                slideStatus = 0;
                slideTimer = 0;
            } else if (slideStatus == -1) {
                slideTime -= currentTime - slideTimer;
                slideStatus = 0;
                slideTimer = 0;
            }
        }
    }

    private void updateCamera(boolean cameraVisionPortalStartPressed, boolean cameraVisionPortalStopPressed) {
        if (cameraVisionPortalStartPressed) {
            mayTag.setCameraStatus(true);
        } else if (cameraVisionPortalStopPressed) {
            mayTag.setCameraStatus(false);
        }
    }

    /**
     * TeleOp main code
     */
    @SuppressWarnings("UnusedDeclaration")
    @Override
    public void runOpMode() {
        // initialize devices
        driveLeft = hardwareMap.get(DcMotor.class, "driveLeft");
        driveRight = hardwareMap.get(DcMotor.class, "driveRight");
        slideMain = hardwareMap.get(DcMotor.class, "slideMain");
        clawMain = hardwareMap.get(Servo.class, "clawMain");

        // initialize AprilTag
        mayTag.init(hardwareMap, telemetry);

        // finish initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            // read input
            float accelX = gamepad1.left_stick_x;
            float accelY = gamepad1.left_stick_y;
            float directionX = gamepad1.right_stick_x;
            float directionY = gamepad1.right_stick_y;
            boolean clawOpenPressed = gamepad1.a;
            boolean clawClosePressed = gamepad1.b;
            boolean slideExtendPressed = gamepad1.left_bumper;
            boolean slideRetractPressed = gamepad1.right_bumper;
            boolean slideRetractForcePressed = gamepad1.dpad_up;
            boolean slideRetractedRecalibratePressed = gamepad1.dpad_down;
            boolean cameraVisionPortalStartPressed = gamepad1.dpad_left;
            boolean cameraVisionPortalStopPressed = gamepad1.dpad_right;

            // update devices
            updateDrive(accelX, accelY, directionX, directionY);
            updateClaw(clawOpenPressed, clawClosePressed);
            updateSlide(slideExtendPressed, slideRetractPressed);
            updateCamera(cameraVisionPortalStartPressed, cameraVisionPortalStopPressed);

            // update telemetry
            telemetry.addData("Status", "Running");
            telemetry.addData("slideStatus", slideStatus);
            telemetry.addData("slideTime", slideTime);
            telemetry.addData("slideTimer", slideTimer);

            mayTag.update();
            telemetry.update();
        }

        mayTag.close();

        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}