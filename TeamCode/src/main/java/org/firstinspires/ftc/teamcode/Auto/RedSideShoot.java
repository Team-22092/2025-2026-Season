package org.firstinspires.ftc.teamcode.Auto;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Arclength;
import com.acmerobotics.roadrunner.CompositeAccelConstraint;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Pose2dDual;
import com.acmerobotics.roadrunner.PosePath;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.ColorTest;
import org.firstinspires.ftc.teamcode.hardware.Flick;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.ShootWheels;
import org.firstinspires.ftc.teamcode.hardware.Sort;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Autonomous(name = "OnSkibAutoFRRRR")
public class RedSideShoot extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        // init shooter
        ShootWheels shootWheels = new ShootWheels(hardwareMap);
        Flick flick = new Flick(hardwareMap);

        LimeLight limeLight = new LimeLight(hardwareMap, telemetry);
        final float[] sortposup = {0};
        final boolean[] flipdir = new boolean[1];


        ColorTest colorTest = new ColorTest(hardwareMap, telemetry);

        Intake intake = new Intake(hardwareMap);

        Sort sort = new Sort(hardwareMap);



        // TODO - MAKE SURE TO UPDATE INITIAL POSITION
        Pose2d initialPose =  new Pose2d(69, 11, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        Action MainAction = drive.actionBuilder(initialPose)


                .afterTime(0, new InstantAction(() -> {

                    String c0 = limeLight.GetColors(0);
                    String c1 = limeLight.GetColors(1);

                    if (c0.equals("P") && c1.equals("G")) {
                        sort.sort.setPosition(0.19);
                    }
                    else if (c0.equals("P") && c1.equals("P")) {
                        sort.sort.setPosition(0.56);
                    }
                    else if (c0.equals("G") && c1.equals("P")) {
                        sort.sort.setPosition(0.935);
                    }

                }))


                .strafeTo(new Vector2d(60, 11))


//  private final double[] OUTTAKEPOS = {0.9, 0.525, 0.150, 0.525};
                .afterTime(0, new InstantAction(() -> sort.sort.setPosition(0.9)))
                .afterTime(0.5, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(0.8, new InstantAction(() ->  flick.flickthing.setPosition(0.2)))


//////
                .waitSeconds(7.6)
                .strafeTo(new Vector2d(35, 32))




//
//
//
//
//                .strafeToLinearHeading(new Vector2d(11, 32), Math.toRadians(90))
//
//                .strafeTo(new Vector2d(11, 45))
//
//                .strafeTo(new Vector2d(11, 32))
//
//                .strafeToLinearHeading(new Vector2d(60, 11), Math.toRadians(-198))
////
////
//////
//////
////
////                .waitSeconds(15)
////                .lineToX(10)
                .build();

        while (!isStarted() && !isStopRequested()) {

            if(Objects.equals(limeLight.GetColors(0), "P") && Objects.equals(limeLight.GetColors(1), "G"))
            {
               // sort.sort.setPosition(0.19);
                telemetry.addData("FIRST ONE", "P");

            }
            else if(Objects.equals(limeLight.GetColors(0), "P") && Objects.equals(limeLight.GetColors(1), "P"))
            {
                telemetry.addData("FIRST ONE", "P");
               // sort.sort.setPosition(0.56);
            }
            else if(Objects.equals(limeLight.GetColors(0), "G") && Objects.equals(limeLight.GetColors(1), "P")) {
              //  sort.sort.setPosition(0.935);
                telemetry.addData("FIRST ONE", "G");
            }

            telemetry.addData("Status", "Waiting for Start - Alliance: Red");

            telemetry.addLine("READY! GOOD LUCK :)");

            telemetry.update();
            idle(); // Important to yield the processor to other processes
        }



        waitForStart();
        if (isStopRequested()) return;

        // --- Shooter thread (unchanged) ---
        final AtomicBoolean keepShooting = new AtomicBoolean(true);
        Thread shooterThread = new Thread(() -> {
            while (opModeIsActive() && keepShooting.get()) {
                try {
                    shootWheels.AutoSHOOT(limeLight, hardwareMap, telemetry);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try { Thread.sleep(20); } catch (InterruptedException ignored) { break; }
            }
            // optional final stop

        }, "ShooterThread");
        shooterThread.start();








        final AtomicBoolean SORT = new AtomicBoolean(true);
        Thread SORTThread = new Thread(() -> {
            while (opModeIsActive() && SORT.get()) {
                try {

//                    if(sort.carroselOn) //@hack CHANGED CARROSELON, NEEDS TO BE FIXED.
//                    {
//                        sort.sort.setPosition(sortposup[0]);
//                        if(sortposup[0] >= 1)
//                        {
//                            flipdir[0] = !flipdir[0];
//                        }
//                        if(sortposup[0] <= 0){
//                            flipdir[0] = !flipdir[0];
//                        }
//
//
//                        if(flipdir[0])    sortposup[0] = (float) (sortposup[0] + 0.015);
//
//                        if(!flipdir[0])    sortposup[0] = (float) (sortposup[0] - 0.015);
//
//
//                    }




                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try { Thread.sleep(20); } catch (InterruptedException ignored) { break; }
            }
            // optional final stop

        }, "sort");

        SORTThread.start();




        final AtomicBoolean INTAKE = new AtomicBoolean(true);
        Thread INTAKEThread = new Thread(() -> {
            while (opModeIsActive() && INTAKE.get()) {
                try {

                    if(intake.IntakeON)
                    {
                        intake.spin_input.setPower(1);
                      //  intake.intakepart.setPower(1);

                    }




                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try { Thread.sleep(20); } catch (InterruptedException ignored) { break; }
            }
            // optional final stop

        }, "intake");

        INTAKEThread.start();

























        // --- LimeLight thread: continuously call your limelight updater ---
        final AtomicBoolean keepLime = new AtomicBoolean(true);
        Thread limeThread = new Thread(() -> {
            while (opModeIsActive() && keepLime.get()) {
                try {
                    // call your method that populates telemetry / reads yaw
                    limeLight.LimeLightOpMode(telemetry,colorTest);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                // ~50Hz update rate (adjust if you want faster/slower)
                try { Thread.sleep(20); } catch (InterruptedException ignored) { break; }
            }
        }, "LimeThread");
        limeThread.start();




        // --- Run the main action (blocks until it finishes) ---
        Actions.runBlocking(new ParallelAction(MainAction));

        // --- After path completes: stop threads and cleanup ---
        keepShooting.set(false);
        shooterThread.interrupt();


        try { shooterThread.join(500); } catch (InterruptedException ignored) {}

        keepLime.set(false);
        limeThread.interrupt();
        try { limeThread.join(500); } catch (InterruptedException ignored) {}


        SORT.set(false);
        try { SORTThread.interrupt(); SORTThread.join(500); } catch (Exception ignored) {}


        INTAKE.set(false);
        try { SORTThread.interrupt(); SORTThread.join(500); } catch (Exception ignored) {}

        telemetry.addLine("AUTO COMPLETE");

        telemetry.update();
    }
}
