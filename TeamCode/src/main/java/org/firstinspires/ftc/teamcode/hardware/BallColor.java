package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallColor {

    ColorSensor color;
    private Servo rgbLight;


    public BallColor(HardwareMap hardwareMap)
    {
        //TODO - DEFINE SERVOS
        color = hardwareMap.get(ColorSensor.class, "Color");
        rgbLight = hardwareMap.get(Servo.class, "rgbLight");

        ServoImplEx ex = (ServoImplEx) rgbLight;
        // PwmRange(minPulseWidthMicroseconds, maxPulseWidthMicroseconds)
        ex.setPwmRange(new PwmControl.PwmRange(500, 2500));


    }


    public void ColorOpMode(Telemetry telemetry)
    {

        int r = color.red();
        int g = color.green();
        int b = color.blue();


        boolean isGreen = g > r && g > b && g > 50;
        boolean isPurple = (r > 120 && b > 120);

        double pos = 0;

        if (isPurple) {
            pos = 0.625;
        }
        else if (isGreen) {
            pos = 0.50;
        }


        rgbLight.setPosition(pos);
    }




}





