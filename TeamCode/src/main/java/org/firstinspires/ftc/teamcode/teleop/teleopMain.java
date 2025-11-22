package org.firstinspires.ftc.teamcode.teleop;


import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.hardware.Flick;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.ShootWheels;
import org.firstinspires.ftc.teamcode.hardware.Wheels;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Sort;

import java.util.List;

@TeleOp(name = "Teleop Main") //this is the name that will appear on the Driver Station
public class teleopMain extends OpMode { //extends opMode imports the info the station needs from OpMode class


    Gamepad currentGamepadDrive = new Gamepad(); //Gamepad 1, of current inputs
    //Gamepad prevGamepadDrive = new Gamepad(); //Gamepad 1 last frame input, used for buttons (like a toggle)

    Gamepad currentGamepadCopilot = new Gamepad(); //Gamepad 2, of current inputs
    Gamepad prevGamepadCopilot = new Gamepad(); //Gamepad 2 last frame input, used for buttons (like a toggle)

    Wheels wheels; //Get the wheels, this is a secondary func in HardWare.
    Intake intake;

    Sort sort;
    public LimeLight limeLight; //Get the Limelight

    public Flick flick;

    ShootWheels shootWheels; //Get the outtake wheels, this is a secondary func in HardWare.
    @Override
    public void init()
    {
        //DO NOT RUN ANY MOVEMENT MOTORS

        //DEFINE ANY HARDWARE MAPS BELOW.

        //TODO - Uncomment
       wheels = new Wheels(hardwareMap); //new hardware map for wheels.
        intake = new Intake(hardwareMap);

        sort = new Sort(hardwareMap);

        flick = new Flick(hardwareMap);
        //TODO - Uncomment

        limeLight = new LimeLight(hardwareMap, telemetry); //Get the Limelight

        shootWheels = new ShootWheels(hardwareMap);




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
        //Define Gamepads //TODO - Uncomment
       currentGamepadDrive.copy(gamepad1);
       currentGamepadCopilot.copy(gamepad2);



        //This is for our last frame gamepads
//        prevGamepadDrive.copy(currentGamepadDrive);




        //Run the drive
        //TODO - DEFINE DIFFERENT PARTS OF THE ROBOT

        wheels.ManualDrive(currentGamepadDrive); //Drive code --DO NOT REMOVE
        intake.IntakeOpMode(currentGamepadCopilot);
        sort.SortOpMode(currentGamepadCopilot, prevGamepadCopilot, flick);
        flick.FlickOpMode(currentGamepadCopilot, prevGamepadCopilot);


        //Run the shoot

       shootWheels.ShootWheelsOpMode(currentGamepadCopilot, prevGamepadCopilot, limeLight);

          //Gets the copilot buttons and sends it to spin the motors

        //LIMELIGHTPULL
//
//        //TODO - Will need to be updated



        limeLight.LimeLightOpMode(telemetry); //Pull the Yaw for the AprilTag, and display.




        //TODO - STATE MACHINE GOES HERE.








        //update telem, for side classes


        //Do telem


        //TODO - Uncomment
        limeLight.Display_Telemetry(telemetry); //DISPLAY LIMELIGHT TELEM

        //TODO - Uncomment
        shootWheels.Display_Telemetry(telemetry);

        sort.SortTelem(telemetry);

        telemetry.update();

        prevGamepadCopilot.copy(currentGamepadCopilot);

    }

    //TODO - Add stop funct




}
