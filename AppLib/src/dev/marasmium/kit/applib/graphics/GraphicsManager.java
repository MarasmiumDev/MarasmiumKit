/**
 * File:        GraphicsManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines the main class of the MarasmiumKit application framework's graphics system
 */

package dev.marasmium.kit.applib.graphics;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
     * The set of sprites to be rendered in the current frame
     */
    private final ArrayList<Sprite> sprites = new ArrayList<>();
    /**
     * Scope lock for thread-safety modifying/reading the set of sprites in the current frame
     */
    private final ReentrantLock spritesLock = new ReentrantLock();
    /**
     * The set of OpenGL vertex array object IDs used by the graphics system
     */
    private final int[] VAOIDs = new int[1];
    /**
     * The set of OpenGL vertex buffer object IDs used by the graphics system
     */
    private final int[] VBOIDs = new int[1];
    /**
     * The current size of the OpenGL vertex buffer in bytes
     */
    private int vertexBufferSize = 0;
    /**
     * The set of OpenGL index buffer object IDs used by the graphics system
     */
    private final int[] IBOIDs = new int[1];
    /**
     * The current size of the OpenGL index buffer in bytes
     */
    private int indexBufferSize = 0;
    /**
     * The GLSL shader program ID used by the graphics system
     */
    private int shaderID = 0;

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

    public boolean beginFrame() {
        spritesLock.lock();
        sprites.clear();
        return true;
    }

    public void submit(Sprite sprite) {
        sprites.add(sprite);
    }

    public void submit(List<Sprite> sprites) {
        for (Sprite sprite : sprites) {
            submit(sprite);
        }
    }

    public boolean endFrame() {
        sprites.sort(Comparator.comparingDouble(Sprite::getDepth));
        try {
            spritesLock.unlock();
        } catch (IllegalMonitorStateException _) {
            return false;
        }
        return true;
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
        clearColourLock.lock();
        clearColour = null;
        try {
            clearColourLock.unlock();
        } catch (IllegalMonitorStateException _) {
            success = false;
        }
        spritesLock.lock();
        sprites.clear();
        try {
            spritesLock.unlock();
        } catch (IllegalMonitorStateException _) {
            success = false;
        }
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
        // Set OpenGL flags
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Initializing OpenGL parameters");
        String OpenGLVersion = gl3.glGetString(GL3.GL_VERSION);
        gl3.glEnable(GL3.GL_BLEND);
        gl3.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        App.Log.write(LogSource.Graphics, LogLevel.Info, "OpenGL version \"", OpenGLVersion, "\"");
        // Create shader program
        int vertexShaderID = gl3.glCreateShader(GL3.GL_VERTEX_SHADER);
        final String[] vertexSources = {
                """
                    #version 330 core
                    layout (location = 0) in vec3 inPosition;
                    layout (location = 1) in vec4 spriteColour;
                    out vec4 vertexColour;
                    void main() {
                        vertexColour = spriteColour;
                        gl_Position = vec4(inPosition, 1.0);
                    }
                """,
        };
        int[] vertexSourceLengths = {
                vertexSources[0].length(),
        };
        gl3.glShaderSource(vertexShaderID, 1, vertexSources, vertexSourceLengths, 0);
        gl3.glCompileShader(vertexShaderID);
        int[] vertexStatuses = new int[1];
        gl3.glGetShaderiv(vertexShaderID, GL3.GL_COMPILE_STATUS, vertexStatuses, 0);
        if (vertexStatuses[0] == GL3.GL_FALSE) {
            int[] vertexLogLengths = new int[1];
            gl3.glGetShaderiv(vertexShaderID, GL3.GL_INFO_LOG_LENGTH, vertexLogLengths, 0);
            byte[] vertexLog = new byte[vertexLogLengths[0]];
            gl3.glGetShaderInfoLog(vertexShaderID, vertexLogLengths[0], null, 0, vertexLog, 0);
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to compile vertex shader, message:\n",
                    new String(vertexLog));
            return;
        }
        int fragmentShaderID = gl3.glCreateShader(GL3.GL_FRAGMENT_SHADER);
        final String[] fragmentSources = {
                """
                    #version 330 core
                    in vec4 vertexColour;
                    out vec4 outColour;
                    void main() {
                        outColour = vertexColour;
                    }
                """,
        };
        int[] fragmentSourceLengths = {
                fragmentSources[0].length(),
        };
        gl3.glShaderSource(fragmentShaderID, 1, fragmentSources, fragmentSourceLengths, 0);
        gl3.glCompileShader(fragmentShaderID);
        int[] fragmentStatuses = new int[1];
        gl3.glGetShaderiv(fragmentShaderID, GL3.GL_COMPILE_STATUS, fragmentStatuses, 0);
        if (fragmentStatuses[0] == GL3.GL_FALSE) {
            int[] fragmentLogLengths = new int[1];
            gl3.glGetShaderiv(fragmentShaderID, GL3.GL_INFO_LOG_LENGTH, fragmentLogLengths, 0);
            byte[] fragmentLog = new byte[fragmentLogLengths[0]];
            gl3.glGetShaderInfoLog(fragmentShaderID, fragmentLogLengths[0], null, 0, fragmentLog, 0);
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to compile fragment shader, message:\n",
                    new String(fragmentLog));
            return;
        }
        shaderID = gl3.glCreateProgram();
        gl3.glAttachShader(shaderID, vertexShaderID);
        gl3.glAttachShader(shaderID, fragmentShaderID);
        gl3.glLinkProgram(shaderID);
        gl3.glDeleteShader(vertexShaderID);
        gl3.glDeleteShader(fragmentShaderID);
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Generated shader program ", shaderID);
        // Create VAO, VBO, and IBO
        gl3.glGenVertexArrays(1, VAOIDs, 0);
        gl3.glBindVertexArray(VAOIDs[0]);
        gl3.glGenBuffers(1, VBOIDs, 0);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, VBOIDs[0]);
        gl3.glGenBuffers(1, IBOIDs, 0);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, IBOIDs[0]);
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Generated VAO ", VAOIDs[0], ", VBO ", VBOIDs[0], ", and IBO ",
                IBOIDs[0]);
        // Configure vertex attributes
        gl3.glVertexAttribPointer(0, 3, GL3.GL_DOUBLE, false, 7 * Double.BYTES, 0);
        gl3.glVertexAttribPointer(1, 4, GL3.GL_DOUBLE, false, 7 * Double.BYTES, 3 * Double.BYTES);
        gl3.glEnableVertexAttribArray(0);
        gl3.glEnableVertexAttribArray(1);
        gl3.glBindVertexArray(0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        spritesLock.lock();
        // Gather geometry
        int spriteCount = sprites.size();
        final int verticesPerSprite = 4;
        final int doublesPerVertex = 7;
        final int indicesPerSprite = 6;
        final double[] vertices = new double[verticesPerSprite * doublesPerVertex * spriteCount];
        final int[] indices = new int[indicesPerSprite * spriteCount];
        int sIndex = 0;
        for (Sprite sprite : sprites) {
            Vector position = sprite.getPosition();
            double depth = sprite.getDepth();
            Vector dimensions = sprite.getDimensions();
            Angle angle = sprite.getAngle();
            Vector midpoint = position.add(dimensions.scalarMultiply(0.5d));
            Vector BL = Vector.Cartesian(position.getX(), position.getY());
            BL = BL.rotateAbout(angle, midpoint);
            Vector BR = Vector.Cartesian(position.getX() + dimensions.getX(), position.getY());
            BR = BR.rotateAbout(angle, midpoint);
            Vector TR = Vector.Cartesian(position.getX() + dimensions.getX(), position.getY() + dimensions.getY());
            TR = TR.rotateAbout(angle, midpoint);
            Vector TL = Vector.Cartesian(position.getX(), position.getY() + dimensions.getY());
            TL = TL.rotateAbout(angle, midpoint);
            Colour colour = sprite.getColour();
            double[] sVertices = {
                    BL.getX(), BL.getY(), depth,
                    (double)colour.getRed() / 255.0d, (double)colour.getGreen() / 255.0d, (double)colour.getBlue() / 255.0d, (double)colour.getAlpha() / 255.0d,
                    BR.getX(), BR.getY(), depth,
                    (double)colour.getRed() / 255.0d, (double)colour.getGreen() / 255.0d, (double)colour.getBlue() / 255.0d, (double)colour.getAlpha() / 255.0d,
                    TR.getX(), TR.getY(), depth,
                    (double)colour.getRed() / 255.0d, (double)colour.getGreen() / 255.0d, (double)colour.getBlue() / 255.0d, (double)colour.getAlpha() / 255.0d,
                    TL.getX(), TL.getY(), depth,
                    (double)colour.getRed() / 255.0d, (double)colour.getGreen() / 255.0d, (double)colour.getBlue() / 255.0d, (double)colour.getAlpha() / 255.0d,
            };
            System.arraycopy(sVertices, 0, vertices, sIndex * sVertices.length, sVertices.length);
            int[] sIndices = {
                    (sIndex * 4) + 0, (sIndex * 4) + 1, (sIndex * 4) + 2,
                    (sIndex * 4) + 2, (sIndex * 4) + 3, (sIndex * 4) + 0,
            };
            System.arraycopy(sIndices, 0, indices, sIndex * sIndices.length, sIndices.length);
            sIndex++;
        }
        DoubleBuffer vertexBuffer = Buffers.newDirectDoubleBuffer(vertices);
        IntBuffer indexBuffer = Buffers.newDirectIntBuffer(indices);
        try {
            spritesLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to unlock sprite scope lock");
        }
        // Clear screen
        GL3 gl3 = drawable.getGL().getGL3();
        Colour clearColour = getClearColour();
        gl3.glClearColor(clearColour.getRed(), clearColour.getGreen(), clearColour.getBlue(), clearColour.getAlpha());
        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        gl3.glBindVertexArray(VAOIDs[0]);
        // Upload geometry
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, VBOIDs[0]);
        int verticesSize = doublesPerVertex * Double.BYTES * verticesPerSprite * spriteCount;
        if (verticesSize > vertexBufferSize) {
            vertexBufferSize = Math.max(verticesSize, vertexBufferSize * 2);
            App.Log.write(LogSource.Graphics, LogLevel.Info, "Resizing vertex buffer to ", vertexBufferSize, "B");
            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vertexBufferSize, null, GL3.GL_DYNAMIC_DRAW);
        }
        gl3.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, verticesSize, vertexBuffer);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, IBOIDs[0]);
        int indicesSize = indicesPerSprite * Integer.BYTES * spriteCount;
        if (indicesSize > indexBufferSize) {
            indexBufferSize = Math.max(indicesSize, indexBufferSize * 2);
            App.Log.write(LogSource.Graphics, LogLevel.Info, "Resizing index buffer to ", indexBufferSize, "B");
            gl3.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indexBufferSize, null, GL3.GL_DYNAMIC_DRAW);
        }
        gl3.glBufferSubData(GL3.GL_ELEMENT_ARRAY_BUFFER, 0, indicesSize, indexBuffer);
        // Draw geometry
        gl3.glUseProgram(shaderID);
        gl3.glDrawElements(GL3.GL_TRIANGLES, indicesPerSprite * spriteCount, GL3.GL_UNSIGNED_INT, 0);
        gl3.glBindVertexArray(0);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl3 = drawable.getGL().getGL3();
        gl3.glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Disposing of OpenGL parameters");
        GL3 gl3 = drawable.getGL().getGL3();
        gl3.glDeleteVertexArrays(1, VAOIDs, 0);
        gl3.glDeleteBuffers(1, VBOIDs, 0);
        gl3.glDeleteBuffers(1, IBOIDs, 0);
        gl3.glDeleteProgram(shaderID);
    }

}
