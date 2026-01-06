package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

/**
 * ColorTest (reworked)
 *
 * Behavior:
 *  - Background (layer 0): all LEDs solid RED
 *  - Range (layer 1): when centered==true, the configured start..end range glows GREEN
 *  - Dot (layer 2): when centered==false, a single red dot moves inside start..end
 *
 * Implementation details:
 *  - Three Solid animations are inserted once (one per layer). After insertion we only call
 *    updateAnimationFromIndex(...) when a change is required (minimizes I2C traffic and flicker).
 *  - Use COLOR() from your periodic loop after updating `centered` and `moveRight`.
 */
public class ColorTest {

    private final GoBildaPrismDriver prism;

    // Animations for each layer
    private final PrismAnimations.Solid backgroundAnim; // LAYER_0
    private final PrismAnimations.Solid rangeAnim;      // LAYER_1
    private final PrismAnimations.Solid dotAnim;        // LAYER_2

    // LED indices for the moving-dot region (inclusive)
    private int startIndex = 24;
    private int endIndex   = 30;

    // dot state
    private int position = startIndex;
    private int lastSentPosition = Integer.MIN_VALUE;

    // background tracking (avoid redundant writes)
    private int lastBackgroundRgb = Integer.MIN_VALUE;

    // range tracking
    private boolean rangeVisible = false;
    private int lastRangeRgb = Integer.MIN_VALUE;

    // mode flags (set externally)
    public boolean moveRight = true;
    public boolean centered = false;

    // timing
    private long stepIntervalMs = 150;
    private long lastStepTimeMs = 0;

    // Layers
    private static final GoBildaPrismDriver.LayerHeight BG_LAYER  = GoBildaPrismDriver.LayerHeight.LAYER_0;
    private static final GoBildaPrismDriver.LayerHeight RG_LAYER  = GoBildaPrismDriver.LayerHeight.LAYER_1;
    private static final GoBildaPrismDriver.LayerHeight DOT_LAYER = GoBildaPrismDriver.LayerHeight.LAYER_2;

    // Default colors
    private static final Color RED   = new Color(255, 0, 0 );
    private static final Color GREEN = new Color(0, 255, 0);


    /**
     * Constructor - inserts three animations (background, range, dot) once.
     */
    public ColorTest(HardwareMap hardwareMap, Telemetry telemetry) {
        prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");

        // try to disable default artboard and clear animations to avoid conflicts
        try {
            prism.enableDefaultBootArtboard(false);
            prism.clearAllAnimations();
        } catch (Exception ignored) {}

        // Background: full strip, initially RED
        backgroundAnim = new PrismAnimations.Solid();
        int totalLEDs = Math.max(1, prism.getNumberOfLEDs()); // defensive in case device reports 0
        backgroundAnim.setStartIndex(0);
        backgroundAnim.setStopIndex(totalLEDs - 1);
        backgroundAnim.setPrimaryColor(RED);
        backgroundAnim.setBrightness(100);
        prism.insertAndUpdateAnimation(BG_LAYER, backgroundAnim);
        lastBackgroundRgb = packRgb(255, 0, 0);

        // Range animation (green when visible). Start hidden (brightness 0).
        rangeAnim = new PrismAnimations.Solid();
        rangeAnim.setStartIndex(startIndex);
        rangeAnim.setStopIndex(endIndex);
        rangeAnim.setPrimaryColor(GREEN);
        rangeAnim.setBrightness(0); // hidden initially
        prism.insertAndUpdateAnimation(RG_LAYER, rangeAnim);
        rangeVisible = false;
        lastRangeRgb = packRgb(0, 0, 0);

        // Dot animation (foreground). Insert visible (single LED).
        dotAnim = new PrismAnimations.Solid();
        dotAnim.setPrimaryColor(RED);
        dotAnim.setBrightness(100);
        position = clamp(position, startIndex, endIndex);
        dotAnim.setStartIndex(position);
        dotAnim.setStopIndex(position);
        prism.insertAndUpdateAnimation(DOT_LAYER, dotAnim);
        lastSentPosition = position;

        if (telemetry != null) {
            telemetry.addLine("ColorTest initialized (BG: L0, Range: L1, Dot: L2)");
            telemetry.addData("LEDs total", totalLEDs);
            telemetry.addData("Range", String.format("%d-%d", startIndex, endIndex));
            telemetry.addData("Start pos", position);
            telemetry.update();
        }
    }

