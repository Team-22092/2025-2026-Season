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

@Autonomous(name = "REDOnSkibAutoFRRRR")
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
        Pose2d initialPose =  new Pose2d(69, 11, Math.toRadians(-180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        Action MainAction = drive.actionBuilder(initialPose)


                .afterTime(4.5, new InstantAction(() ->  flick.flickthing.setPosition(0)))

                .afterTime(0, new InstantAction(() -> {

                    String c0 = limeLight.GetColors(0);
                    String c1 = limeLight.GetColors(1);

                    if (c0.equals("P") && c1.equals("G")) {
                        sort.sort.setPosition(0.150);
                    }
                    else if (c0.equals("P") && c1.equals("P")) {
                        sort.sort.setPosition(0.525);
                    }
                    else if (c0.equals("G") && c1.equals("P")) {
                        sort.sort.setPosition(0.9);
                    }

                }))

                .strafeToLinearHeading(new Vector2d(60, 11), Math.toRadians(-209))
                .afterTime(3, new InstantAction(() -> {
                    if (Objects.equals(limeLight.GetColors(0), "P")) {
                        sort.sort.setPosition(0.150);
                    } else {
                        sort.sort.setPosition(0.9);
                    }
                }))
                .afterTime(4, new InstantAction(() -> flick.flickthing.setPosition(0.57)))
                .afterTime(4.5, new InstantAction(() ->  flick.flickthing.setPosition(0.05)))

                .afterTime(5, new InstantAction(() -> {
                    if (Objects.equals(limeLight.GetColors(1), "P")) {
                        sort.sort.setPosition(0.525);
                    } else {
                        sort.sort.setPosition(0.9);
                    }
                }))
                .afterTime(6.0, new InstantAction(() -> flick.flickthing.setPosition(0.57)))
                .afterTime(6.5, new InstantAction(() ->  flick.flickthing.setPosition(0.05)))

                .afterTime(8, new InstantAction(() -> {
                    if (Objects.equals(limeLight.GetColors(2), "P") && Objects.equals(limeLight.GetColors(1), "P"))
                    {
                        sort.sort.setPosition(0.150);
                    }

                    else if (Objects.equals(limeLight.GetColors(2), "P")) {
                        sort.sort.setPosition(0.525);
                    }

                    else {
                        sort.sort.setPosition(0.9);
                    }
                }))

                .afterTime(9.0, new InstantAction(() -> flick.flickthing.setPosition(0.57)))
                .afterTime(9.5, new InstantAction(() ->  flick.flickthing.setPosition(0.05)))

                .waitSeconds(11)

                .afterTime(0, new InstantAction(sort::CarroselOn))
                  .afterTime(0, new InstantAction(intake::IntakeON))
//
                .strafeToLinearHeading(new Vector2d( 38, 11), Math.toRadians(90))


                .strafeTo(new Vector2d(38, 60), new TranslationalVelConstraint(20), new ProfileAccelConstraint(-20, 20))

                .strafeToLinearHeading(new Vector2d(60, 11), Math.toRadians(-208))
                .afterTime(0, new InstantAction(sort::CarroselOff))
                .afterTime(0, new InstantAction(intake::IntakeOFF))

                .afterTime(0.3, new InstantAction(() -> sort.sort.setPosition(0.15)))
                .afterTime(1.5, new InstantAction(() -> flick.flickthing.setPosition(0.57)))
                .afterTime(2, new InstantAction(() ->  flick.flickthing.setPosition(0.05)))

                .afterTime(3, new InstantAction(() -> sort.sort.setPosition(0.525)))
                .afterTime(4, new InstantAction(() -> flick.flickthing.setPosition(0.57)))
                .afterTime(4.5, new InstantAction(() ->  flick.flickthing.setPosition(0.05)))

                .afterTime(6, new InstantAction(() -> sort.sort.setPosition(0.9)))
                .afterTime(7, new InstantAction(() -> flick.flickthing.setPosition(0.57)))
                .afterTime(7.5, new InstantAction(() ->  flick.flickthing.setPosition(0.05)))

                .waitSeconds(8)
                .strafeToLinearHeading(new Vector2d( 12, 11), Math.toRadians(90))





//
//
////
//                // timed servo actions using InstantAction

//
//                .afterTime(9.0, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
//                .afterTime(9.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
////////
//                .afterTime(11, new InstantAction(sort::CarroselOn))
//                .afterTime(11, new InstantAction(intake::IntakeON))
//
//
//
//
//                .waitSeconds(11)
//
////
               // .strafeToLinearHeading(new Vector2d(90, -32), Math.toRadians(-90))

//
//                .strafeTo(new Vector2d(38, -60), new TranslationalVelConstraint(20), new ProfileAccelConstraint(-20, 20))
//
//                .strafeTo(new Vector2d(35, -32))
//
//
//                .strafeToLinearHeading(new Vector2d(62, -11), Math.toRadians(200.5))
//
//                .afterTime(0, new InstantAction(sort::CarroselOff))
//                .afterTime(0, new InstantAction(intake::IntakeOFF))
//
//
//
//

                .afterTime(0, new InstantAction(() -> sort.sort.setPosition(0.19)))
                .afterTime(1.5, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(2, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))

                .afterTime(3, new InstantAction(() -> sort.sort.setPosition(0.56)))
                .afterTime(4, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(4.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))

                .afterTime(6, new InstantAction(() -> sort.sort.setPosition(0.935)))
                .afterTime(7, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(7.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
//////
//                .waitSeconds(7.6)
//                .strafeTo(new Vector2d(35, -32))


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


        telemetry.addLine("READY! GOOD LUCK :)");

        telemetry.update();

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

                    if(sort.MODE) //@hack CHANGED CARROSELON, NEEDS TO BE FIXED.
                    {
                        sort.sort.setPosition(sortposup[0]);
                        if(sortposup[0] >= 1)
                        {
                            flipdir[0] = !flipdir[0];
                        }
                        if(sortposup[0] <= 0){
                            flipdir[0] = !flipdir[0];
                        }


                        if(flipdir[0])    sortposup[0] = (float) (sortposup[0] + 0.015);

                        if(!flipdir[0])    sortposup[0] = (float) (sortposup[0] - 0.015);


                    }




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
                    limeLight.LimeLightOpMode(telemetry, colorTest);
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
