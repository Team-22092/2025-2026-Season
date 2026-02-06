package org.firstinspires.ftc.teamcode.TESTBENCH;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.Wheels;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import java.util.List;

//CREDITS
//A lot of this code isn't mine, and has been created by a lot of awesome people.
//https://medium.com/%40vikramaditya.nishant/how-to-make-a-zero-drift-ftc-localizer-with-kalman-filters-911807e0916d
//https://ftc-docs.firstinspires.org/en/latest/programming_resources/imu/imu.html
//https://docs.limelightvision.io/docs/docs-limelight/apis/ftc-programming
//https://www.youtube.com/watch?v=wA1cVPGm9lY
//https://docs.limelightvision.io/docs/docs-limelight/getting-started/limelight-3a#for-ftc-1
//https://docs.limelightvision.io/docs/docs-limelight/pipeline-apriltag/apriltag-robot-localization-megatag2

@TeleOp(name = "Full Field Localization TEST")
public class FullFieldLocalization extends OpMode {
    //side functions
    Gamepad currentGamepadDrive = new Gamepad();
    Wheels wheels;
    MecanumDrive drive;
    Limelight3A limelight;


    //vars
    Pose3D botpose;


    @Override
    public void init() {
        //DEADWHEELS
        drive = new MecanumDrive(
                hardwareMap,
                new Pose2d(69, 11, Math.toRadians(180)) // Starting from 0,0,0 (roadrunner cords) // @warn set this to where you end in auto.
        );


        wheels = new Wheels(hardwareMap);


        //limelight settings - from the docs ig
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start(); // This tells Limelight to start looking

        List<LynxModule> all_hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : all_hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    @Override
    public void loop() {
        //test limelight.pipelineSwitch(0);
        limelight.pipelineSwitch(0);



        //DEADWHEELS
        double y = -currentGamepadDrive.left_stick_y; //forward
        double x = -currentGamepadDrive.left_stick_x; //strafe
        double rx = -currentGamepadDrive.right_stick_x; //turn
        drive.setDrivePowers(
                new PoseVelocity2d(
                        new Vector2d(y, x), //y needs slot one, x needs slot 2
                        rx
                )
        );
        //PoseVelocity2d vel = drive.updatePoseEstimate(); Can be useful for shooting while moving.
        Pose2d pose = drive.localizer.getPose();

        //check limelight:

        // --- telem ---
        telemetry.addLine("DEADWHEEL (odometry) pose:");
        telemetry.addData("X", "%.2f", pose.position.x);
        telemetry.addData("Y", "%.2f", pose.position.y);
        telemetry.addData("Heading (deg)", "%.2f", Math.toDegrees(pose.heading.toDouble()));

        //--- telem for limelight ---
        //we call it
        limelighttest(telemetry);


        //--- telem for Kalman ---
        telemetry.addLine("Kalman Filter");
        telemetry.addData("X", "%.2f", Kalman(pose.position.x, botpose.getPosition().x));
        telemetry.addData("Y", "%.2f", Kalman(pose.position.y, botpose.getPosition().y));
        telemetry.addData("Heading (deg)", "%.2f",Kalman(Math.toDegrees(pose.heading.toDouble()), Math.toDegrees(botpose.getOrientation().getYaw())));



        currentGamepadDrive.copy(gamepad1);
        wheels.ManualDrive(currentGamepadDrive);
        // limeLight.Display_Telemetry(telemetry);
        telemetry.update();
    }



    public void limelighttest(Telemetry telemetry){

        YawPitchRollAngles orientation = drive.lazyImu.get().getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw(AngleUnit.DEGREES));
        LLResult result = limelight.getLatestResult();
        if (result != null) {
            if (result.isValid()) {
                botpose = result.getBotpose_MT2();
                // Use botpose data
                telemetry.addLine("LIMELIGHT (camera) pose:");
                telemetry.addData("X", "%.2f", botpose.getPosition().x);
                telemetry.addData("Y", "%.2f", botpose.getPosition().y);
            }
        } else {
            telemetry.addLine("No valid result");
        }

    }

    double x = 0;
    double p = 1;
    final double q = 0.02;
    final double r = 0.5;
    public double Kalman(double prediction, double measurement) {


        x = prediction;
        p += q;

        // update
        double k = p / (p + r);
        x = x + k * (measurement - x);
        p = (1 - k) * p;

        return x;
    }




}
