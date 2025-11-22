package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp; // or Auton
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * ShooterAutoCalibrateAdvanced
 *
 * Repeated multi-band sweep calibration for shooter feedforward k (power per RPM).
 *
 * Usage:
 *  - Start this OpMode on the Driver Station.
 *  - Apply the real load you want (shooting, or keep it loaded) while it runs.
 *  - Let it iterate several sweeps; watch telemetry or console.
 *  - When satisfied, stop the OpMode. Motor will be stopped.
 *
 * Tuning features:
 *  - Multi-band sweep (low/mid/high) to capture nonlinearity under load.
 *  - Outlier removal using Median Absolute Deviation (MAD).
 *  - Ordinary least-squares per sweep, per band, and combined.
 *  - Running moving average of k across completed sweeps with std-dev.
 *
 * Brainrot comment: it's a bit noisy, but that noise is honest and delicious.
 */
@TeleOp(name = "Shooter AutoCalibrate Advanced", group = "Calibration")
public class ShooterAutoCalibrateAdvanced extends LinearOpMode {

    // ---------- USER CONFIG ----------
    private final String MOTOR_NAME = "WHEEL";
    // power bands (each band will be swept in 'pointsPerBand' steps)
    private final double[][] bands = { {0.18, 0.35}, {0.36, 0.60}, {0.62, 0.90} }; // low, mid, high
    private final int pointsPerBand = 5;         // points per band
    private final double settleTime = 0.9;       // sec to wait after applying power
    private final double sampleTime = 0.6;       // sec to sample RPM after settle
    private final double ticksPerRev = 26.0;     // encoder CPR
    private final double gearRatio = 1.0;        // motor revs per wheel rev
    private final int minValidRpm = 5;           // ignore RPMs less than this (stalled)
    private final int sweepsToKeep = 5;         // moving average window for final k
    // ----------------------------------

    // storage for moving-average of recent k values
    private final double[] recentKs = new double[sweepsToKeep];
    private int recentIndex = 0;
    private int recentCount = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.clearAll();
        telemetry.addLine("Shooter AutoCalibrate Advanced");
        telemetry.addLine("WARNING: Motor will spin repeatedly. Keep clear.");
        telemetry.update();

        DcMotor wheel = hardwareMap.get(DcMotor.class, MOTOR_NAME);
        wheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        wheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        wheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        waitForStart();

        // main loop: repeat sweeps until opmode stopped
        int sweepCount = 0;
        while (opModeIsActive()) {
            sweepCount++;
            telemetry.addData("Sweep", "%d", sweepCount);
            telemetry.update();

            // collect points for this sweep
            int totalPoints = bands.length * pointsPerBand;
            double[] powers = new double[totalPoints];
            double[] rpms   = new double[totalPoints];
            int idx = 0;

            // For each band sweep across its power range
            for (int b = 0; b < bands.length && opModeIsActive(); b++) {
                double pStart = bands[b][0];
                double pEnd   = bands[b][1];

                for (int s = 0; s < pointsPerBand && opModeIsActive(); s++) {
                    double p = pStart + ( (pEnd - pStart) * s / (pointsPerBand - 1) );

                    // Apply power
                    wheel.setPower(p);
                    // Wait for settle
                    sleepWithActiveChecks((long)(settleTime * 1000L));

                    // Sample RPM over sampleTime
                    double rpm = sampleWindowRPM(wheel, sampleTime, ticksPerRev, gearRatio);

                    powers[idx] = p;
                    rpms[idx]   = rpm;
                    idx++;

                    // small inter-point pause
                    sleepWithActiveChecks(150);
                }
            }

            // stop motor between sweeps briefly
            wheel.setPower(0.0);
            sleepWithActiveChecks(200);

            // Filter invalid points (NaN or tiny RPM)
            boolean[] validMask = new boolean[totalPoints];
            int validCount = 0;
            for (int i = 0; i < totalPoints; i++) {
                if (Double.isFinite(rpms[i]) && rpms[i] > minValidRpm) {
                    validMask[i] = true; validCount++;
                } else {
                    validMask[i] = false;
                }
            }

            if (validCount < 2) {
                telemetry.addData("SweepResult", "FAILED - not enough valid points (%d)", validCount);
                telemetry.update();
                // keep looping but don't add k
                continue;
            }

            // Outlier removal using MAD on rpm residuals vs median RPM trend:
            // We compute median RPM, then MAD, and remove rpm points that deviate strongly.
            double medianR = medianOfArrayFiltered(rpms, validMask);
            double mad = madOfArrayFiltered(rpms, validMask, medianR);
            double madThreshold = Math.max(1.0, mad * 3.5); // tolerant threshold
            int filteredCount = 0;
            boolean[] filteredMask = new boolean[totalPoints];
            for (int i = 0; i < totalPoints; i++) {
                if (!validMask[i]) { filteredMask[i] = false; continue; }
                double dev = Math.abs(rpms[i] - medianR);
                if (dev <= madThreshold) { filteredMask[i] = true; filteredCount++; }
                else filteredMask[i] = false;
            }

            if (filteredCount < 2) {
                telemetry.addData("SweepResult", "FAILED - all points rejected by MAD");
                telemetry.update();
                continue;
            }

            // Compute ordinary least squares slope: power = k * rpm + c  => k = cov(r,p)/var(r)
            double kOverall = computeSlopeFiltered(rpms, powers, filteredMask);

            // Compute per-band slopes as diagnostics:
            double[] bandK = new double[bands.length];
            for (int b = 0, base = 0; b < bands.length; b++, base += pointsPerBand) {
                boolean[] bandMask = new boolean[totalPoints];
                int bandValid = 0;
                for (int i = 0; i < pointsPerBand; i++) {
                    int j = base + i;
                    bandMask[j] = filteredMask[j];
                    if (bandMask[j]) bandValid++;
                }
                if (bandValid >= 2) bandK[b] = computeSlopeFiltered(rpms, powers, bandMask);
                else bandK[b] = Double.NaN;
            }

            // Add to moving average of ks
            addRecentK(kOverall);

            // compute running avg and stddev
            double avgK = meanOfRecent();
            double stdK = stddevOfRecent();

            // Output result line exactly as requested and diagnostics
            String kLine = String.format("k == %.9f (sweep %d) avg=%.9f σ=%.9e", kOverall, sweepCount, avgK, stdK);
            telemetry.clearAll();
            telemetry.addLine(kLine);
            for (int b = 0; b < bands.length; b++) {
                String bandStr = String.format("band %d k= %s", b+1,
                        Double.isFinite(bandK[b]) ? String.format("%.9f", bandK[b]) : "N/A");
                telemetry.addLine(bandStr);
            }
            telemetry.addLine(String.format("valid pts %d filtered %d total %d", filteredCount, validCount - filteredCount, totalPoints));
            telemetry.addLine("Press STOP on DS to end calibration");
            telemetry.update();

            // also print to console for logs (one concise line)
            System.out.println(kLine);

            // small pause between sweeps so operator can change load manually if desired
            sleepWithActiveChecks(500);
        }

