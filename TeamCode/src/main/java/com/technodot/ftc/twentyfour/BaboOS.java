package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

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

    // constants
    public static final float headingConstant = 0.636619772386F;

    /**
     * Updates the drive motors with controller input
     *
     * @param accelX        left joystick X
     * @param accelY        left joystick Y
     * @param directionX    right joystick X
     * @param directionY    right joystick Y
     */
    private void updateDrive(float accelX, float accelY, float directionX, float directionY) {
        float powerLeft = accelY * RobotConstants.driveLeftMultiplier;
        float powerRight = accelY * RobotConstants.driveRightMultiplier;

        if (directionX != 0) {
            float heading = (float) (Math.atan(directionX / directionY) * headingConstant);
            if (directionX < 0) {
                heading *= -1;
            }
            if (directionY < 0) {
                heading += 2;
            }
            heading -= 1;
            if (RobotConstants.driveTurnDirection > 0) {
                if (directionX < 0) {
                    powerLeft *= -heading;
                } else if (directionX > 0) {
                    powerRight *= heading;
                }
            } else if (RobotConstants.driveTurnDirection < 0) {
                if (directionX < 0) {
                    powerRight *= -heading;
                } else if (directionX > 0) {
                    powerLeft *= heading;
                }
            }
        }

        driveLeft.setPower(powerLeft);
        driveRight.setPower(powerRight);
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
    private void updateSlide(boolean slideExtendPressed, boolean slideRetractPressed) {
        long currentTime = System.currentTimeMillis();

        // TODO: buggy, period

        if (slideExtendPressed) { // extend slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime <= RobotConstants.slideConstant) { // slide can move
                    slideMain.setPower(RobotConstants.slideDirection * RobotConstants.slideSpeedMultiplier);
                    slideTimer = currentTime;
                    slideStatus = 1;
                }
            } else { // slide is moving
                if (currentTime >= slideTimer - slideTime + RobotConstants.slideConstant) { // slide can't move
                    slideMain.setPower(0.0);
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
                    slideMain.setPower(0.0);
                    slideTime = 0;
                    slideStatus = 0;
                    slideTimer = 0;
                } else if (slideStatus != 0) { // slide is moving
                    slideMain.setPower(-1.0 * RobotConstants.slideDirection * RobotConstants.slideSpeedMultiplier * RobotConstants.slideRetractMultiplier);
                }
            }
        } else { // neither buttons pressed
            slideMain.setPower(0.0);
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

    /**
     * TeleOp main code
     */
    @Override
    public void runOpMode() {
        // initialize devices
        driveLeft = hardwareMap.get(DcMotor.class, "driveLeft");
        driveRight = hardwareMap.get(DcMotor.class, "driveRight");
        slideMain = hardwareMap.get(DcMotor.class, "slideMain");
        clawMain = hardwareMap.get(Servo.class, "clawMain");

        // initialize telemetry
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
            boolean slideExtendForcePressed = gamepad1.dpad_up;
            boolean slideRetractForcePressed = gamepad1.dpad_down;
            boolean slideRetractedRecalibratePressed = gamepad1.dpad_left;
            boolean slideExtendedRecalibratePressed = gamepad1.dpad_right;

            // update devices
            updateDrive(accelX, accelY, directionX, directionY);
            updateClaw(clawOpenPressed, clawClosePressed);
            updateSlide(slideExtendPressed, slideRetractPressed);

            // update telemetry
            telemetry.addData("Status", "Running");
            telemetry.addData("slideStatus", slideStatus);
            telemetry.addData("slideTime", slideTime);
            telemetry.addData("slideTimer", slideTimer);
            telemetry.update();
        }
    }
}