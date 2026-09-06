/**
 * File:        GraphicsManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines the main class of the MarasmiumKit application framework's graphics system
 */

package dev.marasmium.kit.applib.graphics;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.util.concurrent.locks.ReentrantLock;

/**
 * The main class of the MarasmiumKit application framework's graphics system
 */
public class GraphicsManager implements GLEventListener {

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
     * The colour to clear the window to each frame
     */
    private Colour clearColour = null;
    /**
     * Scope lock for thread-safety modifying/reading the window clear colour
     */
    private final ReentrantLock clearColourLock = new ReentrantLock();

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
        if (!setClearColour(config.clearColour)) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to initialize graphics system, initial clear ",
                    "colour invalid");
            return false;
        }
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Initialized graphics system");
        return true;
    }

    public void beginFrame() {

    }

    public void endFrame() {

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
        clearColour = null;
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
     * Get the colour to clear the window to each frame
     * @return The graphics system's clear colour
     */
    public Colour getClearColour() {
        clearColourLock.lock();
        Colour clearColour = this.clearColour;
        try {
            clearColourLock.unlock();
        } catch (IllegalMonitorStateException _) {
            return null;
        }
        return clearColour;
    }

    /**
     * Set the colour to clear the window to each frame
     * @param clearColour The new clear colour
     * @return Whether the given clear colour was valid
     */
    public boolean setClearColour(Colour clearColour) {
        if (clearColour == null) {
            return false;
        }
        clearColourLock.lock();
        this.clearColour = clearColour;
        try {
            clearColourLock.unlock();
        } catch (IllegalMonitorStateException _) {
            return false;
        }
        return true;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl3 = drawable.getGL().getGL3();
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Initializing OpenGL parameters");
        String OpenGLVersion = gl3.glGetString(GL3.GL_VERSION);
        App.Log.write(LogSource.Graphics, LogLevel.Info, "OpenGL version \"", OpenGLVersion, "\"");

    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl3 = drawable.getGL().getGL3();
        Colour clearColour = getClearColour();
        gl3.glClearColor(clearColour.getRed(), clearColour.getGreen(), clearColour.getBlue(), clearColour.getAlpha());
        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl3 = drawable.getGL().getGL3();
        gl3.glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Disposing of OpenGL parameters");
    }

}
