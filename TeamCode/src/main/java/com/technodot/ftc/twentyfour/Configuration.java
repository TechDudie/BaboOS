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
}