    /** Call this each loop after updating centered and moveRight. */
    public void COLOR() {
        long now = System.currentTimeMillis();
        if (lastStepTimeMs == 0) lastStepTimeMs = now;

        // Ensure background stays RED (only write if color changed)
        if (lastBackgroundRgb != packRgb(255, 0, 0)) {
            backgroundAnim.setPrimaryColor(RED);
            backgroundAnim.setBrightness(100);
            prism.updateAnimationFromIndex(BG_LAYER);
            lastBackgroundRgb = packRgb(255, 0, 0);
        }

        // If centered: show the green range and hide the dot
        if (centered) {
            if (!rangeVisible || lastRangeRgb != packRgb(0, 255, 0)) {
                rangeAnim.setPrimaryColor(GREEN);
                rangeAnim.setBrightness(100);
                prism.updateAnimationFromIndex(RG_LAYER);
                rangeVisible = true;
                lastRangeRgb = packRgb(0, 255, 0);
            }
            // hide dot (if visible)
            if (dotAnim.getBrightness() != 0) {
                dotAnim.setBrightness(0);
                prism.updateAnimationFromIndex(DOT_LAYER);
            }
            // nothing else while centered
            return;
        }

        // Not centered: ensure range is hidden and dot visible
        if (rangeVisible) {
            rangeAnim.setBrightness(0);
            prism.updateAnimationFromIndex(RG_LAYER);
            rangeVisible = false;
            lastRangeRgb = packRgb(0, 0, 0);
        }

        if (dotAnim.getBrightness() == 0) {
            dotAnim.setBrightness(100);
            prism.updateAnimationFromIndex(DOT_LAYER);
        }

        // Rate-limit stepping
        if (now - lastStepTimeMs < stepIntervalMs) {
            return;
        }

        // Move dot (bounce behavior)
        if (moveRight) {
            if (position < endIndex) {
                position++;
            } else {
                moveRight = false;
                position = Math.max(startIndex, endIndex - 1);
            }
        } else {
            if (position > startIndex) {
                position--;
            } else {
                moveRight = true;
                position = Math.min(endIndex, startIndex + 1);
            }
        }
        lastStepTimeMs = now;

        // Update dot only when it changed
        if (position != lastSentPosition) {
            dotAnim.setPrimaryColor(RED);
            dotAnim.setStartIndex(position);
            dotAnim.setStopIndex(position);
            prism.updateAnimationFromIndex(DOT_LAYER);
            lastSentPosition = position;
        }
    }

    // Configuration helpers

    /** Set the subrange the moving dot may occupy. */
    public void setLedRange(int start, int end) {
        if (end <= start) throw new IllegalArgumentException("end must be > start");
        startIndex = start;
        endIndex = end;
        // update rangeAnim indices
        rangeAnim.setStartIndex(startIndex);
        rangeAnim.setStopIndex(endIndex);
        // clamp position and update dot
        position = clamp(position, startIndex, endIndex);
        dotAnim.setStartIndex(position);
        dotAnim.setStopIndex(position);
        prism.updateAnimationFromIndex(RG_LAYER);
        prism.updateAnimationFromIndex(DOT_LAYER);
        lastSentPosition = position;
    }

    public void setStepIntervalMs(long ms) {
        stepIntervalMs = Math.max(10, ms);
    }

    public void jumpToIndex(int idx) {
        position = clamp(idx, startIndex, endIndex);
        dotAnim.setStartIndex(position);
        dotAnim.setStopIndex(position);
        dotAnim.setBrightness(100);
        prism.updateAnimationFromIndex(DOT_LAYER);
        lastSentPosition = position;
    }

    // utilities

    private static int packRgb(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private int clamp(int x, int lo, int hi) {
        if (x < lo) return lo;
        if (x > hi) return hi;
        return x;
    }
}