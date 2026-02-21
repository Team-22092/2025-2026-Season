package org.firstinspires.ftc.teamcode.TESTBENCH;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.MecanumDrive;

@TeleOp(name = "True Triangulation - 55:90 Ratio")
public class FullFieldLocalization extends OpMode {

    MecanumDrive drive;
    DcMotor turret;

    // --- HARDWARE CONSTANTS ---
    static final double MOTOR_TICKS_PER_REV = 751.8;
    static final double GEAR_RATIO = 90.0 / 55.0;
    static final double TICKS_PER_DEGREE = (MOTOR_TICKS_PER_REV * GEAR_RATIO) / 360.0;

    static final int TURRET_MAX_TICKS = 550; // Total travel allowed
    static final double TICKS_AT_FORWARD = 275.0;


    static final Vector2d TARGET_POS = new Vector2d(-60, 50);//-60, 50 for RIGHT, -60, -50 for LEFT

    @Override
    public void init() {
        turret = hardwareMap.get(DcMotor.class, "T");
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // STARTING CONDITION: Turret must be at the 0 position (Far Right) when you hit Init
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set your starting position on the field (X, Y, Heading in Radians)
        drive = new MecanumDrive(hardwareMap, new Pose2d(69, 11, Math.toRadians(180)));


        for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
    }

    @Override
    public void loop() {
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

        // 7. Update Motor
        turret.setTargetPosition((int) targetTicks);
        turret.setPower(1.0); // Full speed to track accurately while moving

        // 8. Driving (Standard Mecanum)
        drive.setDrivePowers(new PoseVelocity2d(
                new Vector2d(-gamepad1.left_stick_y, -gamepad1.left_stick_x),
                -gamepad1.right_stick_x
        ));

        // --- DEBUGGING TELEMETRY ---
        telemetry.addData("X Position", "%.2f", currentPose.position.x);
        telemetry.addData("Y Position", "%.2f", currentPose.position.y);
        telemetry.addData("Heading", "%.2f", robotHeadingDeg);
        telemetry.addData("Calculated Target Ticks", (int)targetTicks);
        telemetry.addData("Current Turret Ticks", turret.getCurrentPosition());
        telemetry.update();
    }
}