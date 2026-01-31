package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();
        new Pose2d(69, 11, Math.toRadians(-180));
        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(69, 11, -180))
                .strafeToLinearHeading(new Vector2d(60, 11), Math.toRadians(-203))

                .strafeToLinearHeading(new Vector2d( 38, 11), Math.toRadians(90))


                .strafeTo(new Vector2d(38, 60))

                .strafeToLinearHeading(new Vector2d(60, 11), Math.toRadians(-203))

                .strafeToLinearHeading(new Vector2d( 12, 11), Math.toRadians(90))


                .strafeToLinearHeading(new Vector2d(60, 11), Math.toRadians(-203))



//
//
////
//                // timed servo actions using InstantAction
//                .afterTime(3, new InstantAction(() -> {
//                    if (Objects.equals(limeLight.GetColors(0), "P")) {
//                        sort.sort.setPosition(0.19);
//                    } else {
//                        sort.sort.setPosition(0.935);
//                    }
//                }))
//                .afterTime(3.5, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
//                .afterTime(4, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
//
//                .afterTime(5, new InstantAction(() -> {
//                    if (Objects.equals(limeLight.GetColors(1), "P")) {
//                        sort.sort.setPosition(0.56);
//                    } else {
//                        sort.sort.setPosition(0.935);
//                    }
//                }))
//                .afterTime(6.0, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
//                .afterTime(6.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
//
//                .afterTime(8, new InstantAction(() -> {
//                    if (Objects.equals(limeLight.GetColors(2), "P") && Objects.equals(limeLight.GetColors(1), "P"))
//                    {
//                        sort.sort.setPosition(0.19);
//                    }
//
//                    else if (Objects.equals(limeLight.GetColors(2), "P")) {
//                        sort.sort.setPosition(0.56);
//                    }
//
//                    else {
//                        sort.sort.setPosition(0.935);
//                    }
//                }))
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
////
////                .afterTime(0, new InstantAction(() -> sort.sort.setPosition(0.19)))
////                .afterTime(1.5, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
////                .afterTime(2, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
////
////                .afterTime(3, new InstantAction(() -> sort.sort.setPosition(0.56)))
////                .afterTime(4, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
////                .afterTime(4.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
////
////                .afterTime(6, new InstantAction(() -> sort.sort.setPosition(0.935)))
////                .afterTime(7, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
////                .afterTime(7.5, new InstantAction(() ->  flick.flickthing.setPosition(0.35)))
//////////
//                .waitSeconds(7.6)
//                .strafeTo(new Vector2d(35, -32))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}