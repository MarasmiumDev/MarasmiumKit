/**
 * File:        Monitor.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.14
 * Purpose:     Defines a data structure representing a display/monitor for a fullscreen window
 */

package dev.marasmium.kit.applib.windowing;

import dev.marasmium.kit.applib.data.Vector;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

/**
 * Data structure representing a monitor in the local graphics environment with a description, position, and dimensions
 */
public class Monitor implements Cloneable {

    /**
     * The index of this monitor in the local graphics environment's array of screen devices
     */
    private int index = 0;
    /**
     * The system-provided description string of this monitor
     */
    private String description = null;
    /**
     * The default position of this monitor in pixels in the local graphics environment
     */
    private Vector position = null;
    /**
     * The default dimensions of this monitor in pixels
     */
    private Vector dimensions = null;

    /**
     * Create an monitor with an index and validate it
     * @param index The index of this monitor in the local graphics environment's array of screen devices
     */
    public Monitor(int index) {
        setIndex(index);
    }

    /**
     * Free this monitor's memory
     */
    public void destroy() {
        index = 0;
        description = null;
        position = null;
        dimensions = null;
    }

    /**
     * Check that this monitor is still available to the system and update its reported description, position, and
     * dimensions and change the index to the default if unavailable
     * @return Whether the monitor was still available and was validated successfully
     */
    private boolean validate() {
        // Retrieve available monitors
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds;
        try {
            gds = ge.getScreenDevices();
        } catch (HeadlessException _) {
            index = -1;
            description = "";
            position = null;
            dimensions = null;
            return false;
        }
        if (gds.length == 0) {
            index = -1;
            description = "";
            position = null;
            dimensions = null;
            return false;
        }
        // Check index
        boolean success = true;
        if (index < 0 || index >= gds.length) {
            index = 0;
            success = false;
        }
        // Retrieve monitor attributes
        position = Vector.Cartesian((float)gds[index].getDefaultConfiguration().getBounds().getX(),
                (float)gds[index].getDefaultConfiguration().getBounds().getY());
        description = gds[index].getIDstring();
        dimensions = Vector.Cartesian((float)gds[index].getDefaultConfiguration().getBounds().getWidth(),
                (float)gds[index].getDefaultConfiguration().getBounds().getHeight());
        return success;
    }

    /**
     * Convert this monitor to a string
     * @return This monitor's string representation
     */
    @Override
    public String toString() {
        if (description == null || dimensions == null || position == null) {
            return "monitor(null)";
        }
        return "monitor(index " + index + ", \"" + description + "\", dimensions " + dimensions + ", position "
                + position + ")";
    }

    /**
     * Test whether this monitor represents the same device as another monitor
     * @param o The object to compare this monitor against (must be an instance of Monitor)
     * @return Whether this monitor has the same index as the given monitor
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Monitor monitor)) {
            return false;
        }
        return index == monitor.index;
    }

    /**
     * Make a copy of this monitor containing the same data
     * @return A copy of this monitor
     */
    @Override
    public Monitor clone() {
        Monitor monitor;
        try {
            monitor = (Monitor)super.clone();
        } catch (CloneNotSupportedException _) {
            return null;
        }
        monitor.index = index;
        monitor.description = description;
        if (position != null) {
            monitor.position = position.clone();
        }
        if (dimensions != null) {
            monitor.dimensions = dimensions.clone();
        }
        return monitor;
    }

    /**
     * Validate that this monitor is still available and get its index in the local graphics environment's array of
     * screen devices
     * @return The current index of this monitor or 0
     */
    public int getIndex() {
        validate();
        return index;
    }

    /**
     * Set this monitor's index in the local graphics environment's array of screen devices
     * @param index The new monitor index for this monitor structure to represent
     * @return Whether the given index was valid
     */
    public boolean setIndex(int index) {
        this.index = index;
        return validate();
    }

    /**
     * Validate that this monitor is still available and get its system-provided description string
     * @return This monitor's description string or the default's
     */
    public String getDescription() {
        validate();
        return description;
    }

    /**
     * Validate that this monitor is still available and get its position in pixels in the local graphics environment
     * @return This monitor's position or the default's
     */
    public Vector getPosition() {
        validate();
        return position;
    }

    /**
     * Validate that this monitor is still available and get its dimensions in pixels
     * @return This monitor's dimensions or the default's
     */
    public Vector getDimensions() {
        validate();
        return dimensions;
    }

}
