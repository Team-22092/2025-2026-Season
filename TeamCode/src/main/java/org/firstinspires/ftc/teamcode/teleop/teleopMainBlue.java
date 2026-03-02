package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.Pose2d;
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

// 🔵 BLUE SIDE
@TeleOp(name = "Teleop Main Blue")
public class teleopMainBlue extends OpMode {

    MecanumDrive drive;
    DcMotor turret;

    // --- HARDWARE CONSTANTS ---
    static final double MOTOR_TICKS_PER_REV = 751.8;
    static final double GEAR_RATIO = 90.0 / 55.0;
    static final double TICKS_PER_DEGREE = (MOTOR_TICKS_PER_REV * GEAR_RATIO) / 360.0;

    static final int TURRET_MAX_TICKS = 550; // Total travel allowed
    static final double TICKS_AT_FORWARD = 275.0;
    static final double TURRET_OFFSET_STEP_DEG = 1.0;
    static final double TURRET_OFFSET_MAX_DEG = 30.0;

    // MIRRORED FROM RED (-67, 30)
    static final Vector2d TARGET_POS = new Vector2d(-67, -30);

    Gamepad currentGamepadDrive = new Gamepad();
    Gamepad currentGamepadCopilot = new Gamepad();
    Gamepad prevGamepadCopilot = new Gamepad();

    Wheels wheels;
    Intake intake;
    SAVESHOTS saveshots;
    BallColor ballColor;
    ColorTest colorTest;
    Sort sort;
    public LimeLight limeLight;
    public Flick flick;
    ShootWheels shootWheels;

    double turretOffsetDeg = 0.0;

    @Override
    public void init() {
        wheels = new Wheels(hardwareMap);
        intake = new Intake(hardwareMap);
        saveshots = new SAVESHOTS(hardwareMap);
        ballColor = new BallColor(hardwareMap);
        colorTest = new ColorTest(hardwareMap, telemetry);
        shootWheels = new ShootWheels(hardwareMap);
        sort = new Sort(hardwareMap);
        flick = new Flick(hardwareMap, shootWheels);
        limeLight = new LimeLight(hardwareMap, telemetry);

        turret = hardwareMap.get(DcMotor.class, "T");
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        // STARTING CONDITION: Turret must be at the 0 position (Far Left) when you hit Init
        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Set your starting position on the field (Mirrored from Red: Y becomes negative)
        drive = new MecanumDrive(hardwareMap, new Pose2d(35, -32, Math.toRadians(180)));

        for (LynxModule hub : hardwareMap.getAll(LynxModule.class)) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        List<LynxModule> all_hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : all_hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }

        flick.setPattern(LimeLight.getSavedPattern());


    }

    @Override
    public void loop() {

        currentGamepadDrive.copy(gamepad1);
        currentGamepadCopilot.copy(gamepad2);

        // ====== TURRET OFFSET ADJUST ======
        if (currentGamepadCopilot.dpad_left && !prevGamepadCopilot.dpad_left) {
            turretOffsetDeg -= TURRET_OFFSET_STEP_DEG;
        }
        if (currentGamepadCopilot.dpad_right && !prevGamepadCopilot.dpad_right) {
            turretOffsetDeg += TURRET_OFFSET_STEP_DEG;
        }
        if (turretOffsetDeg < -TURRET_OFFSET_MAX_DEG) turretOffsetDeg = -TURRET_OFFSET_MAX_DEG;
        if (turretOffsetDeg > TURRET_OFFSET_MAX_DEG) turretOffsetDeg = TURRET_OFFSET_MAX_DEG;

        ballColor.pos = limeLight.color;
        telemetry.addData("COLORRETURN",  ballColor.pos);

        wheels.ManualDrive(currentGamepadDrive);
        intake.IntakeOpMode(currentGamepadCopilot);
        sort.SortOpMode(currentGamepadCopilot, prevGamepadCopilot, flick);
        flick.FlickOpMode(currentGamepadCopilot, prevGamepadCopilot);
        shootWheels.ShootWheelsOpMode(currentGamepadCopilot, prevGamepadCopilot, limeLight, hardwareMap);

        colorTest.COLOR();

        // Let Limelight handle alliance internally for Blue
        limeLight.LimeLightOpMode(telemetry, colorTest, "B");

        saveshots.IntakeOpMode(gamepad2, shootWheels);
        ballColor.ColorOpMode(telemetry);

        limeLight.Display_Telemetry(telemetry);
        shootWheels.Display_Telemetry(telemetry);
        sort.SortTelem(telemetry);
        telemetry.addData("Turret Offset (deg)", turretOffsetDeg);

        prevGamepadCopilot.copy(currentGamepadCopilot);

        // 1. Update Deadwheel Localization
        drive.updatePoseEstimate();
        Pose2d currentPose = drive.localizer.getPose();

        // 2. Triangulate the Vector to the Goal
        double dx = TARGET_POS.x - currentPose.position.x;
        double dy = TARGET_POS.y - currentPose.position.y;
        double absoluteAngleDeg = Math.toDegrees(Math.atan2(dy, dx));

        // 3. Convert Field Angle to Robot-Relative Angle
        double robotHeadingDeg = Math.toDegrees(currentPose.heading.toDouble());
        double relativeAngle = absoluteAngleDeg - robotHeadingDeg;
        relativeAngle += turretOffsetDeg;

        // 4. Normalize to -180 to 180 (shortest path)
        while (relativeAngle > 180) relativeAngle -= 360;
        while (relativeAngle <= -180) relativeAngle += 360;

        // 5. Convert to Ticks with your Far-Left Offset
        // Because 0 is far left, moving right means negative ticks. Center is -275.
        double targetTicks = (relativeAngle * TICKS_PER_DEGREE) - TICKS_AT_FORWARD;

        // 6. Safety Clamp (Prevents the turret from breaking itself)
        // Max limit is 0 (Far Left), Min limit is -550 (Far Right)
        if (targetTicks > 0) targetTicks = 0;
        if (targetTicks < -TURRET_MAX_TICKS) targetTicks = -TURRET_MAX_TICKS;

        if (!LimeLight.locktarget) {
            // 7. Update Motor
            turret.setTargetPosition(0);
            turret.setPower(1.0);
        }



        // Check for touchpad press on Gamepad 2 (Copilot)
        if (currentGamepadCopilot.touchpadWasPressed()) {
            // Reset the robot's position to X=60, Y=60, while keeping current heading
            double currentHeading = drive.localizer.getPose().heading.toDouble();
            drive.localizer.setPose(new Pose2d(60, -60, currentHeading));
        }

        telemetry.addData("DRIVE POS - ", drive.localizer.getPose());
        telemetry.addData("Turret Ticks", targetTicks);
        telemetry.update();
    }
}