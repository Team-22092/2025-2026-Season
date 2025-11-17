package org.firstinspires.ftc.teamcode.hardware;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Sort {
    private Servo sort;

    public Sort(HardwareMap hardwareMap)
    {
        //TODO - DEFINE MOTORS
        sort = hardwareMap.get(Servo.class, "S");
        sort.setDirection(Servo.Direction.REVERSE  );


    }

    int setpos = 1;
    private final double[] POSITIONSForward = {0.5, 1};

    private final double[] POSITIONSBackward = {0.5, 0};

    public void SortOpMode(Gamepad Gamepad2, Gamepad oldGamepad2)
    {//
        if(Gamepad2.x && !oldGamepad2.x )
        {

            setpos = (setpos + 1) % POSITIONSForward.length; //get the
            sort.setPosition(POSITIONSForward[setpos]);

        }
        if(Gamepad2.y && !oldGamepad2.y)
        {
            setpos = (setpos + 1) % POSITIONSBackward.length; //get the
            sort.setPosition(POSITIONSBackward[setpos]);
        }

    }

    public void SortTelem(Telemetry telemetry)
    {
        telemetry.addData("SetPos", setpos);
       // telemetry.addData("ServoPos", sort.getPosition());
    }


}
