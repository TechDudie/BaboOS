package com.technodot.ftc.twentyfour;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class MaxyMotor {
    public static DcMotor driveLeft;
    public static DcMotor driveRight;
    public static DcMotor slideMain;
    public static Servo clawMain;

    public static void initDevices(HardwareMap hardwareMap) {
        MaxyMotor.driveLeft = hardwareMap.get(DcMotor.class, "driveLeft");
        MaxyMotor.driveRight = hardwareMap.get(DcMotor.class, "driveRight");
        MaxyMotor.slideMain = hardwareMap.get(DcMotor.class, "slideMain");
        MaxyMotor.clawMain = hardwareMap.get(Servo.class, "clawMain");
    }
}
