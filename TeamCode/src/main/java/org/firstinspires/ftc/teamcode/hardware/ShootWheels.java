package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ShootWheels {

    private DcMotor LeftWheel, RightWheel; //Left and right wheels

    HardwareMap hardwareMap;

    private double speed = 1; // DCMOTOR Left and Right Wheel speeds.



    public ShootWheels(HardwareMap hardwareMap)
    {
        RightWheel = this.hardwareMap.get(DcMotor.class, "ShootR"); //Right wheel name set
        LeftWheel = this.hardwareMap.get(DcMotor.class, "ShootL"); //Left wheel name set


        RightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Don't Use the Built in Encoders
        LeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Don't Use the Built in Encoders


        //TODO - Figure out what way the wheels to spin
        RightWheel.setDirection(DcMotor.Direction.FORWARD); // Spins forward (shooting)
        LeftWheel.setDirection(DcMotor.Direction.FORWARD); // Spins backward (shooting)

    }// end of Shoot Wheels


    public void ShootWheelsOpMode(Gamepad gamepadTwo)
    {
        if(gamepadTwo.a)
        {
            //TODO - Will add a way to change the speed automatically
            RightWheel.setPower(speed);
            LeftWheel.setPower(speed);;
        }
        else if(!gamepadTwo.a){
            //Set it to idle speed, at a speed like 0.2f (20%)
            RightWheel.setPower(0.2);
            LeftWheel.setPower(0.2);;
        }

    } //End of Shoot Wheels


    //Dont need telem, so we leave it out.
}
