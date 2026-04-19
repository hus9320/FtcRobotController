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

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
    public Servo tl, tr; // 4-bar
    public Servo bl, br; // coaxial 4-bar
    public Servo w;      // wrist
    public Servo c;      // claw

    @Override
    public void runOpMode() {

        // Motors
        leftMotor  = hardwareMap.get(DcMotor.class, "left");
        rightMotor = hardwareMap.get(DcMotor.class, "right");

        leftMotor.setDirection(DcMotor.Direction.FORWARD);
        rightMotor.setDirection(DcMotor.Direction.FORWARD);

        // Servos
        tl = hardwareMap.get(Servo.class, "topleft");
        tr = hardwareMap.get(Servo.class, "topright");
        bl = hardwareMap.get(Servo.class, "bottomleft");
        br = hardwareMap.get(Servo.class, "bottomright");
        w  = hardwareMap.get(Servo.class, "wrist");
        c  = hardwareMap.get(Servo.class, "claw");

        // Start positions
        tl.setPosition(0.4);
        tr.setPosition(0.6);
        bl.setPosition(0.5);
        br.setPosition(0.5);
        w.setPosition(0.8);
        c.setPosition(0.75);

        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();
        boolean lastDpadUp = false;
        boolean lastDpadDown = false;
        int holdPosition =0;
        while (opModeIsActive()) {

            // SLIDES (right joystick)
            double input = gamepad1.right_stick_y;
            double slidePower = -0.25*input;

            if (Math.abs(input)>0.05){
                leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                leftMotor.setPower(slidePower);
                rightMotor.setPower(slidePower);

                holdPosition = leftMotor.getCurrentPosition();
            } else {
                leftMotor.setTargetPosition(holdPosition);
                rightMotor.setTargetPosition(holdPosition);
                leftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                rightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                leftMotor.setPower(0.5);
                rightMotor.setPower(0.5);
            }



            // 4-BAR ()
            double step = 0.1;

            boolean dpadUpPressed = gamepad1.dpad_up;
            boolean dpadDownPressed = gamepad1.dpad_down;

            if (dpadUpPressed && !lastDpadUp) {
                tl.setPosition(Range.clip(tl.getPosition() + step, 0, 1));
                tr.setPosition(Range.clip(tr.getPosition() - step, 0, 1));
            }

            if (dpadDownPressed && !lastDpadDown) {
                tl.setPosition(Range.clip(tl.getPosition() - step, 0, 1));
                tr.setPosition(Range.clip(tr.getPosition() + step, 0, 1));
            }

            lastDpadUp = dpadUpPressed;
            lastDpadDown = dpadDownPressed;

            // COAXIAL 4-BAR (D-pad)
            if (gamepad1.dpad_left) {
                bl.setPosition(Range.clip(bl.getPosition() + step/2, 0, 1));
                br.setPosition(Range.clip(br.getPosition() - step/2, 0, 1));
            } else if (gamepad1.dpad_right) {
                bl.setPosition(Range.clip(bl.getPosition() - step/2, 0, 1));
                br.setPosition(Range.clip(br.getPosition() + step/2, 0, 1));
            }

            // WRIST (Y / A)
            if (gamepad1.y) {
                w.setPosition(0.5);
            } else if (gamepad1.a) {
                w.setPosition(0.8);
            }

            // CLAW (X / B)
            if (gamepad1.x) {
                c.setPosition(0.75); // open
            } else if (gamepad1.b) {
                c.setPosition(0.0); // close
            }

            // TELEMETRY
            telemetry.addData("Slides Power", slidePower);

            telemetry.addData("Top Left (tl)", tl.getPosition());
            telemetry.addData("Top Right (tr)", tr.getPosition());

            telemetry.addData("Bottom Left (bl)", bl.getPosition());
            telemetry.addData("Bottom Right (br)", br.getPosition());

            telemetry.addData("Wrist", w.getPosition());
            telemetry.addData("Claw", c.getPosition());

            telemetry.update();

            sleep(50);
        }
    }
}