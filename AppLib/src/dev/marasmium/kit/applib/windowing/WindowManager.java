/**
 * File:        WindowManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.10
 * Purpose:     Defines the main class of application framework's windowing system
 */

package dev.marasmium.kit.applib.windowing;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The main class of the MarasmiumKit application framework's windowing system
 */
public class WindowManager {

    /**
     * The title to appear on the window when in windowed mode
     */
    private String title = null;
    /**
     * The current dimensions of the window in pixels
     */
    private Vector dimensions = null;
    /**
     * The dimensions of the window in pixels when in windowed mode
     */
    private Vector windowedDimensions = null;
    /**
     * Whether the window is currently in fullscreen mode
     */
    private boolean fullscreen = false;
    /**
     * The monitor for the window to appear on when in fullscreen mode
     */
    private Monitor monitor = null;
    /**
     * The Java AWT window handle for the window
     */
    private Frame frame = null;
    /**
     * Canvas used to draw graphics on the window
     */
    private Canvas canvas = null;
    /**
     * Whether the system has requested for the window to close
     */
    private volatile boolean closeRequested = false;

    /**
     * Open the application framework's window and set its parameters
     * @param config The title, dimensions, fullscreen mode, and monitor parameters for the initial window to be opened
     * @return Whether the initial window parameters were valid and the window opened successfully
     */
    public boolean initialize(WindowManagerConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Window, LogLevel.Error, "No configuration provided");
            return false;
        }
        // Set monitor and fullscreen mode
        setMonitor(config.monitor);
        if (!setFullscreen(config.fullscreen)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window fullscreen mode");
            return false;
        }
        // Set title for windowed mode
        if (!setTitle(config.title)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window title");
            return false;
        }
        // Set dimensions for windowed mode
        if (!setDimensions(config.dimensions)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window dimensions");
            return false;
        }
        closeRequested = false;
        App.Log.write(LogSource.Window, LogLevel.Info, "Initialized windowing system");
        return true;
    }

    /**
     * Close the application framework's window and clear the windowing system's memory
     * @return Whether the windowing system was destroyed safely
     */
    public boolean destroy() {
        boolean success = true;
        App.Log.write(LogSource.Window, LogLevel.Info, "Destroying windowing system");
        // Free window parameter memory
        title = null;
        dimensions = null;
        windowedDimensions = null;
        fullscreen = false;
        if (monitor != null) {
            monitor.destroy();
            monitor = null;
        }
        // Close the window
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
        canvas = null;
        closeRequested = false;
        return success;
    }

    /**
     * Close the old frame if present and generate a new one for switching between fullscreen and windowed mode
     */
    private void regenerateFrame() {
        // Close the old frame
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
        // Generate new Java AWT window framework
        frame = new Frame();
        if (canvas == null) {
            canvas = new Canvas();
        }
        frame.add(canvas);
        // Set basic window parameters
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {

            /**
             * Callback for window close events
             * @param e The event to be processed
             */
            @Override
            public void windowClosing(WindowEvent e) {
                closeRequested = true;
            }

        });
        App.Log.write(LogSource.Window, LogLevel.Info, "Generated new window frame");
    }

    /**
     * Get the title appearing on the window when in windowed mode
     * @return The title of the window
     */
    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    /**
     * Set the title to appear on the window when in windowed mode
     * @param title The new title for the window
     * @return Whether the new title could be set
     */
    public boolean setTitle(String title) {
        if (title == null) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "No title provided");
            return false;
        }
        if (frame == null) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "Failed to set window title, not initialized");
            return false;
        }
        frame.setTitle(title);
        this.title = title;
        App.Log.write(LogSource.Window, LogLevel.Info, "Set window title \"", title, "\"");
        return true;
    }

    /**
     * Get the current dimensions of the window in pixels
     * @return The current dimensions of the window
     */
    public Vector getDimensions() {
        if (dimensions == null) {
            return Vector.Cartesian(0.0d, 0.0d);
        }
        return dimensions;
    }

    /**
     * Set the dimensions of the window or the dimensions for the next time it appears in windowed mode and relocate the
     * window to the centre of the current monitor
     * @param dimensions The new dimensions of the window in pixels
     * @return Whether the window dimensions could be set
     */
    public boolean setDimensions(Vector dimensions) {
        if (dimensions == null) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "No dimensions provided");
            return false;
        }
        if (frame == null || canvas == null) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "Failed to set window dimensions, not initialized");
            return false;
        }
        // Update windowed mode dimensions
        windowedDimensions = dimensions;
        if (fullscreen) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "Can't directly set dimensions in fullscreen mode");
            return true;
        }
        // Set current window dimensions (in windowed mode)
        canvas.setPreferredSize(new Dimension((int)dimensions.getX(), (int)dimensions.getY()));
        canvas.setMinimumSize(new Dimension((int)dimensions.getX(), (int)dimensions.getY()));
        canvas.setMaximumSize(new Dimension((int)dimensions.getX(), (int)dimensions.getY()));
        canvas.setSize((int)dimensions.getX(), (int)dimensions.getY());
        frame.pack();
        // Set window location on current monitor
        Vector monitorPosition = getMonitor().getPosition();
        Vector monitorDimensions = getMonitor().getDimensions();
        Vector windowPosition = monitorPosition
                .add(monitorDimensions.scalarDivide(2.0d))
                .subtract(dimensions.scalarDivide(2.0d));
        frame.setLocation((int)windowPosition.getX(), (int)windowPosition.getY());
        this.dimensions = dimensions;
        App.Log.write(LogSource.Window, LogLevel.Info, "Set window dimensions ", dimensions);
        return true;
    }

    /**
     * Test whether the window is currently appearing in fullscreen mode
     * @return Whether the window is in fullscreen mode
     */
    public boolean isFullscreen() {
        return fullscreen;
    }

    /**
     * Set whether the window should appear in fullscreen mode and switch modes if this differs from the current mode
     * @param fullscreen Whether the window should appear in fullscreen mode
     * @return Whether the window switched modes (if necessary) successfully
     */
    public boolean setFullscreen(boolean fullscreen) {
        regenerateFrame();
        if (fullscreen) {
            // Switch to fullscreen mode (select monitor and validate)
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gds;
            try {
                gds = ge.getScreenDevices();
            } catch (HeadlessException _) {
                App.Log.write(LogSource.Window, LogLevel.Error, "System is in headless mode");
                return false;
            }
            GraphicsDevice gd = gds[monitor.getIndex()];
            if (!gd.isFullScreenSupported()) {
                App.Log.write(LogSource.Window, LogLevel.Error, "Fullscreen not supported on current monitor");
                return false;
            }
            // Set to fullscreen on appropriate monitor in update current dimensions and windowed mode dimensions
            gd.setFullScreenWindow(frame);
            Vector windowedDimensions = this.windowedDimensions;
            if (!setDimensions(Vector.Cartesian(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight()))) {
                App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window dimensions for fullscreen ",
                        "monitor");
                return false;
            }
            this.windowedDimensions = windowedDimensions;
            this.fullscreen = true;
            App.Log.write(LogSource.Window, LogLevel.Info, "Set window to fullscreen mode");
        } else {
            // Switch to windowed mode (reset title and windowed mode dimensions)
            this.fullscreen = false;
            setTitle(title);
            setDimensions(windowedDimensions);
            App.Log.write(LogSource.Window, LogLevel.Info, "Set window to windowed mode");
        }
        frame.setVisible(true);
        try {
            frame.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
            frame.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet());
        } catch (IllegalArgumentException _) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to disable focus tabbing");
            return false;
        }
        canvas.requestFocus();
        return true;
    }

    /**
     * Get the set of monitors currently available in the local graphics environment
     * @return The currently available set of monitors
     */
    public ArrayList<Monitor> getMonitors() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds;
        try {
            gds = ge.getScreenDevices();
        } catch (HeadlessException _) {
            App.Log.write(LogSource.Window, LogLevel.Error, "System is in headless mode");
            return new ArrayList<>();
        }
        ArrayList<Monitor> monitors = new ArrayList<>();
        for (int index = 0; index < gds.length; index++) {
            Monitor monitor = new Monitor();
            if (monitor.initialize(index)) {
                monitors.add(monitor);
            }
        }
        return monitors;
    }

    /**
     * Get the current monitor for the window to be displayed on when in fullscreen mode
     * @return The current monitor for the window to be displayed in fullscreen mode on
     */
    public Monitor getMonitor() {
        return monitor;
    }

    /**
     * Set the monitor for the window to appear on when in fullscreen mode
     * @param monitor The new monitor for the window to appear on when in fullscreen mode
     */
    public void setMonitor(Monitor monitor) {
        if (monitor == null) {
            return;
        }
        this.monitor = monitor;
        if (fullscreen) {
            setFullscreen(false);
            setFullscreen(true);
        }
        App.Log.write(LogSource.Window, LogLevel.Info, "Set fullscreen monitor index ", monitor);
    }

    /**
     * Get the window's Java AWT canvas for drawing graphics
     * @return The window panel
     */
    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * Test whether the user/system has requested for the window to close
     * @return Whether the window (and application) should close
     */
    public boolean isCloseRequested() {
        return closeRequested;
    }

}
