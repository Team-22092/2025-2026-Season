package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Sort {
    public Servo sort;
    //boolean carroselOn = false;
    public Sort(HardwareMap hardwareMap)
    {
        //TODO - DEFINE MOTORS
        sort = hardwareMap.get(Servo.class, "S");
        sort.setDirection(Servo.Direction.REVERSE  );



    }

    int setpos = 1;

    float sortposup;
    public boolean flipdir, carroselOn;

    private final double[] POSITIONSBackward = {0.19, 0.56, 0.935, 0.56};

    public void SortOpMode(Gamepad Gamepad2, Gamepad oldGamepad2, Flick flick)
    {//

        if(Gamepad2.triangle && !oldGamepad2.triangle)
        {
            carroselOn = !carroselOn;
        }

        if(carroselOn && !flick.flickup)
        {
            sort.setPosition(sortposup);
            if(sortposup >= 1)
            {
                flipdir = !flipdir;
            }
            if(sortposup <= 0){
                flipdir = !flipdir;
            }


            if(flipdir)    sortposup = (float) (sortposup + 0.015);

            if(!flipdir)    sortposup = (float) (sortposup - 0.015);
        }


        if(Gamepad2.right_bumper && !oldGamepad2.right_bumper && !flick.flickup)
        {



           setpos = (setpos + 1) % POSITIONSBackward.length; //get the
           sort.setPosition(POSITIONSBackward[setpos]);


        }
        if(Gamepad2.left_bumper && !oldGamepad2.left_bumper && !flick.flickup )
        {
          // % -POSITIONSBackward.length; //get the
            setpos = (setpos - 1);
           if(setpos < 0)
           {
               setpos = POSITIONSBackward.length-1;
           }


            sort.setPosition(POSITIONSBackward[setpos]);

        }


    }




    public void SortTelem(Telemetry telemetry)
    {
        telemetry.addData("SetPos", setpos);
       // telemetry.addData("ServoPos", sort.getPosition());
    }

    public void CarroselOn() {
        carroselOn = true;
    }

    public void CarroselOff()
    {
       carroselOn = false;
    }
}
