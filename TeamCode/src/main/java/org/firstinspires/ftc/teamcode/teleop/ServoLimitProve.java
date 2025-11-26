package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.Flick;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.ShootWheels;
import org.firstinspires.ftc.teamcode.hardware.Wheels;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Sort;

import java.util.List;

@TeleOp(name = "Servo Test") //this is the name that will appear on the Driver Station
public class ServoLimitProve extends OpMode { //extends opMode imports the info the station needs from OpMode class

    private Servo hood;

    Gamepad currentGamepadCopilot = new Gamepad(); //Gamepad 2, of current inputs
    Gamepad prevGamepadCopilot = new Gamepad(); //Gamepad 2 last frame input, used for buttons (like a toggle)



    //ShootWheels shootWheels; //Get the outtake wheels, this is a secondary func in HardWare.
    @Override
    public void init()
    {
      //ght
        hood = hardwareMap.get(Servo.class, "H");

        //Performance optimization by letting all hardware use bulk Caching.
        List<LynxModule> all_hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : all_hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

//Not needed Right now, but if we need something to move when the robot starts, we add it here:
//    @Override
//    public void Start()
//    {
//
//    }
    Telemetry telemetry;
    double pos = 0.70f;
    @Override
    public void loop() //Main loop
    {
//        //Define Gamepads //TODO - Uncomment
//        currentGamepadDrive.copy(gamepad1);
//        currentGamepadCopilot.copy(gamepad2);
        if(currentGamepadCopilot.dpad_left && !prevGamepadCopilot.dpad_left){
            pos+=0.01;
        }
        if(currentGamepadCopilot.dpad_right && !prevGamepadCopilot.dpad_right)
        {
            pos -= 0.01;
        }

        //    pos = 0.00075*Math.pow(distance, 2) + -0.03851*distance + 0.74990;
        //pos = 0.00918*Math.pow(distance, 2) + -0.06259*distance + 0.75987;

//        pos = Math.max(0.50, Math.min(1.0, pos));
        hood.setPosition(pos);



        //This is for our last frame gamepads
//        prevGamepadDrive.copy(currentGamepadDrive);




        //Run the drive
        //TODO - DEFINE DIFFERENT PARTS OF THE ROBOT
//
//        wheels.ManualDrive(currentGamepadDrive); //Drive code --DO NOT REMOVE
//        intake.IntakeOpMode(currentGamepadCopilot);
//        sort.SortOpMode(currentGamepadCopilot, prevGamepadCopilot, flick);
//        flick.FlickOpMode(currentGamepadCopilot, prevGamepadCopilot);
//
//
//        //Run the shoot
//
//        shootWheels.ShootWheelsOpMode(currentGamepadCopilot, prevGamepadCopilot, limeLight);
//
//        //Gets the copilot buttons and sends it to spin the motors
//
//        //LIMELIGHTPULL
////
////        //TODO - Will need to be updated
//
//
//
//        limeLight.LimeLightOpMode(telemetry); //Pull the Yaw for the AprilTag, and display.




        //TODO - STATE MACHINE GOES HERE.








        //update telem, for side classes

        telemetry.addData("SHOOTROT", String.valueOf(pos));
        //Do telem


        //TODO - Uncomment
//        limeLight.Display_Telemetry(telemetry); //DISPLAY LIMELIGHT TELEM
//
//        //TODO - Uncomment
//        shootWheels.Display_Telemetry(telemetry);
//
//        sort.SortTelem(telemetry);

        telemetry.update();

       // prevGamepadCopilot.copy(currentGamepadCopilot);

    }

    //TODO - Add stop funct




}
