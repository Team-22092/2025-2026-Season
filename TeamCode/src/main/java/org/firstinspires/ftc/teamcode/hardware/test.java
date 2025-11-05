package org.firstinspires.ftc.teamcode.hardware;

public class test {

    public double velocity(double distance, double angle, double motorRPM)
    {
        angle = Math.toRadians(angle);
        motorRPM = 6000;
        double out = distance * Math.sqrt(9.81/
                (2*Math.pow(Math.cos(angle), 2)
                        * (distance * Math.tan(angle) - 0.7845)



                )) * 1.1;

        //rad is made up, will need to find with actual motor, but its closeish
        double MaS_TO_RPM = (out * 60) / (2 * Math.PI * 0.005);









        //this is an example motor, so the rpm is made up.
        return   (MaS_TO_RPM / motorRPM) ; //%
    }
}
