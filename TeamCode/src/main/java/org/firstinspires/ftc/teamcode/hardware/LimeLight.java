package org.firstinspires.ftc.teamcode.hardware;


import static androidx.core.math.MathUtils.clamp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.List;







//  # @note INFO
//These comments use a custom extension to render things like, images, styles, and tags
//please have the extension enabled for the code to make sense

// @img(https://github.com/Team-22092/2024-2025-Season/raw/main/docs/logo.png)


public class LimeLight {
    //LimeLight3a Var


    //TOUCH SENSOR:
    TouchSensor LEFTTOUCH, RIGHTTOUCH;





    private CRServo turretServo;


    private double acceptableTurretErrorDeg = 1.5;


    private Limelight3A limelight;
    private IMU imu;
    public double distance = 0;

  //the start code for the LimeLight
    public LimeLight(HardwareMap hardwareMap, Telemetry telemetry) {


        //touchsensor
        LEFTTOUCH = hardwareMap.get(TouchSensor.class, "LT");
        RIGHTTOUCH = hardwareMap.get(TouchSensor.class, "RT");




//  if (touchSensor.isPressed()){


        //set hardware map
        limelight = hardwareMap.get(Limelight3A.class, "limelight"); //the hardware map is setting the name.

        turretServo = hardwareMap.get(CRServo.class, "TR");

        //MOTOR FOR SHOOTING
        //limelight_detector = hardwareMap.get(DcMotor.class, "LLD");



        //set the detect pipeline to 0
        //MAP
        //0 = AprilTags
        //1 = Purple Balls
        //2 = Green Balls
        //3 = Detect the pattern, ONLY FOR AUTO (in GetColors)

        limelight.pipelineSwitch(0);

        //Changes to detection

        //Get the imu, this is for limelight positioning
        imu = hardwareMap.get(IMU.class, "imu");
        //Get the orientation of the limelight on the robot
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, //Might need to be updated
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD //Same with this
        );
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));


        //start the limelight
        limelight.start();




        telemetry.setMsTransmissionInterval(11);
        telemetry.addData("Limelight On, Returning, this is updated: ", limelight.getStatus()); //Get limelight status
        telemetry.update();
    } //end of Lime Light

    public List<String> Parts = new ArrayList<>();

    // === Turret search config ===
    private static final double SEARCH_POWER = 0.2; // power when we dont see it.
    private static final long SEARCH_FLIP_MS = 700;  // flip direction every 0.7s

    private long lastSearchFlipTime = 0;
    private int searchDirection = 1; // 1 or -1

    public void LimeLightOpMode(Telemetry telemetry, ColorTest colorTest) //final code wont have telem value, this is for testing
    {
        /*
            This block (Yaw, Limelight, LLResult) was moved up for better limelight pipeline switching.
        */
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles(); //Get angles of the robot
        limelight.updateRobotOrientation(orientation.getYaw()); //get the yaw from the rev hub, way more accurate.
        LLResult result = limelight.getLatestResult(); //Pull the results from the limelight

        if(result.getPipelineIndex() != 0) //we want to avoid trying to update it every frame, as this could cause slowness if they haven't factored in switching
        {
            limelight.pipelineSwitch(0); // we attempt to switch it, this is for auto.
        }


        
        //TODO - REMOVE THIS LATER //TODO - MOVE THIS TO Display_Telemetry
        if (limelight == null) { telemetry.addData("limelight", "Not on"); } // debug we cant find the limelight





        //colorTest.moveRight = false;
        if(result.isValid()) { //If its valid, keep going


            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults(); //LL Result types is latest results from the camera, the FiducialResults is valid april tags, and we have a list of them.

            if (!fiducials.isEmpty()) { //skip if theres no tags detected, but if there is, keep going
                LLResultTypes.FiducialResult fr = fiducials.get(0); //get the first valid tag from the camera.

                Pose3D tagPoseCamera = fr.getTargetPoseCameraSpace(); //get the tag in 3d positioning space


                /*
                    This next block checks what the tag is (because we want to avoid some tags when driving and stuff
                    we check tags 24 and 20, as they are the marked ones for the shooter.
                */
                if (fr.getFiducialId() == 24) {

                    distance = computeDistanceToFiducial(fr, telemetry);
                    telemetry.addData("Dist", "%.2f", distance);

                    if (tagPoseCamera != null) {

                        final double CAMERA_X_OFFSET = 0.15; // meters, tune this

                        double x = tagPoseCamera.getPosition().x - CAMERA_X_OFFSET;
                        double z = tagPoseCamera.getPosition().z;

                        double angleDeg = Math.toDegrees(Math.atan2(x, z));
                        double errorDeg = angleDeg;

                        telemetry.addData("Turret Error (deg)", "%.2f", errorDeg);

                        double acceptableError = acceptableTurretErrorDeg;

                        if (Math.abs(errorDeg) > acceptableError) {

                            double baseKP = 0.0155;
                            double distanceScale = clamp(distance / 2.0, 0.6, 1.6);

                            double turretPower = errorDeg * baseKP * distanceScale;

                            turretPower = clamp(turretPower, -0.6, 0.6);

                            if (Math.abs(turretPower) < 0.1) {
                                turretPower = Math.copySign(0.1, turretPower);
                            }

                            turretServo.setPower(-turretPower);

                        } else {
                            turretServo.setPower(0.0);
                        }
                    }
                }



                //if we see the limelight id code as (20)
                 else if (fr.getFiducialId() == 20){
                    distance = computeDistanceToFiducial(fr, telemetry); // meter
                    telemetry.addData("Dist", "%.2f", distance);
                }







            }
            else {
//                // 🧹 SEARCH MODE (no tag detected)
//
//                // If we hit right wall, go left
//                if (RIGHTTOUCH.isPressed()) {
//                    searchDirection = -1;
//
//                }
//
//                // If we hit left wall, go right
//                if (LEFTTOUCH.isPressed()) {
//                    searchDirection = 1;
//
//                }
//
//                turretServo.setPower(-searchDirection * SEARCH_POWER);
                turretServo.setPower( 0);


            }


        }
        else {
            // 🧹 SEARCH MODE (no tag detected)


//            // If we hit right wall, go left
//            if (RIGHTTOUCH.isPressed()) {
//                searchDirection = -1;
//
//            }
//
//            // If we hit left wall, go right
//            if (LEFTTOUCH.isPressed()) {
//                searchDirection = 1;
//
//            }
//
//  v
        turretServo.setPower( 0);

        }



    } //end of Lime Light Op Mode

    public double computeDistanceToFiducial(LLResultTypes.FiducialResult fr, Telemetry telemetry) {
        if (fr == null) return 0;
        Pose3D tagPoseCamera = fr.getTargetPoseCameraSpace();
        if (tagPoseCamera == null) return 0;

        double x = tagPoseCamera.getPosition().x;
        double y = tagPoseCamera.getPosition().y;
        double z = tagPoseCamera.getPosition().z;

        double dist = Math.sqrt(x * x + y * y + z * z);

// telemetry so we can debug quickly

//we used to have a lightbar here, its been removed.
    ;


        return dist;
    }


    //AUTOCODE
    //----------------------------------------------------------------------------------------------------------

    public String GetColors(float part) { //AUTO CODE
        LLResult result = limelight.getLatestResult(); //Pull the results from the limelight
        limelight.pipelineSwitch(3); //PIPELINE 3 IS PATTERN CODE DETECTION


        //Failsafe if we don't see anything
        if (Parts.isEmpty()) {
            Parts.add("P");
            Parts.add("P");
            Parts.add("G");
        }

        if(result == null)
        {
            return Parts.get((int) part);
        }
        if (result.isValid()) { //If its valid, keep going
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults(); //LL Result types is latest results from the camera, the FiducialResults is valid april tags, and we have a list of them.
            if (!fiducials.isEmpty()) {
                LLResultTypes.FiducialResult fr = fiducials.get(0); //get the first apriltag we see (fiducials.get(0))
                if (fr.getFiducialId() == 23) { //if we see ID23, PPG
                    Parts.clear();
                    Parts.add("P");
                    Parts.add("P");
                    Parts.add("G");
                } else if (fr.getFiducialId() == 21) { //if we see ID21, GPP
                    Parts.clear();
                    Parts.add("G");
                    Parts.add("P");
                    Parts.add("P");
                } else if (fr.getFiducialId() == 22) { //if we see ID22, PGP
                    Parts.clear();
                    Parts.add("P");
                    Parts.add("G");
                    Parts.add("P");

                }}}

        return Parts.get((int) part); // return the one we detect for the auto
    }



    public void Display_Telemetry(Telemetry telemetry)
    {
        LLStatus status = limelight.getStatus(); //Get the status of the limelight for telem



        telemetry.addData("Name", "%s", status.getName()); //Get the status
        telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d", //Get the Cpu temp, and fps
                status.getTemp(), status.getCpu(), (int)status.getFps());
        telemetry.addData("Pipeline", "Index: %d, Type: %s", //What pipeline we are using
                status.getPipelineIndex(), status.getPipelineType());



    } //end of Display Telemetry


}
