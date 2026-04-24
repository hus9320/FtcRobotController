/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

/*
 * This OpMode executes a POV Game style Teleop for a direct drive robot
 * The code is structured as a LinearOpMode
 *
 * In this mode the left stick moves the robot FWD and back, the Right stick turns left and right.
 * It raises and lowers the arm using the Gamepad Y and A buttons respectively.
 * It also opens and closes the claws slowly using the left and right Bumper buttons.
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="coaxial4bar", group="Robot")
public class Shreeniehu extends LinearOpMode {

    // Motors
    public DcMotor leftMotor;
    public DcMotor rightMotor;

    // Servos
    public Servo topLeft, topRight; // 4-bar
    public Servo bottomLeft, bottomRight; // coaxial 4-bar
    public Servo wrist;      // wrist
    public Servo claw;      // claw

    @Override
    public void runOpMode() {

        // Motors
        leftMotor  = hardwareMap.get(DcMotor.class, "left");
        rightMotor = hardwareMap.get(DcMotor.class, "right");

        leftMotor.setDirection(DcMotor.Direction.REVERSE);
        rightMotor.setDirection(DcMotor.Direction.FORWARD);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //leftMotor.setTargetPosition(0);
        //rightMotor.setTargetPosition(0);

        leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        // Servos
        topLeft = hardwareMap.get(Servo.class, "topleft");
        topRight = hardwareMap.get(Servo.class, "topright");
        bottomLeft = hardwareMap.get(Servo.class, "bottomleft");
        bottomRight = hardwareMap.get(Servo.class, "bottomright");
        wrist = hardwareMap.get(Servo.class, "wrist");
        claw = hardwareMap.get(Servo.class, "claw");

        int SLIDES_HOME = 0;

        double TL_HOME = 0.4, TR_HOME = 0.6;
        double BL_HOME = 0.5, BR_HOME = 0.5;
        double WRIST_HOME = 0.8, CLAW_HOME = 0.75;

        int SLIDES_MACRO = 2500; //????????????????????????
        double TL_MACRO = 0., TR_MACRO = 0.;
        double BL_MACRO = 0., BR_MACRO = 0.;

        // Start positions
        topLeft.setPosition(TL_HOME);
        topRight.setPosition(TR_HOME);
        bottomLeft.setPosition(BL_HOME);
        bottomRight.setPosition(BR_HOME);
        wrist.setPosition(WRIST_HOME);
        claw.setPosition(CLAW_HOME);

        int slidePos = 0;

        boolean lastRB = false;
        boolean lastLB = false;
        boolean lastDpadUp = false;
        boolean lastDpadDown = false;
        waitForStart();


        while (opModeIsActive()) {


            double input = -gamepad1.right_stick_y; // invert for natural control

            // =======================
            // MANUAL SLIDE CONTROL
            // =======================
            if (Math.abs(input) > 0.05) {
                slidePos += (int)(input * 20); // speed
            }

            // =======================
            // MACRO: EXTEND + ROTATE
            // =======================
            boolean rb = gamepad1.right_bumper;
            if (rb && !lastRB) {
                slidePos = SLIDES_MACRO;

                topLeft.setPosition(TL_MACRO);
                topRight.setPosition(TR_MACRO);
                bottomLeft.setPosition(BL_MACRO);
                bottomRight.setPosition(BR_MACRO);
            }
            lastRB = rb;

            // =======================
            // RESET / HOME
            // =======================
            boolean lb = gamepad1.left_bumper;
            if (lb && !lastLB) {
                slidePos = SLIDES_HOME;

                topLeft.setPosition(TL_HOME);
                topRight.setPosition(TR_HOME);
                bottomLeft.setPosition(BL_HOME);
                bottomRight.setPosition(BR_HOME);

                wrist.setPosition(WRIST_HOME);
                claw.setPosition(CLAW_HOME);
            }
            lastLB = lb;

            // Clamp slide range
            slidePos = Range.clip(slidePos, 0, 3000);

            // Apply to motors (HOLD POSITION)
            leftMotor.setTargetPosition(slidePos);
            rightMotor.setTargetPosition(slidePos);

            leftMotor.setPower(0.6);
            rightMotor.setPower(0.6);

            /*boolean rbPressed = gamepad1.right_bumper;

            if (rbPressed && !lastRB) {

                // Move slides to target height
                slidePos = SLIDES_MACRO;

                // Top 4-bar (rotate)
                topLeft.setPosition(TL_MACRO);
                topRight.setPosition(TR_MACRO);

                // Bottom 4-bar
                bottomLeft.setPosition(BL_MACRO);
                bottomRight.setPosition(BR_MACRO);
            }

            lastRB = rbPressed;

            boolean lbPressed = gamepad1.left_bumper;

            if (lbPressed && !lastLB) {

                // Reset slides
                slidePos = SLIDES_HOME;

                // Reset arms
                topLeft.setPosition(TL_HOME);
                topRight.setPosition(TR_HOME);

                bottomLeft.setPosition(BL_HOME);
                bottomRight.setPosition(BR_HOME);

                // Reset wrist & claw
                wrist.setPosition(WRIST_HOME);
                claw.setPosition(CLAW_HOME);
            }

            lastLB = lbPressed;
        /*    double input = gamepad1.right_stick_y; // negate for intuitive direction

            if (Math.abs(input) > 0.05) {
                slidePos += (int) (10 * input);
            }


            slidePos = Range.clip(slidePos, 0, 3000); // set max to your slide's range
            double input = gamepad1.right_stick_y;

            /*if (rbPressed && !lastRB) {
                slidePos = SLIDES_MACRO;
            }
            else if (lbPressed && !lastLB) {
                slidePos = SLIDES_HOME;
            }
            else if (Math.abs(input) > 0.05) {
                slidePos += (int)(10 * input);
            }

            leftMotor.setTargetPosition(slidePos);
            rightMotor.setTargetPosition(slidePos);
            leftMotor.setPower(0.5);

            rightMotor.setPower(0.5);*/

            // 4-BAR ()
            double step = 0.1;

            boolean dpadUpPressed = gamepad1.dpad_up;
            boolean dpadDownPressed = gamepad1.dpad_down;

            if (dpadUpPressed && !lastDpadUp) {
                topLeft.setPosition(Range.clip(topLeft.getPosition() + step, 0, 1));
                topRight.setPosition(Range.clip(topRight.getPosition() - step, 0, 1));
            }

            if (dpadDownPressed && !lastDpadDown) {
                topLeft.setPosition(Range.clip(topLeft.getPosition() - step, 0, 1));
                topRight.setPosition(Range.clip(topRight.getPosition() + step, 0, 1));
            }

            lastDpadUp = dpadUpPressed;
            lastDpadDown = dpadDownPressed;

            // COAXIAL 4-BAR (D-pad)
            if (gamepad1.dpad_left) {
                bottomLeft.setPosition(Range.clip(bottomLeft.getPosition() + step/2, 0, 1));
                bottomRight.setPosition(Range.clip(bottomRight.getPosition() - step/2, 0, 1));
            } else if (gamepad1.dpad_right) {
                bottomLeft.setPosition(Range.clip(bottomLeft.getPosition() - step/2, 0, 1));
                bottomRight.setPosition(Range.clip(bottomRight.getPosition() + step/2, 0, 1));
            }

            // WRIST (Y / A)
            if (gamepad1.y) {
                wrist.setPosition(0.5);
            } else if (gamepad1.a) {
                wrist.setPosition(0.8);
            }

            // CLAW (X / B)
            if (gamepad1.x) {
                claw.setPosition(0.75); // open
            } else if (gamepad1.b) {
                claw.setPosition(0.0); // close
            }

            // TELEMETRY
            telemetry.addData("Slides position", slidePos);
            //telemetry.addData("Slides input", input);
            telemetry.addData("Slides target", leftMotor.getTargetPosition());
            telemetry.addData("Slides left currentPos", leftMotor.getCurrentPosition());
            telemetry.addData("Slides right currentPos", rightMotor.getCurrentPosition());

            telemetry.addData("Top Left (tl)", topLeft.getPosition());
            telemetry.addData("Top Right (tr)", topRight.getPosition());

            telemetry.addData("Bottom Left (bl)", bottomLeft.getPosition());
            telemetry.addData("Bottom Right (br)", bottomRight.getPosition());

            telemetry.addData("Wrist", wrist.getPosition());
            telemetry.addData("Claw", claw.getPosition());

            telemetry.update();

            sleep(50);
        }
    }
}