package org.firstinspires.ftc.teamcode.hardware;


import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;




//TODO - For Testing

//BUILD TEST BOT

//DRIVE TEST

//THE LIMELIGHT NEEDS TO BE MOUNTED AND CONNECTED.

//TEST IF THE YAW WORKS WHEN IT DETECTS, AND FIX ANY BUGS

//START WITH A BASIC TEXT TELLING WHAT WAY TO ROTATE THROUGH TELEM TO SEE IF ITS GONNA WORK

//ADD MOTORS


//BLOCK SOME QR CODES THAT COULD INTERFERE (1, 2, 3)








public class LimeLight {

 //LimeLight3a Var
    private Limelight3A limelight;


    //start code (when the robot is started, ran in teleopMain.java
    public LimeLight(HardwareMap hardwareMap, Telemetry telemetry) {
        //set hardware map
        limelight = hardwareMap.get(Limelight3A.class, "limelight"); //the hardware map is setting the name.

        telemetry.setMsTransmissionInterval(11);

        //set the detect pipeline to 0

        //MAP

        //0 = AprilTags
        //1 = Purple Balls
        //2 = Green Balls
        limelight.pipelineSwitch(0);


        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */

        limelight.start();




        telemetry.addData("Limelight On, Returning: ", limelight.getStatus()); //Get limelight status


        telemetry.update();
    } //end of Lime Light






    public void LimeLightOpMode(Telemetry telemetry ) //final code wont have telem value, this is for testing //TODO turn this into a double to pull Yaw val for WHEELS
    {
        if (limelight == null) { //If we can detect the limelight



            //TODO - REMOVE THIS LATER

            //TODO - MOVE THIS TO Display_Telemetry
            telemetry.addData("limelight", "Not on"); // debug we cant see it
            telemetry.update(); //push the telem
            return; // end
        }


        LLResult result = limelight.getLatestResult(); //Pull the results from the limelight



        if(result != null && result.isValid()) { //If the result isn't nothing, and its valid, keep going


            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults(); //LL Result types is latest results from the camera, the FiducialResults is valid april tags, and we have a list of them.



            if(!fiducials.isEmpty()) { //skip if theres no tags detected, but if there is, keep going
                LLResultTypes.FiducialResult fr = fiducials.get(0); //get the first valid tag from the camera

                //print the id
                telemetry.addData("ID", fr.getFiducialId()); //TODO - REMOVE THIS LATER

                Pose3D pose = fr.getRobotPoseTargetSpace(); //Get the Pos of the TAG
                Pose3D tagPoseCamera = fr.getTargetPoseCameraSpace(); // Get the Camera Pos

                if(pose != null) // Skip if the data is werid
                {
                    double yaw = pose.getOrientation().getYaw(); //Get the one direction we care about, yaw

                   // double xPos = pose.getPosition().x; //Get the X Pos for lining up

                    //TODO ------ tagX and offsetX prob isn't going to be needed.
                   // double tagX = tagPoseCamera.getPosition().x; //Cam X Pos

                   // double offsetX = xPos - tagX; // Figure out offset


                    telemetry.addData("Yaw", "%.2f", yaw); //TODO - REMOVE THIS LATER
                }


            }


        }

       // telemetry.update(); //TODO - This might be ok???

    } //end of Lime Light Op Mode



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
