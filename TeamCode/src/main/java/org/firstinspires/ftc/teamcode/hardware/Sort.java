package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Sort {
    public Servo sort; //the main sort servo (the axon under our bot)


    //@note, this Class is called in both autos as well.
    public Sort(HardwareMap hardwareMap)
    {
        //this servo is located directly under our bot, This is an axon.
        sort = hardwareMap.get(Servo.class, "S");
        sort.setDirection(Servo.Direction.REVERSE  ); //THIS NEEDS TO BE THE DIR
    }

    int setpos = 1; //this is used to toggle between the different sort pos @warn do not change this from a int, it interfaces with a list.

    float sortposup;

    //
    public boolean flipdir, MODE; //last commit, this used to be a carroselOn, This is now a boolean for if we are taking in balls or shooting.
    //@note MODES ARE AS FOLLOWED:
    //@note - MODE (TRUE) : INTAKE POS              - MODE (FALSE) : OUTTAKE POS


    //@note we need to wrap the code cleanly, so we throw a repeat on the end
    private final double[] INTAKEPOS = {0.75, 0.33, 0, 0.33}; //@warn This is MODE (TRUE)
    private final double[] OUTTAKEPOS = {0.9, 0.525, 0.150, 0.525}; //@warn This is MODE (FALSE)

    public void SortOpMode(Gamepad Gamepad2, Gamepad oldGamepad2, Flick flick)
    {

        if(Gamepad2.triangle && !oldGamepad2.triangle) //check if we hit the button last frame, this prevents toggles hundreds of times a second
        {
            MODE = !MODE; //set it like a LightSwitch, on or off, and if its off its on, ect.
        }

        //# Old Code For @NOTE THIS IS COLLAPSEABLE
        if(MODE)
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


//        if(Gamepad2.right_bumper && !oldGamepad2.right_bumper && !flick.flickup)
//        {
//
//
//
//           setpos = (setpos + 1) % POSITIONSBackward.length; //get the
//           sort.setPosition(POSITIONSBackward[setpos]);
//
//
//        }
//        if(Gamepad2.left_bumper && !oldGamepad2.left_bumper && !flick.flickup )
//        {
//          // % -POSITIONSBackward.length; //get the
//            setpos = (setpos - 1);
//           if(setpos < 0)
//           {
//               setpos = POSITIONSBackward.length-1;
//           }
//
//
//            sort.setPosition(POSITIONSBackward[setpos]);
//
//        }

        if(Gamepad2.right_bumper && !oldGamepad2.right_bumper)
        {
            //FIRST LETS TEST TO SEE IF WHAT MODE WE ARE ON.

            //WE DO THIS BY CHECKING THE KEY. around about @line(32)
//            if(MODE){ //INTAKE
//                setpos = (setpos + 1) % INTAKEPOS.length; //get the
//                sort.setPosition(INTAKEPOS[setpos]);
//            }
//            else{ //OUTTAKE
                setpos = (setpos + 1) % OUTTAKEPOS.length; //get the
                sort.setPosition(OUTTAKEPOS[setpos]);
            //}





        }
        if(Gamepad2.left_bumper && !oldGamepad2.left_bumper )
        {
          //FIRST LETS TEST TO SEE IF WHAT MODE WE ARE ON.

            //WE DO THIS BY CHECKING THE KEY. around about @line(32)
//            if(MODE){ //INTAKE
//                setpos = (setpos - 1) % INTAKEPOS.length;
//                sort.setPosition(INTAKEPOS[setpos]);
//            }
          //  else{ //OUTTAKE
                setpos = (setpos - 1) % OUTTAKEPOS.length;
                sort.setPosition(OUTTAKEPOS[setpos]);
           // }

        }

        //@warn Disable this when you have a normal drive, @note Debug only.
        //@note this is only for tuning
//        if(Gamepad2.right_bumper && !oldGamepad2.right_bumper)
//        {
//            setpos = setpos + 0.005f;
//        }
//        if(Gamepad2.left_bumper && !oldGamepad2.left_bumper)
//        {
//            setpos = setpos - 0.005f;
//        }
//        sort.setPosition(setpos);


    }



    //@warn THE AUTO CODE NEEDS AN UPDATE BECAUSE WE CANNOT USE THE SORTER IN THE CAROUSEL MODE
    public void SortTelem(Telemetry telemetry)
    {
        telemetry.addData("SetPos", setpos);
       // telemetry.addData("ServoPos", sort.getPosition());
    }

    public void CarroselOn() {
        //carroselOn = true;
    }

    public void CarroselOff()
    {
       //carroselOn = false;
    }
}
