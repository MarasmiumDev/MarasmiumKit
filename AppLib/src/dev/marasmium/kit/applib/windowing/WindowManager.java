/**
 * File:        WindowManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.10
 * Purpose:     Defines the main class of application framework's windowing system
 */

package dev.marasmium.kit.applib.windowing;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import javax.swing.SwingUtilities;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The main class of the MarasmiumKit application framework's windowing system
 */
public class WindowManager {

    /**
     * Short time to wait before moving on from AWT frame sizing operations
     */
    private static final int PauseTime = 100;

    /**
     * The title to appear on the window when in windowed mode
     */
    private String title = null;
    /**
     * The current dimensions of the window in pixels
     */
    private final Vector dimensions = Vector.Zero();
    /**
     * The dimensions of the window in pixels when in windowed mode
     */
    private final Vector windowedDimensions = Vector.Zero();
    /**
     * Whether the window is currently in fullscreen mode
     */
    private boolean fullscreen = false;
    /**
     * The graphics device (monitor) which the window is displayed on when in fullscreen mode
     */
    private GraphicsDevice fullscreenDevice = null;
    /**
     * The monitor for the window to appear on when in fullscreen mode
     */
    private Monitor monitor = null;
    /**
     * Whether the mouse cursor is visible on the window
     */
    private boolean mouseCursorVisible = false;
    /**
     * The Java AWT window handle for the window
     */
    private Frame frame = null;
    /**
     * The JOGL canvas to draw graphics on in the window
     */
    private GLCanvas canvas = null;
    /**
     * Whether the system has requested for the window to close
     */
    private volatile boolean closeRequested = false;

