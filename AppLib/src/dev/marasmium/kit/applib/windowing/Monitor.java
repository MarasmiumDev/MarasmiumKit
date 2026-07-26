/**
 * File:        Monitor.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.14
 * Purpose:     Defines a data structure representing a display/monitor for a fullscreen window
 */

package dev.marasmium.kit.applib.windowing;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vec2D;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

/**
 * Data structure representing a monitor in the local graphics environment with a description, position, and dimensions
 */
public class Monitor {

    /**
     * The index of this monitor in the local graphics environment's array of screen devices
     */
    private int index;
    /**
     * The system-provided description string of this monitor
     */
    private String description;
    /**
     * The default position of this monitor in pixels in the local graphics environment
     */
    private Vec2D position;
    /**
     * The default dimensions of this monitor in pixels
     */
    private Vec2D dimensions;

    /**
     * Construct a default monitor from the first index in the local graphics environment's array of screen devices
     */
    public Monitor() {
        setIndex(0);
    }

    /**
     * Construct a monitor given its index in the local graphics environment's array of screen devices
     * @param index The index of this monitor
     */
    public Monitor(int index) {
        setIndex(index);
    }

    /**
     * Check that this monitor is still available to the system and update its reported description, position, and
     * dimensions. If this monitor's current index is unavailable this will revert the structure to representing the
     * first index in the array of screen devices
     * @return Whether the monitor was still available and was validated successfully
     */
    private boolean validate() {
        // Retrieve available monitors
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds;
        try {
            gds = ge.getScreenDevices();
        } catch (HeadlessException _) {
            App.Log.write(LogSource.Window, LogLevel.Error, "System is in headless mode");
            index = -1;
            description = "";
            position = null;
            dimensions = null;
            return false;
        }
        if (gds.length == 0) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Primary monitor unavailable");
            index = -1;
            description = "";
            position = null;
            dimensions = null;
            return false;
        }
        // Check index
        boolean success = true;
        if (index < 0 || index >= gds.length) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "Monitor index ", index, " invalid");
            index = 0;
            success = false;
        }
        // Retrieve monitor attributes
        position = Vec2D.Cartesian(gds[index].getDefaultConfiguration().getBounds().getX(),
                gds[index].getDefaultConfiguration().getBounds().getY());
        description = gds[index].getIDstring();
        dimensions = Vec2D.Cartesian(gds[index].getDefaultConfiguration().getBounds().getWidth(),
                gds[index].getDefaultConfiguration().getBounds().getHeight());
        return success;
    }

    /**
     * Validate that this monitor is still available and get its index in the local graphics environment's array of
     * screen devices. If this monitor's current index is unavailable this will revert the structure to representing the
     * first index in the array of screen devices
     * @return The current index of this monitor or 0
     */
    public int getIndex() {
        validate();
        return index;
    }

    /**
     * Set this monitor's index in the local graphics environment's array of screen devices. If the given index is
     * unavailable, default to the first index in the array
     * @param index The new monitor index for this monitor structure to represent
     * @return Whether the given index was valid
     */
    public boolean setIndex(int index) {
        this.index = index;
        return validate();
    }

    /**
     * Validate that this monitor is still available and get its system-provided description string. If this monitor is
     * unavailable this will revert the structure to representing the first index in the array of screen devices
     * @return This monitor's description string or the default's
     */
    public String getDescription() {
        validate();
        return description;
    }

    /**
     * Validate that this monitor is still available and get its position in pixels in the local graphics environment.
     * If this monitor is unavailable this will revert the structure to representing the first index in the array of
     * screen devices
     * @return This monitor's position or the default's
     */
    public Vec2D getPosition() {
        validate();
        return position;
    }

    /**
     * Validate that this monitor is still available and get its dimensions in pixels. If this monitor is unavailable
     * this will revert the structure to representing the first index in the array of screen devices
     * @return This monitor's dimensions or the default's
     */
    public Vec2D getDimensions() {
        validate();
        return dimensions;
    }

    /**
     * Validate that this monitor is still available and get its string representation including its index in the local
     * graphics environment's array of screen devices, system-provided description string, dimensions, and position in,
     * the local graphics environment. If this monitor is unavailable this will revert the structure to representing the
     * first index in the array of screen devices
     * @return This monitor's string representation
     */
    @Override
    public String toString() {
        return "monitor(" + getIndex() + ", \"" + getDescription() + "\", " + getDimensions() + ", " + getPosition()
                + ")";
    }
}
