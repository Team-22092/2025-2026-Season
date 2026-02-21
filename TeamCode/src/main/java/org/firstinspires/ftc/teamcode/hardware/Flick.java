package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import java.util.ArrayList;
import java.util.List;

public class Flick {
    private NormalizedColorSensor C1, C2, C3;
    public Servo flick1, flick2, flick3;

    private static final double F1_DOWN = 1.0, F1_UP = 0.3;
    private static final double F2_DOWN = 0.0, F2_UP = 0.6;
    private static final double F3_DOWN = 1.0, F3_UP = 0.0;
    private static final double MOVE_TIME = 0.3;

    private final ElapsedTime timer = new ElapsedTime();
    private boolean busy = false;
    private List<Integer> shootOrder = new ArrayList<>();

    private enum State { IDLE, S1_UP, S1_DOWN, S2_UP, S2_DOWN, S3_UP, S3_DOWN }
    private State state = State.IDLE;

    public List<String> PATTERN = new ArrayList<>(List.of("G", "P", "P"));

    public Flick(HardwareMap hardwareMap) {
        flick1 = hardwareMap.get(Servo.class, "F1");
        flick2 = hardwareMap.get(Servo.class, "F2");
        flick3 = hardwareMap.get(Servo.class, "F3");

        flick1.setDirection(Servo.Direction.REVERSE);
        flick2.setDirection(Servo.Direction.REVERSE);
        flick3.setDirection(Servo.Direction.REVERSE);

        flick1.setPosition(F1_DOWN);
        flick2.setPosition(F2_DOWN);
        flick3.setPosition(F3_DOWN);

        C1 = hardwareMap.get(NormalizedColorSensor.class, "C1");
        C2 = hardwareMap.get(NormalizedColorSensor.class, "C2");
        C3 = hardwareMap.get(NormalizedColorSensor.class, "C3");
    }

    private void setFlickerPos(int index, boolean up) {
        if (index == 1) flick1.setPosition(up ? F1_UP : F1_DOWN);
        else if (index == 2) flick2.setPosition(up ? F2_UP : F2_DOWN);
        else if (index == 3) flick3.setPosition(up ? F3_UP : F3_DOWN);
    }

    public String getColor(NormalizedColorSensor sensor) {
        NormalizedRGBA colors = sensor.getNormalizedColors();

        if (colors.green > colors.red && colors.green > colors.blue) return "G";
        if (colors.blue > colors.red && colors.blue > colors.green) return "P";
        return "U";
    }

    public void FlickOpMode(Gamepad gamepad2, Gamepad oldGamepad2) {
        boolean pressed = (oldGamepad2 != null) && (gamepad2.b && !oldGamepad2.b);

        if (pressed && !busy) {
            shootOrder.clear();
            String[] detected = {getColor(C1), getColor(C2), getColor(C3)};
            boolean[] used = {false, false, false};


            for (String target : PATTERN) {
                for (int i = 0; i < 3; i++) {
                    if (!used[i] && detected[i].equals(target)) {
                        shootOrder.add(i + 1);
                        used[i] = true;
                        break;
                    }
                }
            }


            for (int i = 0; i < 3; i++) {
                if (!used[i]) {
                    shootOrder.add(i + 1);
                    used[i] = true;
                }
            }

            busy = true;
            state = State.S1_UP;
            setFlickerPos(shootOrder.get(0), true);
            timer.reset();
        }

        if (!busy) return;

        handleStateMachine();
    }

    private void handleStateMachine() {
        switch (state) {
            case S1_UP:
                if (timer.seconds() > MOVE_TIME) {
                    setFlickerPos(shootOrder.get(0), false);
                    state = State.S1_DOWN;
                    timer.reset();
                }
                break;
            case S1_DOWN:
                if (timer.seconds() > MOVE_TIME) {
                    setFlickerPos(shootOrder.get(1), true);
                    state = State.S2_UP;
                    timer.reset();
                }
                break;
            case S2_UP:
                if (timer.seconds() > MOVE_TIME) {
                    setFlickerPos(shootOrder.get(1), false);
                    state = State.S2_DOWN;
                    timer.reset();
                }
                break;
            case S2_DOWN:
                if (timer.seconds() > MOVE_TIME) {
                    setFlickerPos(shootOrder.get(2), true);
                    state = State.S3_UP;
                    timer.reset();
                }
                break;
            case S3_UP:
                if (timer.seconds() > MOVE_TIME) {
                    setFlickerPos(shootOrder.get(2), false);
                    state = State.S3_DOWN;
                    timer.reset();
                }
                break;
            case S3_DOWN:
                if (timer.seconds() > MOVE_TIME) {
                    state = State.IDLE;
                    busy = false;
                }
                break;
        }
    }

    public void SortTelem(Telemetry telemetry) {
        telemetry.addData("Shoot Order", shootOrder.toString());
        telemetry.addData("Sensors", "1:%s 2:%s 3:%s", getColor(C1), getColor(C2), getColor(C3));
    }
}