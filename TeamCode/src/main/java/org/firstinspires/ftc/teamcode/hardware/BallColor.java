package org.firstinspires.ftc.teamcode.hardware;


import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class BallColor { //This class detects the ball color loaded into the shoot slot

     private ColorSensor color; //Pull the current colorsensor
    private Servo rgbLight; //The light is controlled with PWM signals, so we pretend is a servo.


    public BallColor(HardwareMap hardwareMap) // This is called in init.
    {
        //TODO - DEFINE SERVOS
        color = hardwareMap.get(ColorSensor.class, "Color"); //THIS IS FOR THE COLOR SENSOR V3.
        rgbLight = hardwareMap.get(Servo.class, "rgbLight");// @note We Want THIS TO BE A SERVO, TO CONTROL WITH PWM

        ServoImplEx ex = (ServoImplEx) rgbLight; //set it to a servo EX, this allows more control over the servo, so we can edit pwm
        // PwmRange(minPulseWidthMicroseconds, maxPulseWidthMicroseconds)
        ex.setPwmRange(new PwmControl.PwmRange(500, 2500)); //set the range between the lowest and brightest colors.
        // @img(https://chatgpt.com/backend-api/estuary/content?id=file_00000000702871fdabf001a30d986557&ts=491526&p=fs&cid=1&sig=cf44e43f6b1ad0570d9fa6cee8d38b82319b59bb724801896ffae4d3adc167cd&v=0)


    }


    public double pos = 0.3;; //set the starting pos, white. (0.9) the pwd is between 0.3-0.9

    public void ColorOpMode(Telemetry telemetry) // pull telem for the future
    {
        //convert the r - g - b vals into saved numbers
        int r = color.red();
        int g = color.green();
        int b = color.blue();



        rgbLight.setPosition(pos);

    }




}





