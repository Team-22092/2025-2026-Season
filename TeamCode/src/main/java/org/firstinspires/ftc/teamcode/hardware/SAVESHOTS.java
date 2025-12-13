// SAVESHOTS.java
package org.firstinspires.ftc.teamcode.hardware;

import android.content.Context;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SAVESHOTS {
    private final File savesFile;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public SAVESHOTS(HardwareMap hardwareMap) {
        Context ctx = hardwareMap.appContext;
        savesFile = new File(ctx.getExternalFilesDir(null), "saveshots.txt");

        try {
            if (!savesFile.exists()) {
                savesFile.createNewFile();
                appendLine("timestamp,power,hood,distance");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void appendLine(String line) {
        try (FileWriter fw = new FileWriter(savesFile, true)) {
            fw.append(line).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveShot(double wheelPower, double hoodPosition, double distanceMeters) {
        String ts = sdf.format(new Date());
        String line = String.format(Locale.US, "%s,%.4f,%.4f,%.3f", ts, wheelPower, hoodPosition, distanceMeters);
        appendLine(line);
    }

    public void IntakeOpMode(Gamepad gamepad2, ShootWheels shootWheels) {
        if (gamepad2.touchpad) {
            saveShot(shootWheels.WHEEL.getPower(), ShootWheels.hood.getPosition(), ShootWheels.distance);
        }
    }
}
