package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="BaboOS", group="TechnoCode")
public class BaboOS extends LinearOpMode {
    // motors
    public static DcMotor driveLeft;
    public static DcMotor driveRight;
    public static DcMotor slideMain;
    public static Servo clawMain;

    // constants
    public static final float headingConstant = 0.636619772386F;
    public static final int slideConstant = 3000;
    public static final float delta = 0.00001F;

    // subclasses
    private final AkbarDrive controllerDrive = new AkbarDrive();
    private final ChrisClaw controllerClaw = new ChrisClaw();

    // helper function to check if a variable is within a certain delta of a target
    private static boolean isWithinDelta(double variable, double target) {
        return Math.abs(variable - target) <= delta;
    }

    @Override
    public void runOpMode() {
        // initialize devices
        driveLeft = hardwareMap.get(DcMotor.class, "driveLeft");
        driveRight = hardwareMap.get(DcMotor.class, "driveRight");
        slideMain = hardwareMap.get(DcMotor.class, "slideMain");
        clawMain = hardwareMap.get(Servo.class, "clawMain");

        // tell operator that robot has initialized
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // wait for start
        waitForStart();

        // enter main loop
        while (opModeIsActive()) {
            // read gamepad input and set initial motor power
            float accelX = gamepad1.left_stick_x;
            float accelY = gamepad1.left_stick_y;
            float directionX = gamepad1.right_stick_x;
            float directionY = gamepad1.right_stick_y;
            boolean clawPressed = gamepad1.a;
            boolean buttonB = gamepad1.b;
            boolean buttonX = gamepad1.x;
            boolean buttonY = gamepad1.y;
            boolean slideExtendPressed = gamepad1.right_bumper;
            boolean slideRetractPressed = gamepad1.left_bumper;

            telemetry.addData("left_stick_x", "%.4f", accelX);
            telemetry.addData("left_stick_y", "%.4f", accelY);
            telemetry.addData("right_stick_x", "%.4f", directionX);
            telemetry.addData("right_stick_y", "%.4f", directionY);

            controllerDrive.calculateDriveSpeed(accelX, accelY, directionX, directionY);
            controllerDrive.updateDriveSpeed();

            controllerClaw.updateClawStatus(clawPressed);
            controllerClaw.updateSlideStatus(slideExtendPressed, slideRetractPressed);

            // update telemetry
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
