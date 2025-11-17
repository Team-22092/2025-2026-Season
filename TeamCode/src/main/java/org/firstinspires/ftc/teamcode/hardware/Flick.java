package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Flick {
    private Servo flickthing; //servo of Our Flicker

    public Flick(HardwareMap hardwareMap)
    {
        //TODO - DEFINE SERVOS
         flickthing = hardwareMap.get(Servo.class, "F"); //SERVO FLICKER
        flickthing.setDirection(Servo.Direction.REVERSE  ); //SET THE DIR (Not sure its needed)


    }

    int setpos = 0; //the pos to set
    private final double[] POSITIONS = {0.4, 1}; //positions to go to
    public void FlickOpMode(Gamepad Gamepad2, Gamepad oldGamepad2)
    {//
        if(Gamepad2.right_bumper)
        {

         flickthing.setPosition(1);

        }
        else {
            flickthing.setPosition(0.35);
        }


    }

    public void SortTelem(Telemetry telemetry)
    {
        telemetry.addData("SetPos", setpos);
        // telemetry.addData("ServoPos", sort.getPosition());
    }


}
