package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.Wheels;

import java.util.List;

@TeleOp(name = "Teleop Main") //this is the name that will appear on the Driver Station
public class teleopMain extends OpMode { //extends opMode imports the info the station needs from OpMode class


    Gamepad currentGamepadDrive = new Gamepad(); //Gamepad 1, of current inputs
    //Gamepad prevGamepadDrive = new Gamepad(); //Gamepad 1 last frame input, used for buttons (like a toggle)

    Gamepad currentGamepadCopilot = new Gamepad(); //Gamepad 2, of current inputs
    //Gamepad prevGamepadCopilot = new Gamepad(); //Gamepad 2 last frame input, used for buttons (like a toggle)

    Wheels wheels; //Get the wheels, this is a secondary func in HardWare.
    LimeLight limeLight;

    @Override
    public void init()
    {
        //DO NOT RUN ANY MOVEMENT MOTORS

        //DEFINE ANY HARDWARE MAPS BELOW.

        wheels = new Wheels(hardwareMap); //new hardware map for wheels.

        limeLight = new LimeLight(hardwareMap, telemetry); //Get the Limelight






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


    @Override
    public void loop() //Main loop
    {
        //Define Gamepads
       currentGamepadDrive.copy(gamepad1);
       currentGamepadCopilot.copy(gamepad2);



        //This is for our last frame gamepads
//        prevGamepadDrive.copy(currentGamepadDrive);
//        prevGamepadCopilot.copy(currentGamepadCopilot);



        //Run the drive
        //TODO - DEFINE DIFFERENT PARTS OF THE ROBOT
        wheels.ManualDrive(currentGamepadDrive);


        //LIMELIGHTPULL

        limeLight.LimeLightOpMode(telemetry);




        //TODO - STATE MACHINE GOES HERE.








        //update telem, for side classes


        //Do telem

        limeLight.Display_Telemetry(telemetry); //Limelight Telem


        telemetry.update();



    }






}
