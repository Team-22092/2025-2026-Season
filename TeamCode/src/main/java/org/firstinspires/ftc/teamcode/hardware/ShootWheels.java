package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class ShootWheels {

    private DcMotor WHEEL; // Single shooter wheel
    private Servo hood;
    private double targetPosition = 0; // ticks target,should be set to 0.
    private ElapsedTime timer;

    double pos = 0.75;



    public ShootWheels(HardwareMap hardwareMap) {


        // Initialize hardware
        WHEEL = hardwareMap.get(DcMotor.class, "WHEEL");
        hood = hardwareMap.get(Servo.class, "H");

        // Motor setup
        WHEEL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // or RUN_USING_ENCODER if you want built-in velocity control
        WHEEL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // don't actively brake
        WHEEL.setDirection(DcMotor.Direction.FORWARD);



        timer = new ElapsedTime();
        timer.reset();
    }

    /** Call this in your main loop to control wheel speed and hood */
    double distance;
    boolean wheelOn;
    public void ShootWheelsOpMode(Gamepad gamepadTwo, Gamepad gamepad2Old, LimeLight limeLight) {


        if (gamepadTwo.left_bumper && !gamepad2Old.left_bumper) {
            wheelOn = !wheelOn;
        }
//
//        if(wheelOn)
//        {
            double currentPos = WHEEL.getCurrentPosition();

            distance = limeLight.distance;
//OLD (354.39823*Math.pow(distance, 2) + -525.61364*distance + 3514.68988)
            targetPosition = 263.56729*Math.pow(distance, 2) + -301.42887*distance + 3413.50402 ;





            // Adjust hood position


        if(gamepadTwo.dpad_left && !gamepad2Old.dpad_left){
            pos+=0.02;
        }
        if(gamepadTwo.dpad_right && !gamepad2Old.dpad_right)
        {
            pos -= 0.02;
        }

        //    pos = 0.00075*Math.pow(distance, 2) + -0.03851*distance + 0.74990;
        pos = 0.00226*Math.pow(distance, 2) + -0.04383*distance + 0.75969;

            pos = Math.max(0.0, Math.min(1.0, pos));
            hood.setPosition(pos);




        targetRPM(targetPosition);



            //debug code

            if (gamepadTwo.a && !gamepad2Old.a) {
                  targetPosition += 50;

              }
            if (gamepadTwo.b && !gamepad2Old.b)
              {
                    targetPosition -= 50;
              }
//        }
//        else{
//            WHEEL.setPower(0);
//        }




    }

    /** Telemetry for debugging */
    public void Display_Telemetry(Telemetry telemetry) {


        telemetry.addData("Hood Position", hood.getPosition());
        telemetry.addData("Wheel % ", Math.round(WHEEL.getPower() * 100) + "%");
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


    void targetRPM(double targetRPM) {
        double current = currentRPM();
        double error = targetRPM - current;

        double k = 0.000003;

        double power = WHEEL.getPower();

        if (error > 0) {
            power += error * k;
        } else {
            power -= Math.min(Math.abs(error * k), power);
        }

        power = Math.max(0, Math.min(1, power));
        WHEEL.setPower(power);
    }




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

}