    /**
     * Initialize the MarasmiumKit application framework's window
     * @param title The initial title to appear on the window
     * @param dimensions The initial dimensions of the window when in windowed mode in pixels
     * @param monitor The initial monitor for the window to appear on when in fullscreen mode
     * @param fullscreen Whether the window should initially appear in fullscreen mode
     * @param mouseCursorVisible Whether the mouse cursor will be initially visible on the window
     * @return Whether the parameters were valid and the window was initialized successfully
     */
    public boolean initialize(String title, Vector dimensions, Monitor monitor, boolean fullscreen,
                              boolean mouseCursorVisible) {
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new Frame();
                frame.setResizable(false);
                frame.addWindowListener(new WindowAdapter() {

                    /**
                     * Callback for window close events
                     *
                     * @param e The event to be processed
                     */
                    @Override
                    public void windowClosing(WindowEvent e) {
                        closeRequested = true;
                    }

                });
                try {
                    canvas = new GLCanvas(new GLCapabilities(GLProfile.get(GLProfile.GL3)));
                } catch (GLException _) {
                    App.Log.write(LogSource.Window, LogLevel.Error, "Failed to initialize JOGL canvas");
                }
                canvas.addKeyListener(App.Input.keyboard);
                canvas.addMouseListener(App.Input.mouse);
                canvas.addMouseMotionListener(App.Input.mouse);
                canvas.addMouseWheelListener(App.Input.mouse);
                try {
                    canvas.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
                    canvas.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.emptySet());
                } catch (IllegalArgumentException _) {
                    App.Log.write(LogSource.Window, LogLevel.Error, "Failed to disable focus tabbing");
                    return;
                }
                frame.add(canvas);
                frame.setVisible(true);
                canvas.requestFocus();
                App.Log.write(LogSource.Window, LogLevel.Info, "Generated new window frame");
            });
        } catch (InterruptedException | InvocationTargetException _) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to initialize AWT frame");
            return false;
        }
        if (!setTitle(title)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window title");
            return false;
        }
        setMonitor(monitor);
        if (!setDimensions(dimensions)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window dimensions");
            return false;
        }
        if (!setFullscreen(fullscreen)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set window fullscreen mode");
            return false;
        }
        if (!setMouseCursorVisible(mouseCursorVisible)) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set mouse cursor visibility");
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
        title = null;
        fullscreen = false;
        if (monitor != null) {
            monitor.destroy();
            monitor = null;
        }
        if (canvas != null) {
            canvas.destroy();
            canvas = null;
        }
        if (frame != null) {
            frame.dispose();
            frame = null;
        }
        closeRequested = false;
        return success;
    }

    /**
     * Get the title appearing on the window when in windowed mode
     * @return The title of the window
     */
    public String getTitle() {
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
        this.title = title;
        try {
            SwingUtilities.invokeAndWait(() -> frame.setTitle(title));
        } catch (InterruptedException | InvocationTargetException _) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set title");
            return false;
        }
        App.Log.write(LogSource.Window, LogLevel.Info, "Set window title \"", title, "\"");
        return true;
    }

    /**
     * Get the current dimensions of the window in pixels
     * @return The current dimensions of the window
     */
    public Vector getDimensions() {
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
        windowedDimensions.setX(dimensions.getX());
        windowedDimensions.setY(dimensions.getY());
        if (fullscreen) {
            App.Log.write(LogSource.Window, LogLevel.Warning, "Can't directly set dimensions in fullscreen mode");
            return true;
        }
        try {
            SwingUtilities.invokeAndWait(() -> {
                canvas.setPreferredSize(new Dimension((int) dimensions.getX(), (int) dimensions.getY()));
                canvas.setSize((int) dimensions.getX(), (int) dimensions.getY());
                frame.pack();
                Monitor monitor = getMonitor();
                if (monitor != null) {
                    Vector monitorPosition = getMonitor().getPosition();
                    Vector monitorDimensions = getMonitor().getDimensions();
                    Vector windowPosition = monitorPosition
                            .add(monitorDimensions.scalarDivide(2.0f))
                            .subtract(dimensions.scalarDivide(2.0f));
                    frame.setLocation((int) windowPosition.getX(), (int) windowPosition.getY());
                }
                App.Log.write(LogSource.Window, LogLevel.Info, "Set window dimensions ", dimensions);
            });
        } catch (InterruptedException | InvocationTargetException _) {
            App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set AWT window dimensions");
            return false;
        }
        this.dimensions.setX(canvas.getWidth());
        this.dimensions.setY(canvas.getHeight());
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
            monitors.add(new Monitor(index));
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
            App.Log.write(LogSource.Window, LogLevel.Warning, "No monitor provided");
            return;
        }
        this.monitor = monitor;
        if (fullscreen) {
            setFullscreen(false);
            setFullscreen(true);
        } else {
            setDimensions(dimensions);
        }
        App.Log.write(LogSource.Window, LogLevel.Info, "Set fullscreen monitor index ", monitor);
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
        if (fullscreen && !this.fullscreen) {
            Vector windowedDimensions = this.windowedDimensions.clone();
            setDimensions(Vector.Cartesian(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight()));
            this.windowedDimensions.setX(windowedDimensions.getX());
            this.windowedDimensions.setY(windowedDimensions.getY());
            this.fullscreen = true;
            try {
                SwingUtilities.invokeAndWait(() -> {
                    gd.setFullScreenWindow(frame);
                    fullscreenDevice = gd;
                });
                Thread.sleep(PauseTime);
            } catch (InterruptedException | InvocationTargetException _) {
                App.Log.write(LogSource.Window, LogLevel.Error, "Failed to set fullscreen monitor");
                return false;
            }
            App.Log.write(LogSource.Window, LogLevel.Info, "Set window to fullscreen mode");
        } else if (!fullscreen && this.fullscreen) {
            if (fullscreenDevice == null) {
                return false;
            }
            try {
                SwingUtilities.invokeAndWait(() -> {
                    fullscreenDevice.setFullScreenWindow(null);
                    fullscreenDevice = null;
                });
                Thread.sleep(PauseTime);
            } catch (InterruptedException | InvocationTargetException _) {
                App.Log.write(LogSource.Window, LogLevel.Error, "Failed to remove fullscreen monitor");
                return false;
            }
            this.fullscreen = false;
            setDimensions(windowedDimensions);
            App.Log.write(LogSource.Window, LogLevel.Info, "Set window to windowed mode");
        }
        return true;
    }

    /**
     * Test whether the mouse cursor is visible on the window
     * @return Whether the mouse cursor is currently visible
     */
    public boolean isMouseCursorVisible() {
        return mouseCursorVisible;
    }

    /**
     * Set whether the mouse cursor is visible on the window
     * @param mouseCursorVisible Whether the mouse cursor should be visible
     */
    public boolean setMouseCursorVisible(boolean mouseCursorVisible) {
        if (mouseCursorVisible) {
            canvas.setCursor(Cursor.getDefaultCursor());
            App.Log.write(LogSource.Window, LogLevel.Info, "Set mouse cursor visible");
        } else {
            try {
                canvas.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
                        new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "invisible"));
            } catch (IndexOutOfBoundsException | HeadlessException _) {
                App.Log.write(LogSource.Window, LogLevel.Warning, "Failed to set mouse cursor invisible");
                return false;
            }
            App.Log.write(LogSource.Window, LogLevel.Info, "Set mouse cursor invisible");
        }
        this.mouseCursorVisible = mouseCursorVisible;
        return true;
    }

    /**
     * Get the AWT frame for the window
     * @return The AWT frame for the window
     */
    public Frame getFrame() {
        return frame;
    }

    /**
     * Get the JOGL canvas for the window
     * @return The JOGL canvas for the window
     */
    public GLCanvas getCanvas() {
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
