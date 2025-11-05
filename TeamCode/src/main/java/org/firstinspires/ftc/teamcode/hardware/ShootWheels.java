package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootWheels {

    private DcMotor WHEEL; //Left and right wheels

    HardwareMap hardwareMap;


    // Telemetry limelightTelem; //Used to prevent error, nothing else



    public ShootWheels(HardwareMap hardwareMap)
    {
        this.hardwareMap = hardwareMap; // fix's an error

       // limeLight = new LimeLight(hardwareMap, limelightTelem);



        WHEEL = this.hardwareMap.get(DcMotor.class, "WHEEL"); //Right wheel name set
        //LeftWheel = this.hardwareMap.get(DcMotor.class, "ShootL"); //Left wheel name set


        WHEEL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Don't Use the Built in Encoders
        //LeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //Don't Use the Built in Encoders


        //TODO - Figure out what way the wheels to spin
        WHEEL.setDirection(DcMotor.Direction.REVERSE); // Spins forward (shooting)
//        LeftWheel.setDirection(DcMotor.Direction.REVERSE); // Spins backward (shooting)

    }// end of Shoot Wheels


    public void ShootWheelsOpMode(Gamepad gamepadTwo)
    {
        //WHEEL.setPower(0.8);

            //TODO - Will add a way to change the speed automatically


//                LeftWheel.setPower(1);





    } //End of Shoot Wheels


    public void Display_Telemetry(Telemetry telemetry, Gamepad gamepadTwo)
    {
        telemetry.addData("Is A Key Pressed:", gamepadTwo.a);
    }





    //Dont need telem, so we leave it out.
}
