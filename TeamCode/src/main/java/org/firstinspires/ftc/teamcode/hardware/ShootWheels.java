package org.firstinspires.ftc.teamcode.hardware;

import static com.acmerobotics.roadrunner.Math.clamp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootWheels {

    private VoltageSensor myControlHubVoltageSensor;

    public DcMotorEx WHEEL_L, WHEEL_R;

    public  double highVeocity = 1580;

    public double lowVelocity = 980;

    double ourTargetVel = highVeocity;

    double F = -14.6;
    double P = -0.7250;


    double[] stepSizes = {10.0, 1.0, 0.1, 0.001, 0.0001};

    int stepIndex = 1;

    public static Servo hood;

    private ElapsedTime timer;

    double pos = 0.75;
    public static double distance;
    boolean wheelOn;


    // RPM tracking variables
    private ElapsedTime rpmTimer = new ElapsedTime();



    public ShootWheels(HardwareMap hardwareMap) {
        // Initialize hardware
        WHEEL_L = hardwareMap.get(DcMotorEx.class, "WHEEL_L");
        WHEEL_R = hardwareMap.get(DcMotorEx.class, "WHEEL_R");
        hood = hardwareMap.get(Servo.class, "H");

        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");

        // Motor setup
        WHEEL_L.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL_L.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WHEEL_L.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        WHEEL_L.setDirection(DcMotor.Direction.REVERSE);

        WHEEL_R.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL_R.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        WHEEL_R.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        WHEEL_R.setDirection(DcMotor.Direction.REVERSE);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0.0,0.0, F);
        WHEEL_L.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);
        WHEEL_R.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidfCoefficients);


        timer = new ElapsedTime();
        timer.reset();
        rpmTimer.reset();
    }
    double curVelocity = 0;
    double error;
    double hoodpos = 0.6;
    public void ShootWheelsOpMode(Gamepad gamepadTwo, Gamepad gamepad2Old, LimeLight limeLight, HardwareMap hardwareMap) {
        if (gamepadTwo.square && !gamepad2Old.square) {
            wheelOn = !wheelOn;
        }

        if (wheelOn) {





            if(gamepadTwo.dpadUpWasPressed()){
                ourTargetVel += 50;
            }
            if(gamepadTwo.dpadDownWasPressed()){
                ourTargetVel -= 50;
            }


            double currentVoltage = myControlHubVoltageSensor.getVoltage();


            double liveF = F * (12.8 / currentVoltage);

            PIDFCoefficients livePIDF = new PIDFCoefficients(P, 0.0, 0.0, liveF);
            WHEEL_L.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, livePIDF);
            WHEEL_R.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, livePIDF);

            WHEEL_L.setVelocity(ourTargetVel);
            WHEEL_R.setVelocity(ourTargetVel);


            curVelocity = WHEEL_R.getVelocity(); //we only get right, cause they are linked.
           error = ourTargetVel - (curVelocity);



        //set hood.

            hood.setPosition(hoodpos);

            if(gamepadTwo.dpadLeftWasPressed()){
                hoodpos += 0.05;
            }
            if(gamepadTwo.dpadRightWasPressed()){
                hoodpos -= 0.05;
            }






















        } else {
            WHEEL_L.setPower(0);
            WHEEL_R.setPower(0);
        }
    }

    /** Telemetry for debugging */
    public void Display_Telemetry(Telemetry telemetry) {
//        telemetry.addData("Hood Position", hood.getPosition());
//        telemetry.addData("Wheel % ", Math.round(WHEEL_L.getPower() * 100) * compensation + "%");
//        telemetry.addData("Wheel PID Target", compensatedPower);
//        telemetry.addData("DistGettingReturned", distance);
//
        telemetry.addData("Current Velocity", curVelocity);


        telemetry.addData("SERVO", hood.getPosition());



    }



    public void AutoSHOOT(LimeLight limeLight, HardwareMap hardwareMap, Telemetry telemetry) {
        distance = limeLight.distance;

        double idealVoltage = 13.50;
        double currentVoltage = myControlHubVoltageSensor.getVoltage();
        double autoCompensation = idealVoltage / currentVoltage;

        // Regression for power
        double base = -0.01526 * Math.pow(distance, 2) + 0.13556 * distance + 0.35106;
        double targetPower = base * autoCompensation;

        // Regression for hood position
        pos = 0.00305 * Math.pow(distance, 2) + -0.02711 * distance + 0.74979;
        pos = Math.max(0.0, Math.min(1.0, pos));
        hood.setPosition(pos);

        WHEEL_L.setPower(targetPower);
        WHEEL_R.setPower(targetPower);

        telemetry.addData("Auto Power", targetPower);
        telemetry.addData("Auto Hood", pos);
    }
}
