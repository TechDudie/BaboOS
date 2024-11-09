package com.technodot.ftc.twentyfour;

/**
 * This class contains hardware profile constants for the robot.
 */
public class HardwareProfile {
    /*
    0: Default
    1: LT joystick controls power, RT joystick controls heading
    2: Each joystick controls their respective motor
    3: LT joystick control everything (ARRI AUTISTIC TM)
     */
    public static final int driveControlVersion = 2;

    public static final float driveLeftMultiplier = -1.0F;
    public static final float driveRightMultiplier = 1.0F;
    public static final int driveTurnDirection = 1;
    public static final float drivePreciseModeMultiplier = 0.5F;

    public static final float clawOpenPosition = 1.0F;
    public static final float clawClosePosition = -1.0F;

    public static final int slideConstant = 3000;
    public static final int slideDirection = 1;
    public static final float slideSpeedMultiplier = 1.0F;
    public static final float slideRetractMultiplier = 0.10F;

    public static final int armClosePosition = 0;
    public static final int armOpenPosition = -75;
    public static final float armSpeed = 0.4F;

    public static final float robotCameraOffset = 180.0F;
}