        // ensure motor is stopped at end
        wheel.setPower(0.0);
        telemetry.addLine("Calibration stopped - motor OFF");
        telemetry.update();
    }

    // ---------- Helpers ----------

    // sample RPM over a time window using encoder difference across the whole window (more robust)
    private double sampleWindowRPM(DcMotor motor, double seconds, double ticksPerRev, double gearRatio) throws InterruptedException {
        if (seconds <= 0.0) return Double.NaN;
        int startEnc = motor.getCurrentPosition();
        long ms = (long)(seconds * 1000.0);
        long slept = 0;
        long step = 20;
        while (slept < ms && opModeIsActive()) {
            long toSleep = Math.min(step, ms - slept);
            Thread.sleep(toSleep);
            slept += toSleep;
        }
        int endEnc = motor.getCurrentPosition();
        double deltaTicks = (double)(endEnc - startEnc);
        double revs = deltaTicks / (ticksPerRev * gearRatio);
        double rpm = (revs / seconds) * 60.0;
        if (!Double.isFinite(rpm)) return Double.NaN;
        return rpm;
    }

    private void sleepWithActiveChecks(long ms) throws InterruptedException {
        long slept = 0;
        long step = 20;
        while (slept < ms && opModeIsActive()) {
            long toSleep = Math.min(step, ms - slept);
            Thread.sleep(toSleep);
            slept += toSleep;
        }
    }

    // compute median of filtered array
    private double medianOfArrayFiltered(double[] arr, boolean[] mask) {
        int n = 0;
        for (int i = 0; i < arr.length; i++) if (mask[i]) n++;
        double[] tmp = new double[n];
        int idx = 0;
        for (int i = 0; i < arr.length; i++) if (mask[i]) tmp[idx++] = arr[i];
        java.util.Arrays.sort(tmp);
        if (n == 0) return Double.NaN;
        if ((n & 1) == 1) return tmp[n/2];
        else return (tmp[n/2 - 1] + tmp[n/2]) / 2.0;
    }

    // MAD (median absolute deviation) for filtered array
    private double madOfArrayFiltered(double[] arr, boolean[] mask, double median) {
        int n = 0;
        for (int i = 0; i < arr.length; i++) if (mask[i]) n++;
        if (n == 0) return Double.NaN;
        double[] dev = new double[n];
        int idx = 0;
        for (int i = 0; i < arr.length; i++) if (mask[i]) dev[idx++] = Math.abs(arr[i] - median);
        java.util.Arrays.sort(dev);
        if ((n & 1) == 1) return dev[n/2];
        else return (dev[n/2 - 1] + dev[n/2]) / 2.0;
    }

    // compute slope by ordinary least squares: power = k * rpm + c
    // filteredMask selects valid points
    private double computeSlopeFiltered(double[] rpm, double[] power, boolean[] filteredMask) {
        double sumR = 0.0, sumP = 0.0;
        int n = 0;
        for (int i = 0; i < rpm.length; i++) {
            if (!filteredMask[i]) continue;
            sumR += rpm[i];
            sumP += power[i];
            n++;
        }
        if (n < 2) return Double.NaN;
        double meanR = sumR / n;
        double meanP = sumP / n;
        double num = 0.0, den = 0.0;
        for (int i = 0; i < rpm.length; i++) {
            if (!filteredMask[i]) continue;
            double dr = rpm[i] - meanR;
            num += dr * (power[i] - meanP);
            den += dr * dr;
        }
        if (Math.abs(den) < 1e-12) return Double.NaN;
        return num / den;
    }

    // moving-average buffer helpers
    private void addRecentK(double k) {
        recentKs[recentIndex] = k;
        recentIndex = (recentIndex + 1) % sweepsToKeep;
        if (recentCount < sweepsToKeep) recentCount++;
    }

    private double meanOfRecent() {
        if (recentCount == 0) return Double.NaN;
        double s = 0.0;
        for (int i = 0; i < recentCount; i++) s += recentKs[i];
        return s / recentCount;
    }

    private double stddevOfRecent() {
        if (recentCount <= 1) return 0.0;
        double m = meanOfRecent();
        double s = 0.0;
        for (int i = 0; i < recentCount; i++) {
            double d = recentKs[i] - m;
            s += d * d;
        }
        return Math.sqrt(s / (recentCount - 1));
    }
}
