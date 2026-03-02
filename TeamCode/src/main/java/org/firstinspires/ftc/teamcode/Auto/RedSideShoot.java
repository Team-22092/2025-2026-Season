package org.firstinspires.ftc.teamcode.Auto;

import androidx.annotation.NonNull;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.hardware.ColorTest;
import org.firstinspires.ftc.teamcode.hardware.Flick;
import org.firstinspires.ftc.teamcode.hardware.Intake;
import org.firstinspires.ftc.teamcode.hardware.LimeLight;
import org.firstinspires.ftc.teamcode.hardware.ShootWheels;
import org.firstinspires.ftc.teamcode.hardware.Sort;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

@Autonomous(name = "REDOnSkibAutoFRRRR")
public class RedSideShoot extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        // ------------------------------
        // Hardware initialization
        // ------------------------------
        ShootWheels shootWheels = new ShootWheels(hardwareMap);
        Flick flick = new Flick(hardwareMap, shootWheels); // Flick uses the shooter internally
        LimeLight limeLight = new LimeLight(hardwareMap, telemetry);
        limeLight.centerTurretForAuto();
        ColorTest colorTest = new ColorTest(hardwareMap, telemetry);
        Intake intake = new Intake(hardwareMap);
        Sort sort = new Sort(hardwareMap);

        // ------------------------------
        // Initial pose and drivetrain
        // ------------------------------
        Pose2d initialPose = new Pose2d(69, 11, Math.toRadians(-180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        // ------------------------------
        // Main Action timeline
        // ------------------------------
        Action MainAction = drive.actionBuilder(initialPose)
                .afterTime(0, () -> {
                    // read initial colors if needed
                    String c0 = limeLight.GetColors(0);
                    String c1 = limeLight.GetColors(1);
                })
                .strafeToLinearHeading(new Vector2d(60, 13), Math.toRadians(-205))

                // start first auto-burst (all three disks)
                .afterTime(3.6, flick::startAutoBurstAuto)

                // Carousel + intake

                .afterTime(7, intake::IntakeON)
                .waitSeconds(7)

                .strafeToLinearHeading(new Vector2d(38, 20), Math.toRadians(90))
                .strafeTo(new Vector2d(38, 60), new TranslationalVelConstraint(20),
                        new ProfileAccelConstraint(-20, 20))
                .strafeTo(new Vector2d(35, 32))
                .strafeToLinearHeading(new Vector2d(62, 15), Math.toRadians(-196))

                // Turn off carousel and intake, reset sorter

                .afterTime(0, intake::IntakeOFF)
                .afterTime(0, () -> sort.sort.setPosition(0.19))

                // optional second auto-burst later
                .afterTime(1.5, flick::startAutoBurstAuto)

                .waitSeconds(7.6)
                .strafeToLinearHeading(new Vector2d(35, 32), Math.toRadians(180))
                .waitSeconds(15)
                .build();

        // ------------------------------
        // Pre-start loop (color detection & telemetry)
        // ------------------------------
        while (!isStarted() && !isStopRequested()) {
            limeLight.detectAndSavePattern();
            List<String> savedPattern = LimeLight.getSavedPattern();
            flick.setPattern(savedPattern);

            String c0 = savedPattern.get(0);
            String c1 = savedPattern.get(1);

            if (Objects.equals(c0, "P") && Objects.equals(c1, "G")) {
                telemetry.addData("FIRST ONE", "P");
            } else if (Objects.equals(c0, "P") && Objects.equals(c1, "P")) {
                telemetry.addData("FIRST ONE", "P");
            } else if (Objects.equals(c0, "G") && Objects.equals(c1, "P")) {
                telemetry.addData("FIRST ONE", "G");
            }

            String code = savedPattern.get(0) + savedPattern.get(1) + savedPattern.get(2);
            telemetry.addData("Detected Code", code);
            telemetry.addData("Saved Pattern", "%s-%s-%s", savedPattern.get(0), savedPattern.get(1), savedPattern.get(2));
            telemetry.addData("Status", "Waiting for Start - Alliance: Blue");
            telemetry.addLine("READY! GOOD LUCK :)");
            telemetry.update();
            idle();
        }

        telemetry.addLine("READY! GOOD LUCK :)");
        telemetry.update();
        waitForStart();
        if (isStopRequested()) return;

        flick.setPattern(LimeLight.getSavedPattern());

        // ------------------------------
        // Shooter thread
        // ------------------------------
        final AtomicBoolean keepShooting = new AtomicBoolean(true);
        Thread shooterThread = new Thread(() -> {
            while (opModeIsActive() && keepShooting.get()) {
                try {
                    shootWheels.AutoSHOOT(limeLight, hardwareMap, telemetry);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
        }, "ShooterThread");
        shooterThread.start();

        // ------------------------------
        // Intake thread
        // ------------------------------
        final AtomicBoolean INTAKE = new AtomicBoolean(true);
        Thread INTAKEThread = new Thread(() -> {
            while (opModeIsActive() && INTAKE.get()) {
                try {
                    if (intake.IntakeON) {
                        intake.spin_input.setPower(1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
        }, "intake");
        INTAKEThread.start();

        // ------------------------------
        // LimeLight updater thread
        // ------------------------------
        final AtomicBoolean keepLime = new AtomicBoolean(true);
        Thread limeThread = new Thread(() -> {
            while (opModeIsActive() && keepLime.get()) {
                try {
                    limeLight.LimeLightOpMode(telemetry, colorTest, "B");
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
        }, "LimeThread");
        limeThread.start();

        // ------------------------------
        // Flick updater thread (runs state machine)
        // ------------------------------
        final AtomicBoolean keepFlick = new AtomicBoolean(true);
        Thread flickThread = new Thread(() -> {
            while (opModeIsActive() && keepFlick.get()) {
                try {
                    flick.updateAutoBurst();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ignored) {}
            }
        }, "FlickThread");
        flickThread.start();

        // ------------------------------
        // Run main action timeline
        // ------------------------------
        Actions.runBlocking(new ParallelAction(MainAction));

        // ------------------------------
        // Cleanup threads
        // ------------------------------
        keepShooting.set(false);
        shooterThread.interrupt();
        try { shooterThread.join(500); } catch (InterruptedException ignored) {}

        keepLime.set(false);
        limeThread.interrupt();
        try { limeThread.join(500); } catch (InterruptedException ignored) {}

        INTAKE.set(false);
        try { INTAKEThread.join(200); } catch (InterruptedException ignored) {}

        keepFlick.set(false);
        flickThread.interrupt();
        try { flickThread.join(500); } catch (InterruptedException ignored) {}

        telemetry.addLine("AUTO COMPLETE");
        telemetry.update();
    }
}