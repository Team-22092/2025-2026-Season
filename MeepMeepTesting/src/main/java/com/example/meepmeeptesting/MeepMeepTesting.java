package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(700);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-56, 52, -180))


                .strafeTo(new Vector2d(32, 52))


//                .afterTime(0.5, new InstantAction(() -> flick.flickthing.setPosition(1.0)))
//                .afterTime(0.8, new InstantAction(() ->  flick.flickthing.setPosition(0.2)))


//////

                .strafeTo(new Vector2d(38, -60), new TranslationalVelConstraint(20),
                        new ProfileAccelConstraint(-20, 20))
                .strafeTo(new Vector2d(35, -32))
                .strafeToLinearHeading(new Vector2d(62, -11), Math.toRadians(200.5))



                .strafeToLinearHeading(new Vector2d(60, 60), Math.toRadians(90))





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
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_JUICE_BLACK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}