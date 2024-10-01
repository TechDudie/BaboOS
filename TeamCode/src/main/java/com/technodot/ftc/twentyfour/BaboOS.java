package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="BaboOS", group="TechnoCode")
public class BaboOS extends LinearOpMode {
    // constants
    public static final float headingConstant = 0.636619772386F;
    public static final int slideConstant = 3000;
    public static final float delta = 0.00001F;

    // controllers
    private final AkbarDrive controllerDrive = new AkbarDrive();
    private final ChrisClaw controllerClaw = new ChrisClaw();
    private final MaxyMotor controllerMotor = new MaxyMotor();

    private static boolean isWithinDelta(double variable, double target) {
        return Math.abs(variable - target) <= delta;
    }

    @Override
    public void runOpMode() {
        controllerMotor.initDevices(hardwareMap);
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
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

            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
