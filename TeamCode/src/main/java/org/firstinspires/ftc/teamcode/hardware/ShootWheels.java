package org.firstinspires.ftc.teamcode.hardware;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
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

    public DcMotor WHEEL; // Single shooter wheel
    public static Servo hood;
    private double targetPosition = 0; // ticks target,should be set to 0.
    private ElapsedTime timer;

    double pos = 0.75;



    public ShootWheels(HardwareMap hardwareMap) {


        // Initialize hardware
        WHEEL = hardwareMap.get(DcMotor.class, "WHEEL");
        hood = hardwareMap.get(Servo.class, "H");

        //prism = hardwareMap.get(GoBildaPrismDriver.class,"prism");


        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");
// The string "Control Hub" may vary based on your configuration file name.


        // Motor setup
        WHEEL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // or RUN_USING_ENCODER if you want built-in velocity control
        WHEEL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // don't actively brake
        WHEEL.setDirection(DcMotor.Direction.FORWARD);



        //prism.setStripLength(64);

        //setColor(255, 0, 0);






        timer = new ElapsedTime();
        timer.reset();
    }

    /** Call this in your main loop to control wheel speed and hood */
    public static double distance;
    boolean wheelOn;

    public double compensation ;

    double brightness;
    public void ShootWheelsOpMode(Gamepad gamepadTwo, Gamepad gamepad2Old, LimeLight limeLight, HardwareMap hardwareMap) {

        //setColor(0, 255, 0);

        if (gamepadTwo.square && !gamepad2Old.square) {
            wheelOn = !wheelOn;
        }

        if(wheelOn)
        {
            double currentPos = WHEEL.getCurrentPosition();

            distance = limeLight.distance;
//            double voltage;
//            voltage = myControlHubVoltageSensor.getVoltage();



            double idealVoltage = 13.50;

            double currentVoltage = hardwareMap.voltageSensor.iterator().next().getVoltage();

            //double base = -0.01885*Math.pow(distance, 2) + 0.13790*distance + 0.35232; OLD

            double base = -0.02998*Math.pow(distance, 2) + 0.18677*distance + 0.35187;

            double compensation = idealVoltage / currentVoltage;

            double targetPosition = base * compensation;












           // pos =  0.00229*Math.pow(distance, 2) + -0.02398*distance + 0.72793; OLD
            pos = 0.00230*Math.pow(distance, 2) + -0.02401*distance + 0.72795;






            if(gamepadTwo.dpad_left && !gamepad2Old.dpad_left){
                pos+=0.01;
            }
        if(gamepadTwo.dpad_right && !gamepad2Old.dpad_right)
        {
            pos -= 0.01;
        }



            pos = Math.max(0.0, Math.min(1.0, pos));
            hood.setPosition(pos);

//        if()

            WHEEL.setPower(targetPosition);



            //debug code

            if (gamepadTwo.dpad_up && !gamepad2Old.dpad_up) {
                  targetPosition += 0.05;
                 // brightness += 50;

              }
            if (gamepadTwo.dpad_down && !gamepad2Old.dpad_down)
              {
                    targetPosition -= 0.05;

                    //brightness +=-50;
              }
        }
        else{
            WHEEL.setPower(0);
        }








    }

    /** Telemetry for debugging */
    public void Display_Telemetry(Telemetry telemetry) {


        telemetry.addData("Hood Position", hood.getPosition());
        telemetry.addData("Wheel % ", Math.round(WHEEL.getPower() * 100) * compensation + "%");
      //  telemetry.addData("Wheel Encoder", WHEEL.getCurrentPosition());
        telemetry.addData("","");
        telemetry.addData("Wheel PID Target", targetPosition);


        telemetry.addData("DistGettingReturned", distance);

        telemetry.addData("ServoPOs", pos);

        telemetry.addData("RPM", currentRPM());
    }

    private ElapsedTime rpmTimer = new ElapsedTime();
    private int lastEncoder = 0;
    private double lastTimeSec = 0.0;
    private double test = 0.0;

    // smoothing (moving average)
    private static final int RPM_SMOOTH_SIZE = 5;
    private final double[] rpmBuffer = new double[RPM_SMOOTH_SIZE];
    private int rpmBufIndex = 0;
    private int rpmBufCount = 0;

    // IMPORTANT: set these for your motor
    // ticksPerRev = encoder counts per motor shaft revolution (from datasheet)
    private final double ticksPerRev = 26; // <<--- REPLACE with your motor's CPR
    // gearRatio = motor_revs / wheel_revs (if motor attached directly to wheel use 1.0)
    private final double gearRatio = 1.0; // <<--- set if you have gearbox





    double currentRPM() {

        double now = rpmTimer.seconds();
        int enc = WHEEL.getCurrentPosition();
        double dt = now - lastTimeSec;


        if (dt > 0.02) { // 20 ms minimum sampling
            int deltaTicks = enc - lastEncoder;

            // revolutions in interval
            double revs = deltaTicks / (ticksPerRev * gearRatio);

            // rpm = revs per second * 60
            double measuredRPM = (revs / dt) * 60.0;

            // accumulate into moving average buffer
            rpmBuffer[rpmBufIndex] = measuredRPM;
            rpmBufIndex = (rpmBufIndex + 1) % RPM_SMOOTH_SIZE;
            if (rpmBufCount < RPM_SMOOTH_SIZE) rpmBufCount++;

            double sum = 0.0;
            for (int i = 0; i < rpmBufCount; i++) sum += rpmBuffer[i];
            test = sum / rpmBufCount;

            // store for next iteration
            lastEncoder = enc;
            lastTimeSec = now;
        }

        return test;

    }

    public void AutoSHOOT(LimeLight limeLight, HardwareMap hardwareMap, Telemetry telemetry)
    {
        double currentPos = WHEEL.getCurrentPosition();

        distance = limeLight.distance;

        double idealVoltage = 13.50;

        double currentVoltage = hardwareMap.voltageSensor.iterator().next().getVoltage();

        double base = -0.01526*Math.pow(distance, 2) + 0.13556*distance + 0.35106;

        double compensation = idealVoltage / currentVoltage;

        double targetPosition = base * compensation;


        telemetry.addData(String.valueOf(WHEEL.getPower()), "POWER");

        telemetry.addData(String.valueOf(hood.getPosition()), "HOOD");




        //    pos = 0.00075*Math.pow(distance, 2) + -0.03851*distance + 0.74990;
        //pos = 0.00918*Math.pow(distance, 2) + -0.06259*distance + 0.75987;

        pos =  0.00305*Math.pow(distance, 2) + -0.02711*distance + 0.72979;

        //pos = 0.00171*Math.pow(distance,2) + -0.02322*distance + 0.72784;

        pos = Math.max(0.0, Math.min(1.0, pos));
        hood.setPosition(pos);

//        if()

        WHEEL.setPower(targetPosition);

    }


//    private void setColor(int r, int g, int b) {
//        PrismAnimations.Solid newSolid = new PrismAnimations.Solid(new Color(r, g, b));
//        prism.insertAndUpdateAnimation(LayerHeight.LAYER_0, newSolid);
//    }



}
