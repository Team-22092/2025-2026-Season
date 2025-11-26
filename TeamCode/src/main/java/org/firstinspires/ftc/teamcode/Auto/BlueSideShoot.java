package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.Flick;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.ShootWheels;
import org.firstinspires.ftc.teamcode.hardware.Sort;

import java.util.concurrent.atomic.AtomicBoolean;

@Autonomous(name = "BLUEOnSkibAutoFRRRR")
public class BlueSideShoot extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // init shooter
        ShootWheels shootWheels = new ShootWheels(hardwareMap);
        Flick flick = new Flick(hardwareMap);

        LimeLight limeLight = new LimeLight(hardwareMap, telemetry);

        Sort sort = new Sort(hardwareMap);

        // TODO - MAKE SURE TO UPDATE INITIAL POSITION
        Pose2d initialPose =  new Pose2d(69, 11, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        Action MainAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(60,11), Math.toRadians(-158))

                // timed servo actions using InstantAction
                .afterTime(4.0, new InstantAction(() -> sort.sort.setPosition(0.19)))
                .afterTime(5.0, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(5.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))

                .afterTime(7.0, new InstantAction(() -> sort.sort.setPosition(0.56)))
                .afterTime(8.0, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(8.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))

                .afterTime(10.0, new InstantAction(() -> sort.sort.setPosition(0.935)))
                .afterTime(11.0, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
                .afterTime(11.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))

                .waitSeconds(15)
                .lineToX(10)
                //.strafeToLinearHeading(new Vector2d(50,11), Math.toRadians(145))
                .build();

        telemetry.addLine("READY! GOOD LUCK :)");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        // --- Shooter thread (unchanged) ---
        final AtomicBoolean keepShooting = new AtomicBoolean(true);
        Thread shooterThread = new Thread(() -> {
            while (opModeIsActive() && keepShooting.get()) {
                try {
                    shootWheels.AutoSHOOT(limeLight);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try { Thread.sleep(20); } catch (InterruptedException ignored) { break; }
            }
            // optional final stop

        }, "ShooterThread");
        shooterThread.start();

        // --- LimeLight thread: continuously call your limelight updater ---
        final AtomicBoolean keepLime = new AtomicBoolean(true);
        Thread limeThread = new Thread(() -> {
            while (opModeIsActive() && keepLime.get()) {
                try {
                    // call your method that populates telemetry / reads yaw
                    limeLight.LimeLightOpMode(telemetry);
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

        telemetry.addLine("AUTO COMPLETE");
        telemetry.update();
    }
}
