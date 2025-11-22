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



@Autonomous(name = "OnSkibAutoFRRRR")
public class RedSideShoot extends LinearOpMode {


//    public class transfer implements Action
//    { private double beginTs = -1;
//
//        @Override
//        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
//
//            double t;
//            if (beginTs < 0) {
//                beginTs = com.acmerobotics.roadrunner.Actions.now();
//                t = 0;
//            } else {
//                t = com.acmerobotics.roadrunner.Actions.now() - beginTs;
//            }
//            if(t >= 3.6)
//            {
//                intake.Left.setPower(1);
//                intake.Right.setPower(1);
//                lift.LIFTTARGET = 0;
//                return false;
//            }
//            if(t >= 3.5)
//            {
//                lift.LIFTTARGET = 0;
//                intake.Left.setPower(1);
//                intake.Right.setPower(1);
//
//                //  return false;
//
//
//            }
//            if(t >= 2.30) {
//                intake.Flipup();
//                intake.Left.setPower(0);
//                intake.Right.setPower(0);
//                lift.LIFTTARGET = 1000;
//
//            }
//
//            else if (t >=1.70) {
//                //  extendo.OUTTARGET = 100;
//                intake.Left.setPower(-1);
//                intake.Right.setPower(1);
//
//            } else if (t >= 1.30){
//                intake.Left.setPower(0);
//                intake.Right.setPower(0);
//                extendo.OUTTARGET = 100;
//
//            } else if (t >= 1){
//
//                intake.Flipdown();//flipup
//            } else {
//                intake.Left.setPower(1);
//                intake.Right.setPower(-1);
//                extendo.OUTTARGET = 800;
//
//            }
//            return true;
//        }
//    }
//
//
//
//
//
//
//
//    public class Lastone implements Action
//    { private double beginTs = -1;
//
//        @Override
//        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
//
//            double t;
//            if (beginTs < 0) {
//                beginTs = com.acmerobotics.roadrunner.Actions.now();
//                t = 0;
//            } else {
//                t = com.acmerobotics.roadrunner.Actions.now() - beginTs;
//            }
//            if(t >= 2.9)
//            {
//
//                lift.LIFTTARGET = 0;
//                return false;
//            }
//            else if(t >= 2.30) {
//                intake.Flipup();
//                intake.Left.setPower(0);
//                intake.Right.setPower(0);
//                lift.LIFTTARGET = 1000;
//
//            }
//
//            else if (t >=1.70) {
//
//                intake.Left.setPower(-1);
//                intake.Right.setPower(1);
//
//            } else if (t >= 1.30){
//                intake.Left.setPower(0);
//                intake.Right.setPower(0);
//                extendo.setspeed(0.85f);
//                extendo.OUTTARGET = 100;
//
//            } else if (t >= 1){
//
//                intake.Left.setPower(1);
//                intake.Right.setPower(-1);
//                intake.Flipdown();//flipup
//            } else if( t >= 0.1)
//            {
//                intake.Left.setPower(1);
//                intake.Right.setPower(-1);
//                extendo.setspeed(0.85f);
//                extendo.OUTTARGET = 1300;
//
//            }
//
//
//            else {
//                intake.Left.setPower(1);
//                intake.Right.setPower(-1);
//            }
//            return true;
//        }
//    }
//




    @Override
    public void runOpMode() {
//        bucket = new Bucket(hardwareMap);
//        grabber = new Grabber(hardwareMap);
//        intake = new Intake(hardwareMap);
//        lift = new Lift(hardwareMap, intake);
//        flicker = new TheThirdLeg(hardwareMap);
//        extendo = new Extendo(hardwareMap, true);
        // TODO - MAKE SURE TO UPDATE INITIAL POSITION
        Pose2d initialPose =  new Pose2d(69, 11, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        Action MainAction = drive.actionBuilder(initialPose)




                .strafeToLinearHeading(new Vector2d(40,11),Math.toRadians(180))
                .waitSeconds(5)

                .strafeToLinearHeading(new Vector2d(65,11),Math.toRadians(160))





                .build();

        telemetry.addLine("READY! GOOD LUCK :)");
        telemetry.update();
       // grabber.Startclose();



        waitForStart();

        Actions.runBlocking(new ParallelAction(

                MainAction

        ));

    }




}