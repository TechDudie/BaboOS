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
    private boolean clawOpen = false;
//    private int slideStatus = 0;
    private boolean slideStatus = false;
    private long slideTime = 0;
    private long slideTimer = -1;

    // constants
    public static final float headingConstant = 0.636619772386F;
    public static final int slideExtendConstant = 2000;
    public static final int slideRetractConstant = 1800;
    public static final float delta = 0.00001F;

    // helper function to check if a variable is within a certain delta of a target
    private static boolean isWithinDelta(double variable, double target) {
        return Math.abs(variable - target) <= delta;
    }

    @Override
    public void runOpMode() {
        // TODO: organize code into classes
        // TODO: implement AprilTag detection in ArriTag class

        // initialize devices
        driveLeft = hardwareMap.get(DcMotor.class, "driveLeft");
        driveRight = hardwareMap.get(DcMotor.class, "driveRight");
        slideMain = hardwareMap.get(DcMotor.class, "slideMain");
        clawMain = hardwareMap.get(Servo.class, "clawMain");

        // tell operator that robot has intialized
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // wait for start
        waitForStart();

        // enter main loop
        while (opModeIsActive()) {
            // execute stuff here/

            // read gamepad input and set initial motor power
            // TODO: add configurable controls with gamepad switching
            float accel = gamepad1.left_stick_y;
            float directionX = gamepad1.right_stick_x;
            float directionY = gamepad1.right_stick_y;
            boolean clawPressed = gamepad1.a;
            boolean slidePressed = gamepad1.b;
            boolean slideExtendPressed = gamepad1.x;
            boolean slideRetractPressed = gamepad1.y;

            telemetry.addData("left_stick_y", "%.4f accel", accel);
            telemetry.addData("right_stick_x", "%.4f directionX", directionX);
            telemetry.addData("right_stick_y", "%.4f directionY", directionY);

            float powerLeft = accel;
            float powerRight = -accel;

            // calculate turning power
            if (directionX != 0) {
                // calculate heading as a value between 1 and -1
                float heading = (float) (Math.atan(directionX / directionY) * headingConstant);
                if (directionX < 0) {
                    powerLeft *= -heading;
                } else if (directionX > 0) {
                    powerRight *= heading;
                }
            }

            // set motor power
            driveLeft.setPower(powerLeft);
            driveRight.setPower(powerRight);

            // open or close claw
            if (clawPressed) {
                if (clawOpen) {
                    clawMain.setPosition(0.0);
                } else {
                    clawMain.setPosition(0.4);
                }
                clawOpen = !clawOpen;
            }

            if (slidePressed) {
                if (slideTime == -1) {
                    slideTime = System.currentTimeMillis();
                    slideStatus = !slideStatus;
                }
            }
            if (slideTime != -1) {
                if (System.currentTimeMillis() >= slideTime + (slideStatus ? slideExtendConstant : slideRetractConstant)) {
                    slideMain.setPower(0);
                    slideTime = -1;
                } else {
                    slideMain.setPower(slideStatus ? -1.0 : 1.0);
                }
            }

//            // extend or retract slide
//
//            // ok this code is messed up as hell so more comments here
//            long currentTime = System.currentTimeMillis(); // get current time
//
//            if (slideExtendPressed) { // extend slide button pressed
//                if (slideStatus == 0) { // slide not moving
//                    if (slideTime >= slideConstant) { // slide can move
//                        slideMain.setPower(-1.0);
//                        slideTimer = currentTime;
//                        slideStatus = 1;
//                    }
//                } else { // slide is moving
//                    if (currentTime >= slideTimer - slideTime + slideConstant) { // slide can't move
//                        slideMain.setPower(0.0);
//                        slideTime = slideConstant;
//                        slideStatus = 0;
//                        slideTimer = 0;
//                    } else if (slideStatus != 0) { // slide is moving
//                        slideMain.setPower(-1.0);
//                    }
//                }
//            } else if (slideRetractPressed) { // retract slide button pressed
//                if (slideStatus == 0) { // slide not moving
//                    if (slideTime >= slideConstant) { // slide can move
//                        slideMain.setPower(1.0);
//                        slideTimer = currentTime;
//                        slideStatus = -1;
//                    }
//                } else { // slide is moving
//                    if (currentTime >= slideTimer + slideTime) { // slide can't move
//                        slideMain.setPower(0.0);
//                        slideTime = 0;
//                        slideStatus = 0;
//                        slideTimer = 0;
//                    } else if (slideStatus != 0) { // slide is moving
//                        slideMain.setPower(1.0);
//                    }
//                }
//            } else { // neither buttons pressed
//                slideMain.setPower(0.0);
//                if (slideStatus == 1) {
//                    slideTime += currentTime - slideTimer;
//                    slideStatus = 0;
//                    slideTimer = 0;
//                } else if (slideStatus == -1) {
//                    slideTime -= currentTime - slideTimer;
//                    slideStatus = 0;
//                    slideTimer = 0;
//                }
//            }

            // update telemetry
            telemetry.addData("Status", "Running");
            telemetry.update();
        }
    }
}
