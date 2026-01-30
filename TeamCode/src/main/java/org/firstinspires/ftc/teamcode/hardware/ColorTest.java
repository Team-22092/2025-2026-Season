package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

/**
 * ColorTest (static white range)
 *
 * Behavior:
 *  - Background (layer 0): all LEDs solid RED
 *  - LEDs 18–24 (layer 1): solid WHITE
 *  - No animation, no movement, no state logic
 */
public class ColorTest {

    private final GoBildaPrismDriver prism;

    // Animations
    private final PrismAnimations.Solid backgroundAnim; // LAYER_0
    private final PrismAnimations.Solid whiteRangeAnim; // LAYER_1

    // LED range
    private static final int START_INDEX = 18;
    private static final int END_INDEX   = 24;

    // Layers
    private static final GoBildaPrismDriver.LayerHeight BG_LAYER = GoBildaPrismDriver.LayerHeight.LAYER_0;
    private static final GoBildaPrismDriver.LayerHeight RG_LAYER = GoBildaPrismDriver.LayerHeight.LAYER_1;

    // Colors
    private static final Color RED   = new Color(255, 0, 0);
    private static final Color WHITE = new Color(255, 255, 255);

    public ColorTest(HardwareMap hardwareMap, Telemetry telemetry) {
        prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");

        try {
            prism.enableDefaultBootArtboard(false);
            prism.clearAllAnimations();
        } catch (Exception ignored) {}

        int totalLEDs = Math.max(1, prism.getNumberOfLEDs());

        // Background: solid RED
        backgroundAnim = new PrismAnimations.Solid();
        backgroundAnim.setStartIndex(0);
        backgroundAnim.setStopIndex(totalLEDs - 1);
        backgroundAnim.setPrimaryColor(RED);
        backgroundAnim.setBrightness(100);
        prism.insertAndUpdateAnimation(BG_LAYER, backgroundAnim);

        // LEDs 18–24: solid WHITE
        whiteRangeAnim = new PrismAnimations.Solid();
        whiteRangeAnim.setStartIndex(START_INDEX);
        whiteRangeAnim.setStopIndex(END_INDEX);
        whiteRangeAnim.setPrimaryColor(WHITE);
        whiteRangeAnim.setBrightness(100);
        prism.insertAndUpdateAnimation(RG_LAYER, whiteRangeAnim);

        if (telemetry != null) {

            telemetry.update();
        }
    }


    public void COLOR() {
       //@hack needs to have something in this (todo telemetry?)
    }
}
