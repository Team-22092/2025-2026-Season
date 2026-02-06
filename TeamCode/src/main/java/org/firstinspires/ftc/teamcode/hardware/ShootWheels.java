package org.firstinspires.ftc.teamcode.hardware;

import static com.acmerobotics.roadrunner.Math.clamp;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ShootWheels {

    private VoltageSensor myControlHubVoltageSensor;

    public DcMotor WHEEL_L, WHEEL_R;
    public static Servo hood;
    private double targetPosition = 0; // used as power in manual tuning
    private ElapsedTime timer;

    double pos = 0.75;
    public static double distance;
    boolean wheelOn;
    public double compensation;

    // RPM tracking variables
    private ElapsedTime rpmTimer = new ElapsedTime();
    private int lastEncoder = 0;
    private double lastTimeSec = 0.0;
    private double test = 0.0;

    // smoothing (moving average)
    private static final int RPM_SMOOTH_SIZE = 5;
    private final double[] rpmBuffer = new double[RPM_SMOOTH_SIZE];
    private int rpmBufIndex = 0;
    private int rpmBufCount = 0;

    // Motor CPR and Gear Ratio
    private final double ticksPerRev = 26; 
    private final double gearRatio = 1.0;
    double compensatedPower;

    public ShootWheels(HardwareMap hardwareMap) {
        // Initialize hardware
        WHEEL_L = hardwareMap.get(DcMotor.class, "WHEEL_L");
        WHEEL_R = hardwareMap.get(DcMotor.class, "WHEEL_R");
        hood = hardwareMap.get(Servo.class, "H");

        myControlHubVoltageSensor = hardwareMap.get(VoltageSensor.class, "Control Hub");

        // Motor setup
        WHEEL_L.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL_L.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        WHEEL_L.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        WHEEL_L.setDirection(DcMotor.Direction.FORWARD);

        WHEEL_R.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        WHEEL_R.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        WHEEL_R.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        WHEEL_R.setDirection(DcMotor.Direction.FORWARD);

        timer = new ElapsedTime();
        timer.reset();
        rpmTimer.reset();
    }

    public void ShootWheelsOpMode(Gamepad gamepadTwo, Gamepad gamepad2Old, LimeLight limeLight, HardwareMap hardwareMap) {
        if (gamepadTwo.square && !gamepad2Old.square) {
            wheelOn = !wheelOn;
        }

        if (wheelOn) {
            distance = limeLight.distance;
            
            double idealVoltage = 13.50;
            double currentVoltage = myControlHubVoltageSensor.getVoltage();
            compensation = idealVoltage / currentVoltage;

            // Hood tuning
            if (gamepadTwo.dpad_left && !gamepad2Old.dpad_left) {
                pos += 0.01;
            }
            if (gamepadTwo.dpad_right && !gamepad2Old.dpad_right) {
                pos -= 0.01;
            }
            
            // Manual tuning overrides for hood and power
            if (gamepadTwo.dpad_up && !gamepad2Old.dpad_up) {
                targetPosition += 1;
            }
            if (gamepadTwo.dpad_down && !gamepad2Old.dpad_down) {
                targetPosition -= 1;
            }

            targetPosition = 0.654 + (-0.0888* distance) + Math.pow(0.036 * distance , 2);
             pos = 0.349 + (0.0157 * distance) + Math.pow(0.0361 * distance, 2);
            
            pos = Math.max(0.0, Math.min(1.0, pos));
            hood.setPosition(pos);

            compensatedPower = targetPosition * compensation;
            compensatedPower = Math.max(0.0, Math.min(1.0, compensatedPower));

            WHEEL_L.setPower(compensatedPower);
            WHEEL_R.setPower(compensatedPower);

        } else {
            WHEEL_L.setPower(0);
            WHEEL_R.setPower(0);
        }
    }

    /** Telemetry for debugging */
    public void Display_Telemetry(Telemetry telemetry) {
        telemetry.addData("Hood Position", hood.getPosition());
        telemetry.addData("Wheel % ", Math.round(WHEEL_L.getPower() * 100) * compensation + "%");
        telemetry.addData("Wheel PID Target", compensatedPower);
        telemetry.addData("DistGettingReturned", distance);
        telemetry.addData("ServoPOs", pos);
        telemetry.addData("RPM", currentRPM());
    }

    double currentRPM() {
        double now = rpmTimer.seconds();
        int enc = WHEEL_L.getCurrentPosition();
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
