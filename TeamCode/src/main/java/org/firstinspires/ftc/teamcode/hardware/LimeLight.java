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

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.ArrayList;
import java.util.List;







public class LimeLight {
    //LimeLight3a Var

    private CRServo turretServo;
    private double acceptableTurretErrorDeg = 1.5;


    private Limelight3A limelight;

    private DcMotor limelight_detector;

    private IMU imu;
    public double distance = 0;


    double angle;


    //start code (when the robot is started, ran in teleopMain.java
    public LimeLight(HardwareMap hardwareMap, Telemetry telemetry) {
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

        limelight.pipelineSwitch(0);

        //Changes to detection

        //Get the imu
        imu = hardwareMap.get(IMU.class, "imu");
        //Get the orientation of the limelight on the robot
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, //Might need to be updated
            RevHubOrientationOnRobot.UsbFacingDirection.FORWARD //Same with this
        );
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));



        limelight.start();




        telemetry.setMsTransmissionInterval(11);
        telemetry.addData("Limelight On, Returning, this is updated: ", limelight.getStatus()); //Get limelight status
        telemetry.update();
    } //end of Lime Light
    YawPitchRollAngles orientation;

    public List<String> Parts = new ArrayList<>();


    public void LimeLightOpMode(Telemetry telemetry, ColorTest colorTest) //final code wont have telem value, this is for testing //TODO turn this into a double to pull Yaw val for WHEELS
    {

        limelight.pipelineSwitch(0);

        if (limelight == null) { //If we can detect the limelight
            //TODO - REMOVE THIS LATER
            //TODO - MOVE THIS TO Display_Telemetry
            telemetry.addData("limelight", "Not on"); // debug we cant see it
            return; // end
        }


        //Get angles
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw()); //get the yaw from the rev hub, way more accurate.
        LLResult result = limelight.getLatestResult(); //Pull the results from the limelight

        //telemetry.addData("Have we", "Pushed?");

        //colorTest.moveRight = false;
        if(result != null && result.isValid()) { //If the result isn't nothing, and its valid, keep going


            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults(); //LL Result types is latest results from the camera, the FiducialResults is valid april tags, and we have a list of them.

            if (!fiducials.isEmpty()) { //skip if theres no tags detected, but if there is, keep going
                LLResultTypes.FiducialResult fr = fiducials.get(0); //get the first valid tag from the camera

                Pose3D tagPoseCamera = fr.getTargetPoseCameraSpace();


                //print the id
                //telemetry.addData("ID", fr.getFiducialId()); //TODO - REMOVE THIS LATER
                //Define the patterns


                if (fr.getFiducialId() == 24) {
                    distance = computeDistanceToFiducial(fr, colorTest, telemetry); // meter
                    telemetry.addData("Dist", "%.2f", distance);

                    if (tagPoseCamera != null) {

                        // camera-space coords (meters)
                        double x = tagPoseCamera.getPosition().x; // left/right
                        double z = tagPoseCamera.getPosition().z; // forward

                        // angle to tag (deg), 0 = centered
                        double angleDeg = Math.toDegrees(Math.atan2(x, z));

                        telemetry.addData("TagYawDeg", "%.2f", angleDeg);

                        // error is just the angle (we want 0)
                        double errorDeg = angleDeg;

                        if (Math.abs(errorDeg) > acceptableTurretErrorDeg) {

                           double kP = 0.0155;
                            double turretPower = errorDeg * kP;

                            turretPower = clamp(turretPower, -1, 1);
                            turretServo.setPower(turretPower);

                            telemetry.addData("TurretPower", "%.3f", turretPower);
                        }
                        else {
                            // close enough, stop spinning
                            turretServo.setPower(0.0);
                            telemetry.addData("Turret", "Centered");
                        }






                } else if (fr.getFiducialId() == 20){
                    distance = computeDistanceToFiducial(fr, colorTest, telemetry); // meter
                    telemetry.addData("Dist", "%.2f", distance);
                }
                } else {
                    // lost pose → stop turret
                    turretServo.setPower(0.0);
                    telemetry.addData("Turret", "No Pose");
                }






            }

        }
        else{
            colorTest.centered = false;
        }

    } //end of Lime Light Op Mode

    public double computeDistanceToFiducial(LLResultTypes.FiducialResult fr, ColorTest colorTest, Telemetry telemetry) {
        if (fr == null) return 0;
        Pose3D tagPoseCamera = fr.getTargetPoseCameraSpace();
        if (tagPoseCamera == null) return 0;

        double x = tagPoseCamera.getPosition().x;
        double y = tagPoseCamera.getPosition().y;
        double z = tagPoseCamera.getPosition().z;

        double dist = Math.sqrt(x * x + y * y + z * z);

        double yaw = tagPoseCamera.getOrientation().getYaw();
        //double yawDeg = Math.toDegrees(yawRad);




        //telemetry.addData("ANGLE OF YAW", yaw);

// center zone (strict): -6 .. 4
        boolean inMainCenter = (yaw > -4 && yaw < 9.1);

        if (inMainCenter) {
            // fully centered
            colorTest.centered = true;

            colorTest.moveRight = false;
        }
        else {

            colorTest.centered = false;

            colorTest.moveRight = (yaw > 0);

        }

// telemetry so we can debug quickly
        telemetry.addData("ANGLE", tagPoseCamera.getOrientation().getYaw());


        telemetry.update();







        return dist;
    }


    public String GetColors(float part) {
        LLResult result = limelight.getLatestResult(); //Pull the results from the limelight
        limelight.pipelineSwitch(3);

        if (Parts.isEmpty()) {
            Parts.add("P");
            Parts.add("P");
            Parts.add("G");
        }

        if(result == null)
        {
            return Parts.get((int) part);
        }
        if (result.isValid()) { //If the result isn't nothing, and its valid, keep going
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults(); //LL Result types is latest results from the camera, the FiducialResults is valid april tags, and we have a list of them.
            if (!fiducials.isEmpty()) {
                LLResultTypes.FiducialResult fr = fiducials.get(0);
                if (fr.getFiducialId() == 23) {
                    Parts.clear();
                    Parts.add("P");
                    Parts.add("P");
                    Parts.add("G");
                } else if (fr.getFiducialId() == 21) {
                    Parts.clear();
                    Parts.add("G");
                    Parts.add("P");
                    Parts.add("P");
                } else if (fr.getFiducialId() == 22) {
                    Parts.clear();
                    Parts.add("P");
                    Parts.add("G");
                    Parts.add("P");

                }}}

        return Parts.get((int) part);
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
