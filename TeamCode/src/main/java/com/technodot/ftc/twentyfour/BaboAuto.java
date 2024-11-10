package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.technodot.ftc.twentyfour.maytag.MayTag;

@Autonomous(name="BaboAuto", group="TechnoCode")
public class BaboAuto extends LinearOpMode {
    // motors
    private DcMotor driveLeft;
    private DcMotor driveRight;
    private DcMotor slideMain;
    private DcMotor clawArm;
    private Servo clawMain;

    // variables
    private long slideTime = 0;

    MayTag mayTag = new MayTag();

    private static final float autoSpeed = 0.5F;

    public void runMotor(DcMotor motor, int ticks) {
        motor.setTargetPosition(ticks);
        motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor.setPower(autoSpeed);
    }

    public void resetEncoders() {
        driveLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        driveRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        slideMain.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        clawArm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void drive(int ticksLeft, int ticksRight, float seconds) {
        runMotor(driveLeft, ticksLeft * -1);
        runMotor(driveRight, ticksRight);
        sleep((long) seconds * 1000);
        resetEncoders();
    }

    public void clawOpen() {
        clawMain.setPosition(HardwareProfile.clawOpenPosition);
        sleep(250);
    }

    public void clawClose() {
        clawMain.setPosition(HardwareProfile.clawClosePosition);
        sleep(250);
    }

    public void slideExtend() {
        slideTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < slideTime + HardwareProfile.slideConstant) {
            slideMain.setPower(HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier);
        }
        slideMain.setPower(0.0F);
        sleep(250);
    }

    public void slideRetract() {
        slideTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < slideTime + HardwareProfile.slideConstant) {
            slideMain.setPower(-1.0 * HardwareProfile.slideDirection * HardwareProfile.slideSpeedMultiplier * HardwareProfile.slideRetractMultiplier);
        }
        slideMain.setPower(0.0F);
        sleep(250);
    }

    public void runAuto() {
        // drive(1120, 1120, 3.0F);
        // drive(-4096, 4096, 3.0F);
        // slideExtend();
        // drive(560, 560, 2.0F);
        // clawOpen();
        // sleep(500);
        // clawClose();
        // drive(-560, -560, 2.0F);
        // slideRetract();
        drive(7260, 7260, 7);
        drive(4096, -4096, 5);
        drive(4096, 4096, 5);
    }

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

        resetEncoders();

        // initialize MayTag library
        mayTag.init(hardwareMap, telemetry);

        // finish initialization
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runAuto();

        while (opModeIsActive()) {
            telemetry.addData("driveLeft", driveLeft.getCurrentPosition());
            telemetry.addData("driveRight", driveRight.getCurrentPosition());
            telemetry.addData("slideMain", slideMain.getCurrentPosition());
            telemetry.addData("clawArm", clawArm.getCurrentPosition());
            mayTag.update();
            telemetry.update();
        }

        mayTag.close();

        telemetry.addData("Status", "Stopped");
        telemetry.update();
    }
}