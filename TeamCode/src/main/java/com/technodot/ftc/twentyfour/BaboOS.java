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
    private DcMotor clawArm;
    private Servo clawMain;

    // variables
    private int slideStatus = 0;
    private long slideTime = 0;
    private long slideTimer = 0;
    private int armStatus = 0;

    // constants
    // values for Core Hex Motor
    public static final double ticksPerMotorRevolution = 288;
    public static final double gearReduction = 2.7778;
    public static final double ticksPerRevolution = ticksPerMotorRevolution * gearReduction;
    public static final double ticksPerDegree = ticksPerRevolution / 360;

    // controllers
    MayTag mayTag = new MayTag();

    private int calculateMotorPosition(float degrees) {
        return (int) (ticksPerDegree * Math.toRadians(degrees));
    }

    /**
     * Updates the drive motors with controller input
     *
     * @param accelX        left joystick X
     * @param accelY        left joystick Y
     * @param directionX    right joystick X
     * @param directionY    right joystick Y
     */
    private void updateDrive(float accelX, float accelY, float directionX, float directionY) {
        float[] driveData = DriveTrain.calculateDrive(accelX, accelY, directionX, directionY);

        driveLeft.setPower(driveData[0]);
        driveRight.setPower(driveData[1]);
    }

    private void updateDrivePrecise(float accelX, float accelY, float directionX, float directionY) {
        float[] driveData = DriveTrain.calculateDrive(accelX, accelY, directionX, directionY);

        driveLeft.setPower(driveData[0] * HardwareProfile.drivePreciseModeMultiplier);
        driveRight.setPower(driveData[1] * HardwareProfile.drivePreciseModeMultiplier);
    }

    /**
     * Updates the claw servo with controller input
     *
     * @param clawOpenPressed   button A pressed
     * @param clawClosePressed  button B pressed
     */
    private void updateClaw(boolean clawOpenPressed, boolean clawClosePressed) {
        if (clawOpenPressed) {
            clawMain.setPosition(HardwareProfile.clawOpenPosition);
        } else if (clawClosePressed) {
            clawMain.setPosition(HardwareProfile.clawClosePosition);
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

        if (slideExtendPressed) { // extend slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime < HardwareProfile.slideConstant) { // slide can move
                    slideMain.setPower(HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier);
                    slideTimer = currentTime;
                    slideStatus = 1;
                }
            } else { // slide is moving
                if (currentTime >= slideTimer - slideTime + HardwareProfile.slideConstant) { // slide can't move
                    slideMain.setPower(0.0);
                    slideTime = HardwareProfile.slideConstant;
                    slideStatus = 0;
                    slideTimer = 0;
                } else if (slideStatus != 0) { // slide is moving
                    slideMain.setPower(HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier);
                }
            }
        } else if (slideRetractPressed) { // retract slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime > 0) { // slide can move
                    slideMain.setPower(-1.0 * HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier * HardwareProfile.slideRetractMultiplier);
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
                    slideMain.setPower(-1.0 * HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier * HardwareProfile.slideRetractMultiplier);
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

    private void updateSlideCalibration(boolean slideExtendForcePressed, boolean slideRetractForcePressed) {
        if (slideExtendForcePressed) {
            slideMain.setPower(HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier);
        } else if (slideRetractForcePressed) {
            slideMain.setPower(-1.0 * HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier * HardwareProfile.slideRetractMultiplier);
        }
    }

    private void updateArm(boolean armOpenPressed, boolean armClosePressed) {
        int targetPosition;

        if (armOpenPressed) {
            targetPosition = HardwareProfile.armOpenPosition;
            armStatus = 1;
        } else if (armClosePressed) {
            targetPosition = HardwareProfile.armClosePosition;
            armStatus = -2;
        } else {
            clawArm.setPower(0.3F);
            return;
        }

        clawArm.setTargetPosition(targetPosition);
        clawArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        clawArm.setPower(HardwareProfile.armSpeed * armStatus);
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
        clawArm = hardwareMap.get(DcMotor.class, "clawArm");
        clawMain = hardwareMap.get(Servo.class, "clawMain");
        slideMain.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        clawArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // initialize MayTag library
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
            float drivePrecisionMode = gamepad1.left_trigger;
            boolean clawOpenPressed = gamepad1.a;
            boolean clawClosePressed = gamepad1.b;
            boolean armOpenPressed = gamepad1.x;
            boolean armClosePressed = gamepad1.y;
            boolean slideExtendPressed = gamepad1.left_bumper;
            boolean slideRetractPressed = gamepad1.right_bumper;
            boolean slideExtendForcePressed = gamepad1.dpad_up;
            boolean slideRetractForcePressed = gamepad1.dpad_down;
            boolean cameraVisionPortalStartPressed = gamepad1.dpad_left;
            boolean cameraVisionPortalStopPressed = gamepad1.dpad_right;

            // update devices
            if (drivePrecisionMode == 0) updateDrive(accelX, accelY, directionX, directionY);
            else updateDrivePrecise(accelX, accelY, directionX, directionY);
            updateClaw(clawOpenPressed, clawClosePressed);
            updateSlide(slideExtendPressed, slideRetractPressed);
            updateSlideCalibration(slideExtendForcePressed, slideRetractForcePressed);
            updateArm(armOpenPressed, armClosePressed);
            updateCamera(cameraVisionPortalStartPressed, cameraVisionPortalStopPressed);

            // update telemetry
            telemetry.addData("Status", "Running");
            telemetry.addData("slidePosition", slideMain.getCurrentPosition());
            telemetry.addData("armPosition", clawArm.getCurrentPosition());

            mayTag.update();
            telemetry.update();
        }

        mayTag.close();

        if (armStatus == 1) {
            clawArm.setTargetPosition(HardwareProfile.armClosePosition);
            clawArm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            clawArm.setPower(HardwareProfile.armSpeed);
        }

        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}