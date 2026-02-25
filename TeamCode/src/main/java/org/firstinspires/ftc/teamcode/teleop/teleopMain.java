package org.firstinspires.ftc.teamcode.teleop;


import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.BallColor;
import org.firstinspires.ftc.teamcode.hardware.ColorTest;
import org.firstinspires.ftc.teamcode.hardware.Flick;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.SAVESHOTS;
import org.firstinspires.ftc.teamcode.hardware.ShootWheels;
import org.firstinspires.ftc.teamcode.hardware.Wheels;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.Sort;

import java.util.List;

@TeleOp(name = "Teleop Main") //this is the name that will appear on the Driver Station
public class teleopMain extends OpMode { //extends opMode imports the info the station needs from OpMode class
    MecanumDrive drive;
    DcMotor turret;

    // --- HARDWARE CONSTANTS ---
    static final double MOTOR_TICKS_PER_REV = 751.8;
    static final double GEAR_RATIO = 90.0 / 55.0;
    static final double TICKS_PER_DEGREE = (MOTOR_TICKS_PER_REV * GEAR_RATIO) / 360.0;

    static final int TURRET_MAX_TICKS = 550; // Total travel allowed
    static final double TICKS_AT_FORWARD = 275.0;


    static final Vector2d TARGET_POS = new Vector2d(-67, 30);//-60, 50 for RIGHT, -60, -50 for LEFT


    Gamepad currentGamepadDrive = new Gamepad(); //Gamepad 1, of current inputs
    //Gamepad prevGamepadDrive = new Gamepad(); //Gamepad 1 last frame input, used for buttons (like a toggle)

    Gamepad currentGamepadCopilot = new Gamepad(); //Gamepad 2, of current inputs
    Gamepad prevGamepadCopilot = new Gamepad(); //Gamepad 2 last frame input, used for buttons (like a toggle)

    Wheels wheels; //Get the wheels, this is a secondary func in HardWare.
    Intake intake;

    SAVESHOTS saveshots;

    BallColor ballColor;

    ColorTest colorTest;



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


        saveshots = new SAVESHOTS(hardwareMap);

        ballColor = new BallColor(hardwareMap);

        colorTest = new ColorTest(hardwareMap, telemetry);
        shootWheels = new ShootWheels(hardwareMap);


        sort = new Sort(hardwareMap);

        flick = new Flick(hardwareMap, shootWheels);
        //TODO - Uncomment

        limeLight = new LimeLight(hardwareMap, telemetry); //Get the Limelight



        turret = hardwareMap.get(DcMotor.class, "T");
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // STARTING CONDITION: Turret must be at the 0 position (Far Right) when you hit Init

        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set your starting position on the field (X, Y, Heading in Radians)
        drive = new MecanumDrive(hardwareMap, new Pose2d(69, 11, Math.toRadians(180)));


        for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

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



        ballColor.pos = limeLight.color; //handoff for the color of hte ball
        telemetry.addData("COLORRETURN",  ballColor.pos);
        //Run the drive
        //TODO - DEFINE DIFFERENT PARTS OF THE ROBOT

        wheels.ManualDrive(currentGamepadDrive); //Drive code --DO NOT REMOVE
        intake.IntakeOpMode(currentGamepadCopilot);
        sort.SortOpMode(currentGamepadCopilot, prevGamepadCopilot, flick);
        flick.FlickOpMode(currentGamepadCopilot, prevGamepadCopilot);

       shootWheels.ShootWheelsOpMode(currentGamepadCopilot, prevGamepadCopilot, limeLight, hardwareMap);


       colorTest.COLOR();


          //Gets the copilot buttons and sends it to spin the motors

        //LIMELIGHTPULL
//
//        //TODO - Will need to be updated



        limeLight.LimeLightOpMode(telemetry, colorTest); //Pull the Yaw for the AprilTag, and display.


        saveshots.IntakeOpMode(gamepad2, shootWheels);



        //TODO - STATE MACHINE GOES HERE.


        ballColor.ColorOpMode(telemetry);





        //update telem, for side classes


        //Do telem


        //TODO - Uncomment
        limeLight.Display_Telemetry(telemetry); //DISPLAY LIMELIGHT TELEM

        //TODO - Uncomment
        shootWheels.Display_Telemetry(telemetry);

        sort.SortTelem(telemetry);

        telemetry.update();

        prevGamepadCopilot.copy(currentGamepadCopilot);

        // 1. Update Deadwheel Localization
        drive.updatePoseEstimate();
        Pose2d currentPose = drive.localizer.getPose(); // RoadRunner 1.0 syntax

        // 2. Triangulate the Vector to the Goal
        // This math calculates the angle from the ROBOT (x,y) to the GOAL (x,y)
        double dx = TARGET_POS.x - currentPose.position.x;
        double dy = TARGET_POS.y - currentPose.position.y;

        // This is the absolute angle on the field (0 is usually pointing toward Red Backdrop)
        double absoluteAngleDeg = Math.toDegrees(Math.atan2(dy, dx));

        // 3. Convert Field Angle to Robot-Relative Angle
        double robotHeadingDeg = Math.toDegrees(currentPose.heading.toDouble());
        double relativeAngle = absoluteAngleDeg - robotHeadingDeg;

        // 4. Normalize to -180 to 180 (shortest path)
        while (relativeAngle > 180) relativeAngle -= 360;
        while (relativeAngle <= -180) relativeAngle += 360;

        // 5. Convert to Ticks with your Far-Right Offset
        // Logic: (Angle * Ticks/Degree) + your "Center" point
        double targetTicks = (relativeAngle * TICKS_PER_DEGREE) + TICKS_AT_FORWARD;

        // 6. Safety Clamp (Prevents the turret from breaking itself)
        if (targetTicks < 0) targetTicks = 0;
        if (targetTicks > TURRET_MAX_TICKS) targetTicks = TURRET_MAX_TICKS;
        if(LimeLight.locktarget == false)
        {
            // 7. Update Motor
            turret.setTargetPosition((int) targetTicks);
            turret.setPower(1.0); // Full speed to track accurately while moving

        }

        telemetry.addData("DRIVE POS - ", drive.localizer.getPose());



    }

    //TODO - Add stop funct




}
