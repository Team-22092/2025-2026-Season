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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;







public class LimeLight {
    //LimeLight3a Var
    private Limelight3A limelight;

    private DcMotor limelight_detector;

    private IMU imu;
    public double distance = 0;





    //start code (when the robot is started, ran in teleopMain.java
    public LimeLight(HardwareMap hardwareMap, Telemetry telemetry) {
        //set hardware map
        limelight = hardwareMap.get(Limelight3A.class, "limelight"); //the hardware map is setting the name.
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

    public void LimeLightOpMode(Telemetry telemetry ) //final code wont have telem value, this is for testing //TODO turn this into a double to pull Yaw val for WHEELS
    {
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


        if(result != null && result.isValid()) { //If the result isn't nothing, and its valid, keep going


            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults(); //LL Result types is latest results from the camera, the FiducialResults is valid april tags, and we have a list of them.
            if (!fiducials.isEmpty()) { //skip if theres no tags detected, but if there is, keep going
                LLResultTypes.FiducialResult fr = fiducials.get(0); //get the first valid tag from the camera
                //print the id
                telemetry.addData("ID", fr.getFiducialId()); //TODO - REMOVE THIS LATER
                //Define the patterns
                if (fr.getFiducialId() == 23) {
                    telemetry.addData("Pattern", "[Purple] [Purple] [Green]");
                } else if (fr.getFiducialId() == 21) {
                    telemetry.addData("Pattern", "[Green] [Purple] [Purple]");
                } else if (fr.getFiducialId() == 22) {
                    telemetry.addData("Pattern", "[Purple] [Green] [Purple]");
                } else if (fr.getFiducialId() == 24) {
                    distance = computeDistanceToFiducial(fr); // meter
                    telemetry.addData("Dist", "%.2f", distance);
                } else if (fr.getFiducialId() == 20){
                    distance = computeDistanceToFiducial(fr); // meter
                    telemetry.addData("Dist", "%.2f", distance);
                }


            }

        }

    } //end of Lime Light Op Mode


//    public double returnVals(double numbers)
//    {
//        if(numbers == 0) //Return Distance.
//        {
//            return distance;
//        }
//
//        else{
//            return 0;
//        }
//    }


    // compute euclidean distance (meters) from the camera to the fiducial by using the pose in camera space
    // brainrot: this code is glued together with duct tape and pure courage
    public double computeDistanceToFiducial(LLResultTypes.FiducialResult fr) {
        if (fr == null) return 0;

        // fr.getTargetPoseCameraSpace() returns a location in cam space
        Pose3D tagPoseCamera = fr.getTargetPoseCameraSpace();
        if (tagPoseCamera == null) return 0; //if we dont have anything, dont send somthing important

        double x = tagPoseCamera.getPosition().x; // meters
        double y = tagPoseCamera.getPosition().y; // meters
        double z = tagPoseCamera.getPosition().z; // meters

        // Euclidean distance (meters) (\(x^{2}+y^{2}+z^{2}\)
        double dist = Math.sqrt(x * x + y * y + z * z);

        return dist;
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
