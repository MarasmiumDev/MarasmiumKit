/**
 * File:        GraphicsManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines the main class of the MarasmiumKit application framework's graphics system
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

/**
 * The main class of the MarasmiumKit application framework's graphics system
 */
public class GraphicsManager {

    /**
     * The target (fractional) number of graphics frames to process per millisecond
     */
    private double targetFPMS = 0.0d;
    /**
     * The target number of milliseconds which should elapse per graphics frame
     */
    private int targetMSPF = 0;
    /**
     * The maximum number of logic updates allowed between graphics frames
     */
    private int maxUPF = 0;
    /**
     * The number of graphics frames to buffer before displaying
     */
    private int bufferCount = 0;

    /**
     * Initialize the application framework's graphics system
     * @param config Graphics system configuration structure
     * @return Whether the configuration was valid and the graphics system was initialized successfully
     */
    public boolean initialize(GraphicsManagerConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "No configuration provided for graphics system");
            return false;
        }
        if (!setTargetFPS(config.targetFPS)) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to initialize graphics system, initial target ",
                    "FPS invalid");
            return false;
        }
        if (!setMaxUPF(config.maxUPF)) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to initialize graphics system, initial maximum ",
                    "UPF invalid");
            return false;
        }
        if (!setBufferCount(config.bufferCount)) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to initialize graphics system buffer count");
            return false;
        }
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Initialized graphics system");
        return true;
    }

    public void draw() {

    }

    /**
     * Free the application framework graphics system's memory
     * @return Whether the graphics system was destroyed successfully
     */
    public boolean destroy() {
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Destroying graphics system");
        boolean success = true;
        targetFPMS = 0.0d;
        targetMSPF = 0;
        maxUPF = 0;
        bufferCount = 0;
        return success;
    }

    /**
     * Get the target (fractional) number of graphics frames to process per millisecond
     * @return The target number of frames per millisecond
     */
    public double getTargetFPMS() {
        return targetFPMS;
    }

    /**
     * Get the target number of milliseconds to elapse between graphics frames
     * @return The target number of milliseconds per frame
     */
    public int getTargetMSPF() {
        return targetMSPF;
    }

    /**
     * Get the target number of graphics frames to process per second
     * @return The target number of frames per second
     */
    public int getTargetFPS() {
        return (int)(1000.0d * targetFPMS);
    }

    /**
     * Set the target number of graphics frames to process per second
     * @param targetFPS The new target number of frames per second
     * @return Whether the given target FPS is valid
     */
    public boolean setTargetFPS(int targetFPS) {
        if (targetFPS <= 0) {
            App.Log.write(LogSource.Graphics, LogLevel.Warning, "Target FPS ", targetFPS, " invalid");
            return false;
        }
        targetFPMS = (double)targetFPS / 1000.0d;
        targetMSPF = (int)(1.0d / targetFPMS);
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Target FPS set to ", targetFPS, " -> FPMS=", targetFPMS, ", ",
                "MSPF=", targetMSPF);
        return true;
    }

    /**
     * Get the maximum number of logic updates allowed per graphics frame
     * @return The maximum number of logic updates per frame
     */
    public int getMaxUPF() {
        return maxUPF;
    }

    /**
     * Set the maximum number of logic updates allowed per graphics frame
     * @param maxUPF The new maximum number of logic updates per frame
     * @return Whether the given maximum UPF is valid
     */
    public boolean setMaxUPF(int maxUPF) {
        if (maxUPF <= 0) {
            App.Log.write(LogSource.Graphics, LogLevel.Warning, "Maximum UPF ", maxUPF, " invalid");
            return false;
        }
        this.maxUPF = maxUPF;
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Maximum UPF set to ", maxUPF);
        return true;
    }

    /**
     * Get tbe number of graphics frames to buffer before displaying
     * @return The number of graphics frames to buffer
     */
    public int getBufferCount() {
        return bufferCount;
    }

    /**
     * Set the number of graphics frames to buffer before displaying
     * @param bufferCount The new number of graphics frames to buffer
     * @return Whether the new buffer count was valid and was set successfully
     */
    public boolean setBufferCount(int bufferCount) {
        if (bufferCount <= 0) {
            App.Log.write(LogSource.Graphics, LogLevel.Warning, "Invalid buffer count");
            return false;
        }
        try {
            App.Window.getCanvas().createBufferStrategy(bufferCount);
        } catch (IllegalArgumentException | IllegalStateException _) {
            App.Log.write(LogSource.Graphics, LogLevel.Warning, "Failed to create frame buffer strategy");
            return false;
        }
        this.bufferCount = bufferCount;
        return true;
    }

}
