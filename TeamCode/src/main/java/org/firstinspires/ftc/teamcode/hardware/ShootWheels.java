package org.firstinspires.ftc.teamcode.hardware;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.Servo;

import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import static org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver.LayerHeight;

import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

public class ShootWheels {


   // GoBildaPrismDriver prism;

    private VoltageSensor myControlHubVoltageSensor;

    public DcMotor WHEEL_L, WHEEL_R; // Single shooter wheel
    public static Servo hood;
    private double targetPosition = 0; // ticks target,should be set to 0.
    private ElapsedTime timer;

    double pos = 0.75;



    public ShootWheels(HardwareMap hardwareMap) {


        // Initialize hardware
        WHEEL_L = hardwareMap.get(DcMotor.class, "WHEEL_L");

        WHEEL_R = hardwareMap.get(DcMotor.class, "WHEEL_R");

        hood = hardwareMap.get(Servo.class, "H");

        //prism = hardwareMap.get(GoBildaPrismDriver.class,"prism");


        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
// The string "Control Hub" may vary based on your configuration file name.


        // Motor setup
        WHEEL_L.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL_L.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // or RUN_USING_ENCODER if you want built-in velocity control
        WHEEL_L.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // don't actively brake
        WHEEL_L.setDirection(DcMotor.Direction.FORWARD);



        WHEEL_R.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL_R.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // or RUN_USING_ENCODER if you want built-in velocity control
        WHEEL_R.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // don't actively brake
        WHEEL_R.setDirection(DcMotor.Direction.FORWARD );



        //prism.setStripLength(64);

        //setColor(255, 0, 0);






        timer = new ElapsedTime();
        timer.reset();
    }

    /** Call this in your main loop to control wheel speed and hood */
    public static double distance;
    boolean wheelOn;



    public double compensation ;


    boolean WheelL, WheelR;
    double brightness;
    public void ShootWheelsOpMode(Gamepad gamepadTwo, Gamepad gamepad2Old, LimeLight limeLight, HardwareMap hardwareMap) {

        //setColor(0, 255, 0);

        if (gamepadTwo.square && !gamepad2Old.square) {
            wheelOn = !wheelOn;
        }


        if(wheelOn) {

            if(gamepadTwo.dpad_up && !gamepad2Old.dpad_up)
            {
                WheelL = !WheelL;

                if(WheelL){
                    WHEEL_L.setPower(0.8);
                }else{
                    WHEEL_L.setPower(0);
                }
            }

            if(gamepadTwo.dpad_down && !gamepad2Old.dpad_down)
            {
                WheelR = ! WheelR;

                if(WheelR){
                    WHEEL_R.setPower(0.8);
                }else{
                    WHEEL_R.setPower(0);
                }
            }

        }

    }

    /** Telemetry for debugging */
    public void Display_Telemetry(Telemetry telemetry) {


        telemetry.addData("Hood Position", hood.getPosition());
        telemetry.addData("Wheel % ", Math.round(WHEEL_L.getPower() * 100) * compensation + "%");
      //  telemetry.addData("Wheel Encoder", WHEEL.getCurrentPosition());
        telemetry.addData("","");
        telemetry.addData("Wheel PID Target", targetPosition);


        telemetry.addData("DistGettingReturned", distance);

        telemetry.addData("ServoPOs", pos);

        telemetry.addData("RPM", currentRPM());
    }




    double currentRPM() {
        return 0;
    }

    public void AutoSHOOT(LimeLight limeLight, HardwareMap hardwareMap, Telemetry telemetry) {
    }
}
