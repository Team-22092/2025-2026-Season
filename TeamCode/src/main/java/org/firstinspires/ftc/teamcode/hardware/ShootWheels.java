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

    boolean shouldusewheel = true;

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
        distance = limeLight.distance;

        if (gamepadTwo.square && !gamepad2Old.square) {
            wheelOn = !wheelOn;
        }

        if (wheelOn) {
            if (distance < 1.9 && distance >= 0) {
                pos = 0.6;
                ourTargetVel = 1100;
            }
            else if (distance < 2.0) {
                pos = 0.6;
                ourTargetVel = 1260;
            }
            else if (distance < 3.0) {
                pos = 0.6;
                ourTargetVel = 1320;
            }
            else {
                pos = 0.7;
                ourTargetVel = 1460;
            }

//            if(gamepadTwo.dpadUpWasPressed()){
//                ourTargetVel += 50;
//            }
//            if(gamepadTwo.dpadDownWasPressed()){
//                ourTargetVel -= 50;
//            }



            double currentVoltage = myControlHubVoltageSensor.getVoltage();


            double liveF = F * (12.8 / currentVoltage);

            PIDFCoefficients livePIDF = new PIDFCoefficients(P, 0.0, 0.0, liveF);
            WHEEL_L.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, livePIDF);
            WHEEL_R.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, livePIDF);
            if(shouldusewheel) {
                WHEEL_L.setVelocity(ourTargetVel);
                WHEEL_R.setVelocity(ourTargetVel);
            }


            curVelocity = WHEEL_R.getVelocity(); //we only get right, cause they are linked.
           error = ourTargetVel - (curVelocity);



        //set hood.

            hood.setPosition(pos);

//            if(gamepadTwo.dpadLeftWasPressed()){
//                hoodpos += 0.1;
//            }
//            if(gamepadTwo.dpadRightWasPressed()){
//                hoodpos -= 0.1 ;
//            }






















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


            pos = 0.8;
            ourTargetVel = 1460;




        double currentVoltage = myControlHubVoltageSensor.getVoltage();


        double liveF = F * (12.8 / currentVoltage);

        PIDFCoefficients livePIDF = new PIDFCoefficients(P, 0.0, 0.0, liveF);
        WHEEL_L.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, livePIDF);
        WHEEL_R.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, livePIDF);
        if(shouldusewheel) {
            WHEEL_L.setVelocity(ourTargetVel);
            WHEEL_R.setVelocity(ourTargetVel);
        }


        curVelocity = WHEEL_R.getVelocity(); //we only get right, cause they are linked.
        error = ourTargetVel - (curVelocity);



        //set hood.

        hood.setPosition(pos);
    }

    public void spinUp() {
        wheelOn = true;
        WHEEL_L.setVelocity(ourTargetVel + 300);
        WHEEL_R.setVelocity(ourTargetVel + 100);
        shouldusewheel = false;
    }

    public void stop() {

        WHEEL_L.setVelocity(ourTargetVel);
        WHEEL_R.setVelocity(ourTargetVel);
        shouldusewheel = true;
    }


}
