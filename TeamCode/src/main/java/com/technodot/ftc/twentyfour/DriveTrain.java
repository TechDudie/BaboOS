package com.technodot.ftc.twentyfour;

/**
 * This class represents the drive train of the robot.
 */
public class DriveTrain {
    public static final float headingConstant = 0.636619772386F;

    /**
     * Calculates the drive speed from controller input
     *
     * @param accelX        left joystick X
     * @param accelY        left joystick Y
     * @param directionX    right joystick X
     * @param directionY    right joystick Y
     */
    public static float[] calculateDrive(float accelX, float accelY, float directionX, float directionY) {
        float powerLeft;
        float powerRight;
        switch (HardwareProfile.driveControlVersion) {
            case 1:
                powerLeft = accelY * HardwareProfile.driveLeftMultiplier;
                powerRight = accelY * HardwareProfile.driveRightMultiplier;

                if (directionX != 0) {
                    float heading = (float) (Math.atan(directionX / directionY) * headingConstant);
                    if (directionX < 0) {
                        heading *= -1;
                    }
                    if (directionY < 0) {
                        heading += 2;
                    }
                    heading -= 1;
                    if (HardwareProfile.driveTurnDirection > 0) {
                        if (directionX < 0) {
                            powerLeft *= heading;
                        } else if (directionX > 0) {
                            powerRight *= -heading;
                        }
                    } else if (HardwareProfile.driveTurnDirection < 0) {
                        if (directionX < 0) {
                            powerRight *= heading;
                        } else if (directionX > 0) {
                            powerLeft *= -heading;
                        }
                    }
                }

                return new float[]{powerLeft, powerRight};
            case 2:
                return new float[]{accelY, -directionY};
            case 3:
                // float baseSpeed = (float) Math.sqrt(Math.pow(accelX, 2) + Math.pow(accelY, 2))
                float baseSpeed = accelY;
                powerLeft = baseSpeed * HardwareProfile.driveLeftMultiplier;
                powerRight = baseSpeed * HardwareProfile.driveRightMultiplier;

                if (accelX != 0) {
                    float horizontal = (float) Math.asin(accelX) * headingConstant * -2.0F;
                    if (accelX < 0) {
                        powerLeft *= -horizontal + 1;
                    } else if (accelX > 0) {
                        powerRight *= horizontal + 1;
                    }

                }

                return new float[]{powerLeft, powerRight};
            default:
                return new float[]{accelY, -directionY};
        }
    }
}
