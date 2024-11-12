package com.technodot.ftc.twentyfour.robocore;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technodot.ftc.twentyfour.Configuration;

public class DeviceDrive {
    public DcMotor frontLeft;
    public DcMotor frontRight;
    public DcMotor backLeft;
    public DcMotor backRight;

    public Drivetrain drivetrain;
    public Drivetype drivetype;

    public float speedMultiplier = 1.0F;

    public DeviceDrive(HardwareMap hardwareMap, Drivetrain drivetrainType, Drivetype drivetypeType) {
        drivetrain = drivetrainType;
        drivetype = drivetypeType;

        switch (drivetrain) {
            case TANK:
                frontLeft = hardwareMap.get(DcMotor.class, "driveLeft");
                frontRight = hardwareMap.get(DcMotor.class, "driveRight");
                break;
            case MECANUM:
            case FOURWHEEL:
                frontLeft = hardwareMap.get(DcMotor.class, "driveFrontLeft");
                frontRight = hardwareMap.get(DcMotor.class, "driveFrontRight");
                backLeft = hardwareMap.get(DcMotor.class, "driveBackLeft");
                backRight = hardwareMap.get(DcMotor.class, "driveBackRight");
                break;
        }
    }

    public void updatePreciseMode(boolean preciseMode) {
        if (preciseMode) {
            speedMultiplier = Configuration.drivePreciseModeMultiplier;
        } else {
            speedMultiplier = 1.0F;
        }
    }

    public void updateDrive(float leftX, float leftY, float rightX, float rightY) {
        float powerFrontLeft = 0.0F;
        float powerFrontRight = 0.0F;
        float powerBackLeft = 0.0F;
        float powerBackRight = 0.0F;

        float calculateLeft = 0.0F;
        float calculateRight = 0.0F;

        switch (drivetype) {
            case TANK:
                if (Configuration.driveTurnDirection == 1) {
                    calculateLeft = leftY;
                    calculateRight = rightY;
                } else if (Configuration.driveTurnDirection == -1) {
                    calculateLeft = rightY;
                    calculateRight = leftY;
                }
                break;
            case ARCADE:
                leftX *= Configuration.driveTurnDirection;

                float maximum = Math.max(Math.abs(leftX), Math.abs(leftY));
                float total = leftX + leftY;
                float difference = leftY - leftX;
                boolean drive = leftY >= 0;
                boolean rotate = leftX >= 0;

                switch ((drive ? 2 : 0) + (rotate ? 1 : 0)) {
                    case 0: // quadrant II
                        calculateLeft = total;
                        calculateRight = maximum;
                        break;
                    case 1: // quadrant I
                        calculateLeft = maximum;
                        calculateRight = difference;
                        break;
                    case 2: // quadrant III
                        calculateLeft = -maximum;
                        calculateRight = difference;
                        break;
                    case 3: // quadrant IIV
                        calculateLeft = total;
                        calculateRight = -maximum;
                        break;
                }
                break;
            case TECHNODRIVE_V1:
                float heading = (float) (Math.atan(rightX / rightY) * 0.636619772386F);
                if (rightX < 0) {
                    heading *= -1;
                }
                if (rightY < 0) {
                    heading += 2;
                }
                heading -= 1;
                if (Configuration.driveTurnDirection == 1) {
                    if (rightX < 0) {
                        calculateLeft *= heading;
                    } else if (rightX > 0) {
                        calculateRight *= -heading;
                    }
                } else if (Configuration.driveTurnDirection == -1) {
                    if (rightX < 0) {
                        calculateRight *= heading;
                    } else if (rightX > 0) {
                        calculateLeft *= -heading;
                    }
                }
                break;
        }

        switch (drivetrain) {
            case TANK:
                // TODO: figure out issue with the negative
                // powerFrontLeft = -calculateLeft;
                powerFrontLeft = calculateLeft;
                powerFrontRight = calculateRight;
            case MECANUM:
                // TODO: get those idiots to build a mecanum chassis *that works*
                break;
            case FOURWHEEL:
                powerFrontLeft = calculateLeft;
                powerBackLeft = calculateLeft;
                powerFrontRight = calculateRight;
                powerBackRight = calculateRight;
                break;
        }

        float totalLeft = speedMultiplier * Configuration.driveDirection * Configuration.driveLeftMultiplier;
        float totalRight = speedMultiplier * Configuration.driveDirection * Configuration.driveRightMultiplier;

        powerFrontLeft *= totalLeft;
        powerBackLeft *= totalLeft;
        powerFrontRight *= totalRight;
        powerBackRight *= totalRight;

        frontLeft.setPower(powerFrontLeft);
        frontRight.setPower(powerFrontRight);
        if (drivetrain != Drivetrain.TANK) {
            backLeft.setPower(powerBackLeft);
            backRight.setPower(powerBackRight);
        }
    }
}
