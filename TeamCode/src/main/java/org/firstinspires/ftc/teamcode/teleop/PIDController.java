package org.firstinspires.ftc.teamcode.teleop;

public class PIDController {
    public double kp; // Proportional gain
    public double ki; // Integral gain
    public double kd; // Derivative gain

    private double target; // Setpoint
    private double integral; // Integral term accumulation
    private double previousError; // Previous error value

    // Proper constructor
    public PIDController(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.target = 0.0;
        this.integral = 0.0;
        this.previousError = 0.0;
    }

    // Set the target value
    public void setTarget(double target) {
        this.target = target;
        this.integral = 0.0;
        this.previousError = 0.0;
    }

    // Calculate PID output
    public double calculateOutput(double currentValue, double deltaTime) {
        double error = target - currentValue;

        // Avoid division by zero
        if (deltaTime <= 0) deltaTime = 1e-6;

        // PID terms
        double proportionalTerm = kp * error;
        integral += error * deltaTime;
        double integralTerm = ki * integral;
        double derivativeTerm = kd * ((error - previousError) / deltaTime);

        // Total output
        double output = proportionalTerm + integralTerm + derivativeTerm;

        // Update previous error
        previousError = error;

        return output;
    }
}
