package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.technodot.ftc.twentyfour.maytag.MayTag;
import com.technodot.ftc.twentyfour.robocore.DeviceClaw;
import com.technodot.ftc.twentyfour.robocore.DeviceDrive;
import com.technodot.ftc.twentyfour.robocore.DeviceSlide;
import com.technodot.ftc.twentyfour.robocore.Drivetrain;
import com.technodot.ftc.twentyfour.robocore.Drivetype;

@TeleOp(name="BaboOS", group="TechnoCode")
public class BaboOS extends LinearOpMode {

    public final Drivetrain drivetrain = Drivetrain.TANK;
    public final Drivetype drivetype = Drivetype.ARCADE;

    // controllers
    DeviceDrive deviceDrive;
    DeviceClaw deviceClaw;
    DeviceSlide deviceSlide;
    MayTag mayTag;

    @Override
    public void runOpMode() {
        // initialize devices
        deviceDrive = new DeviceDrive(hardwareMap, drivetrain, drivetype);
        deviceClaw = new DeviceClaw(hardwareMap);
        deviceSlide = new DeviceSlide(hardwareMap);

        // initialize MayTag library
        mayTag = new MayTag();
        mayTag.init(hardwareMap, telemetry);

        // finish initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            // read input
            float leftX = gamepad1.left_stick_x;
            float leftY = gamepad1.left_stick_y;
            float rightX = gamepad1.right_stick_x;
            float rightY = gamepad1.right_stick_y;
            boolean preciseMode = gamepad1.left_trigger > 0.5;
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

            deviceDrive.updatePreciseMode(preciseMode);
            deviceClaw.updatePreciseMode(preciseMode);
            deviceSlide.updatePreciseMode(preciseMode);

            // TODO: update slide calibration
            deviceDrive.updateDrive(leftX, leftY, rightX, rightY);
            deviceClaw.updateArm(armOpenPressed, armClosePressed);
            deviceClaw.updateClaw(clawOpenPressed, clawClosePressed);
            deviceSlide.updateSlide(slideExtendPressed, slideRetractPressed);
            mayTag.updateCamera(cameraVisionPortalStartPressed, cameraVisionPortalStopPressed);

            mayTag.update();
            telemetry.update();
        }

        mayTag.close();

        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}