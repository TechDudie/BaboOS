package com.technodot.ftc.twentyfour;

import com.technodot.ftc.twentyfour.robocore.Drivetrain;
import com.technodot.ftc.twentyfour.robocore.Drivetype;

public class Configuration {
    // drive
    public static final Drivetrain driveTrainType = Drivetrain.TANK;
    public static final Drivetype driveControlType = Drivetype.ARCADE;

    public static final float driveLeftMultiplier = 1.0F;
    public static final float driveRightMultiplier = 1.0F;
    public static final int driveDirection = -1;
    public static final int driveTurnDirection = 1;
    public static final float drivePreciseModeMultiplier = 0.5F;

    public static final float clawOpenPosition = 1.0F;
    public static final float clawClosePosition = -1.0F;

    public static final int armClosePosition = 0;
    public static final int armOpenPosition = -75;
    public static final float armSpeed = 0.4F;
    public static final float armIdleSpeed = 0.1F;
    public static final float armPreciseModeMultiplier = 0.5F;

    public static final int slideConstant = 3000;
    public static final int slideDirection = 1;
    public static final float slideSpeedMultiplier = 1.0F;
    public static final float slideRetractMultiplier = 0.10F;

    public static final float robotCameraAngleOffset = 180.0F;
    public static final float robotCameraCenterOffset = 0.4F;
}
