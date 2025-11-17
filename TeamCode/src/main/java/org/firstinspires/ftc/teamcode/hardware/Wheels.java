package org.firstinspires.ftc.teamcode.hardware; //Import hardware (wire mapping)


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor; // Motor func
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad; // Gets gamepad, and buttons
import com.qualcomm.robotcore.hardware.HardwareMap; //Gets hardware

import org.firstinspires.ftc.robotcore.external.Telemetry;


//Going to be used for auto, uncomment then.

//import com.acmerobotics.dashboard.config.Config;
//@Config

@Disabled
public class Wheels {
    public static Double SLOW_MODE_SPEED = 0.33; //0.33 is 33% of the motor's max power.


    HardwareMap hardwareMap; //This is to map the wheels to the wiring.
//


    private DcMotor Front_Left_Wheel, Front_Right_Wheel, //wheel motors <FRONT ONES>
                    Back_Left_Wheel, Back_Right_Wheel; //wheel motors <FRONT ONES>


    double[] speeds = new double[4]; //Speeds for 4 motors.


    double MotorGlobalSpeed = 1; //this is half speed (higher is faster so 0.6 > 0.3)




    /*
             z (turn / rotation)
             ↑
             |
             |
             +--------→ x (strafe, left/right)
            /0
           /
          ↓
         y (drive, forward/back) */

    private double Drive, Strafe, Turn, Max; // the max var normalizes the speed, so it keeps speeds[] between -1, 1





    //USED IN teleopMAIN.java
    public Wheels(HardwareMap hardwareMap) {

        this.hardwareMap = hardwareMap; // fix's an error

        Front_Left_Wheel = this.hardwareMap.get(DcMotor.class, "FL"); // Front Left Motor is set to LF as the string identifier
        Front_Right_Wheel = this.hardwareMap.get(DcMotor.class, "FR"); // Front Right Motor is set to LR as the string identifier
        Back_Left_Wheel = this.hardwareMap.get(DcMotor.class, "BL"); // Back Left Motor is set to BL as the string identifier
        Back_Right_Wheel = this.hardwareMap.get(DcMotor.class, "BR"); // Back Right Motor is set to BR as the string identifier



        //Set them all to use no encoders (don't track pos)
        Front_Left_Wheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Front_Right_Wheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Back_Left_Wheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        Back_Right_Wheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



        //Set direction, so Reverse sets left to be the considered "Forward" and right to be "Backword", and Forward sets right to be considered "Forward" and right to be "Backword"
        Front_Left_Wheel.setDirection(DcMotor.Direction.FORWARD); //Because this is flipped on the Robot frame, it needs to be set to reverse
        Front_Right_Wheel.setDirection(DcMotor.Direction.REVERSE); //Not Flipped
        Back_Left_Wheel.setDirection(DcMotor.Direction.FORWARD); //Because this is flipped on the Robot frame, it needs to be set to reverse
        Back_Right_Wheel.setDirection(DcMotor.Direction.REVERSE); //Not Flipped


    } //end of Wheels



    //USED IN teleopMAIN.java
    public void ManualDrive(Gamepad gamepadOne) {
         /*
             z (turn / rotation)
             ↑
             |
             |
             +--------→ x (strafe, left/right)
            /
           /
          ↓
         y (drive, forward/back) */

        Drive = gamepadOne.left_stick_y;//Set drive to be the left stick of y (up down)
        Strafe = gamepadOne.left_stick_x;//Set the drive of the left stick x (left, right)
        Turn = gamepadOne.right_stick_x; //Spinning (x,y)


        //speeds of the motors, [0] is LF, [1] is RF, [2] is BL, and [3] is BR

        speeds[0] = - Drive + Turn + Strafe;
        speeds[1] = - Drive - Turn - Strafe;
        speeds[2] = - Drive + Turn - Strafe;
        speeds[3] = - Drive - Turn + Strafe;


        //Set to avoid an edge case if all values are negative.
        Max = Math.abs(speeds[0]);
        //Func to limit all the speed pieces in a list
        for(int i = 1; i < speeds.length; i++)
        {
            //If Max is less than -1, restrict it
            if(Max < Math.abs(speeds[i])) Max = Math.abs(speeds[i]); //if the speed is greater than the limit (-1, 1) crop them.
        }

        if(Max > 1) //If the max is greater than 1, handing another edge case
        {
            for(int i = 0; i < speeds.length; i++) speeds[i] /= Max;
        }


        if(gamepadOne.right_bumper) { //Slow mode, * Motor power by Slow Mode Speed

            //[0] is LF, [1] is RF, [2] is BL, and [3] is BR
            Front_Left_Wheel.setPower(speeds[0] * SLOW_MODE_SPEED);
            Front_Right_Wheel.setPower(speeds[1] * SLOW_MODE_SPEED);;
            Back_Left_Wheel.setPower(speeds[2] * SLOW_MODE_SPEED);
            Back_Right_Wheel.setPower(speeds[3] * SLOW_MODE_SPEED);

        }
        else{ //Slow mode not pressed, motors run at almost full speed

            //[0] is LF, [1] is RF, [2] is BL, and [3] is BR
            Front_Left_Wheel.setPower(speeds[0] * MotorGlobalSpeed); //SLOWDOWN WITH GLOBAL MOTOR SPEED 100% is too fast
            Front_Right_Wheel.setPower(speeds[1] * MotorGlobalSpeed);; //SLOWDOWN WITH GLOBAL MOTOR SPEED 100% is too fast
            Back_Left_Wheel.setPower(speeds[2] * MotorGlobalSpeed); //SLOWDOWN WITH GLOBAL MOTOR SPEED 100% is too fast
            Back_Right_Wheel.setPower(speeds[3] * MotorGlobalSpeed); //SLOWDOWN WITH GLOBAL MOTOR SPEED 100% is too fast
        }


    } //end of Manual Drive


    //TODO - Use LimeLightLineUPTest before this.
    double kp = 0.02; //needs to be adjusted on the robot
    double yawTolerance = 1; //How much the range needs to be
    public void LimeLightLineUP(double yaw) //Auto Line up funct
    {
        if(!Double.isNaN(yaw)) //makes sure that the yaw is not a NAN val
        {
            double Yerror = yaw; //error of the yaw

            if(Math.abs(Yerror) > yawTolerance) { //gets a range, but this historically has not worked in ftc for some reason, so i might have to do this manually
                double power = kp * Yerror; //this figures out what way to turn (+ or -)
                power = Math.max(Math.min(power, 0.5), -0.5); // we figure out if we need +Power or -Power //TODO - The issue is that 50% might be too much.
                Turn = (-power); //TODO this is def wrong this part, needs to be fixed
            }
        }
    } //end of Lime Light Line UP






    //THIS IS A TEST SCRIPT FOR SAFE LINE UP, DO NOT USE IN PRODUCTION.
    public void LimeLightLineUPTest(double yaw, Telemetry telemetry)
    {
        //This is for testing for manual turning btw.

        if(!Double.isNaN(yaw)){ //check to make sure the yaw isn't NAN
            if(yaw > yawTolerance){ //if the yaw (turn, is bigger than the buffer)
                telemetry.addData("Move: ", "LEFT");
            } else if (yaw < yawTolerance){ //if the yaw (turn, is less than the buffer)
                telemetry.addData("Move: ", "RIGHT");
            }
            else{ //if the yaw (don't turn, is bigger than the buffer)
                telemetry.addData("MOVE: ", "CENTERED");
            }
        }
    } //end of Lime Light Line UP Test










} //end of Wheels Main Class
