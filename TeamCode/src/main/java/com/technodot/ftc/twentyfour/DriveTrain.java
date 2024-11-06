package com.technodot.ftc.twentyfour;

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
        return new float[]{-directionY, accelY};
    }

    /*
    public static float[] calculateDrive(float accelX, float accelY, float directionX, float directionY) {
        float powerLeft = accelY * RobotConstants.driveLeftMultiplier;
        float powerRight = accelY * RobotConstants.driveRightMultiplier;

        if (directionX != 0) {
            float heading = (float) (Math.atan(directionX / directionY) * headingConstant);
            if (directionX < 0) {
                heading *= -1;
            }
            if (directionY < 0) {
                heading += 2;
            }
            heading -= 1;
            if (RobotConstants.driveTurnDirection > 0) {
                if (directionX < 0) {
                    powerLeft *= heading;
                } else if (directionX > 0) {
                    powerRight *= -heading;
                }
            } else if (RobotConstants.driveTurnDirection < 0) {
                if (directionX < 0) {
                    powerRight *= heading;
                } else if (directionX > 0) {
                    powerLeft *= -heading;
                }
            }
        }

        return new float[]{powerLeft, powerRight};
    }
     */
}
