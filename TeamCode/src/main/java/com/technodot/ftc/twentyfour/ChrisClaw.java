package com.technodot.ftc.twentyfour;

public class ChrisClaw {
    private boolean clawOpen = false;
    private int slideStatus = 0;
    private long slideTime = 0;
    private long slideTimer = -1;

    public void updateClawStatus(boolean clawPressed) {
        if (clawPressed) {
            if (clawOpen) {
                MaxyMotor.clawMain.setPosition(0.0);
            } else {
                MaxyMotor.clawMain.setPosition(0.4);
            }
            clawOpen = !clawOpen;
        }
    }

    public void updateSlideStatus(boolean slideExtendPressed, boolean slideRetractPressed) {
        // ok this code is messed up as hell so more comments here
        long currentTime = System.currentTimeMillis(); // get current time

        if (slideExtendPressed) { // extend slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime >= BaboOS.slideConstant) { // slide can move
                    MaxyMotor.slideMain.setPower(-1.0);
                    slideTimer = currentTime;
                    slideStatus = 1;
                }
            } else { // slide is moving
                if (currentTime >= slideTimer - slideTime + BaboOS.slideConstant) { // slide can't move
                    MaxyMotor.slideMain.setPower(0.0);
                    slideTime = BaboOS.slideConstant;
                    slideStatus = 0;
                    slideTimer = 0;
                } else if (slideStatus != 0) { // slide is moving
                    MaxyMotor.slideMain.setPower(-1.0);
                }
            }
        } else if (slideRetractPressed) { // retract slide button pressed
            if (slideStatus == 0) { // slide not moving
                if (slideTime >= BaboOS.slideConstant) { // slide can move
                    MaxyMotor.slideMain.setPower(1.0);
                    slideTimer = currentTime;
                    slideStatus = -1;
                }
            } else { // slide is moving
                if (currentTime >= slideTimer + slideTime) { // slide can't move
                    MaxyMotor.slideMain.setPower(0.0);
                    slideTime = 0;
                    slideStatus = 0;
                    slideTimer = 0;
                } else if (slideStatus != 0) { // slide is moving
                    MaxyMotor.slideMain.setPower(1.0);
                }
            }
        } else { // neither buttons pressed
            MaxyMotor.slideMain.setPower(0.0);
            if (slideStatus == 1) {
                slideTime += currentTime - slideTimer;
                slideStatus = 0;
                slideTimer = 0;
            } else if (slideStatus == -1) {
                slideTime -= currentTime - slideTimer;
                slideStatus = 0;
                slideTimer = 0;
            }
        }
    }
}
