package org.firstinspires.ftc.teamcode.hardware;


import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

//TODO - Code the intake
public class Intake {
    private DcMotor spin_input; //Spin Input

    public Intake(HardwareMap hardwareMap)
    {
        //TODO - DEFINE MOTORS
        spin_input = hardwareMap.get(DcMotor.class, "I"); // Input MOTOR
        spin_input.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); //RUN WITHOUT TRACKING
        spin_input.setDirection(DcMotor.Direction.REVERSE  ); //SPIN IT BACKWARDS


    }


    public void IntakeOpMode(Gamepad Gamepad2)
    {

        //intake.
        if(Gamepad2.a)
        {
            spin_input.setPower(1);
        }
        else if(Gamepad2.options)
        {
            spin_input.setPower(-1);
        }
        else {
            spin_input.setPower(0);
        }

    }


    void Display_Telemetry(Telemetry telemetry)
    {
        //TODO - Code Telemetry
        //and example of telem
        //telemetry.addData("TEST", FAKEVAR);
    }
}
