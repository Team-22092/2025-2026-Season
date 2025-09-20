package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootWheels {

    private DcMotor LeftWheel, RightWheel; //Left and right wheels

    HardwareMap hardwareMap;

    private double speed = 1; // DCMOTOR Left and Right Wheel speeds.



    public ShootWheels(HardwareMap hardwareMap)
    {
        this.hardwareMap = hardwareMap; // fix's an error

        RightWheel = this.hardwareMap.get(DcMotor.class, "ShootR"); //Right wheel name set
        LeftWheel = this.hardwareMap.get(DcMotor.class, "ShootL"); //Left wheel name set


        RightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Don't Use the Built in Encoders
        LeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Don't Use the Built in Encoders


        //TODO - Figure out what way the wheels to spin
        RightWheel.setDirection(DcMotor.Direction.FORWARD); // Spins forward (shooting)
        LeftWheel.setDirection(DcMotor.Direction.REVERSE); // Spins backward (shooting)

    }// end of Shoot Wheels


    public void ShootWheelsOpMode(Gamepad gamepadTwo)
    {

            //TODO - Will add a way to change the speed automatically
            RightWheel.setPower(speed);
            LeftWheel.setPower(speed);;



    } //End of Shoot Wheels


    public void Display_Telemetry(Telemetry telemetry, Gamepad gamepadTwo)
    {
        telemetry.addData("Is A Key Pressed:", gamepadTwo.a);
    }


    //Dont need telem, so we leave it out.
}
