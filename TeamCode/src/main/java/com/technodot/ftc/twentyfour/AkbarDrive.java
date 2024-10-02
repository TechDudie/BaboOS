package com.technodot.ftc.twentyfour;

public class AkbarDrive {
    public float powerLeft;
    public float powerRight;

    public void calculateDriveSpeed(float lx, float ly, float rx, float ry) {
        powerLeft = ly;
        powerRight = ly;

        if (ry != 0) {
            // calculate heading as a value between 1 and -1
            float heading = (float) (Math.atan(rx / ry) * BaboOS.headingConstant);
            if (rx < 0) {
                powerLeft *= -heading;
            } else if (rx > 0) {
                powerRight *= heading;
            }
        } else {
            if (rx < 0) {
                powerLeft = 0;
            } else if (rx > 0) {
                powerRight = 0;
            }
        }
    }

    public void updateDriveSpeed() {
        MaxyMotor.driveLeft.setPower((double) powerLeft);
        MaxyMotor.driveRight.setPower((double) powerRight);
    }
}
