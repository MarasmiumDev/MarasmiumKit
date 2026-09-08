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
import dev.marasmium.kit.applib.assets.Animation;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.nio.ByteBuffer;
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
     * The set of IDs of textures uploaded to OpenGL video memory
     */
    private final ArrayList<Integer> textureIDs = new ArrayList<>();

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

    private boolean loadTextureID(GL3 gl3, Animation animation) {
        int width = (int)(animation.getSheetDimensions().getX() * animation.getFrameDimensions().getX());
        int height = (int)(animation.getSheetDimensions().getY() * animation.getFrameDimensions().getY());
        ByteBuffer pixels = Buffers.newDirectByteBuffer(width * height * Integer.BYTES);
        for (Colour pixel : animation.getData()) {
            byte red = (byte)pixel.getRed();
            byte green = (byte)pixel.getGreen();
            byte blue = (byte)pixel.getBlue();
            byte alpha = (byte)pixel.getAlpha();
            pixels.put(red);
            pixels.put(green);
            pixels.put(blue);
            pixels.put(alpha);
        }
        pixels.flip();
        int[] textureIDs = new int[1];
        gl3.glGenTextures(1, textureIDs, 0);
        int textureID = textureIDs[0];
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, textureID);
        gl3.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl3.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA8, width, height, 0, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE,
                pixels);
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        animation.setTextureID(textureID);
        return true;
    }

    private void draw(GL3 gl3, int spriteCount, int textureID, DoubleBuffer vertices, IntBuffer indices) {
        final int verticesPerSprite = 4;
        final int doublesPerVertex = 5;
        final int indicesPerSprite = 6;
        // Upload geometry
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, VBOIDs[0]);
        int verticesSize = doublesPerVertex * Double.BYTES * verticesPerSprite * spriteCount;
        if (verticesSize > vertexBufferSize) {
            vertexBufferSize = Math.max(verticesSize, vertexBufferSize * 2);
            App.Log.write(LogSource.Graphics, LogLevel.Info, "Resizing vertex buffer to ", vertexBufferSize, "B");
            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vertexBufferSize, null, GL3.GL_DYNAMIC_DRAW);
        }
        gl3.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, verticesSize, vertices);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, IBOIDs[0]);
        int indicesSize = indicesPerSprite * Integer.BYTES * spriteCount;
        if (indicesSize > indexBufferSize) {
            indexBufferSize = Math.max(indicesSize, indexBufferSize * 2);
            App.Log.write(LogSource.Graphics, LogLevel.Info, "Resizing index buffer to ", indexBufferSize, "B");
            gl3.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indexBufferSize, null, GL3.GL_DYNAMIC_DRAW);
        }
        gl3.glBufferSubData(GL3.GL_ELEMENT_ARRAY_BUFFER, 0, indicesSize, indices);
        // Draw geometry
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, textureID);
        gl3.glUseProgram(shaderID);
        gl3.glDrawElements(GL3.GL_TRIANGLES, indicesPerSprite * spriteCount, GL3.GL_UNSIGNED_INT, 0);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, 0);
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
                    layout (location = 0) in vec3 inSpritePosition;
                    layout (location = 1) in vec2 inTexturePosition;
                    out vec2 texturePosition;
                    void main() {
                        texturePosition = inTexturePosition;
                        gl_Position = vec4(inSpritePosition, 1.0);
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
                    in vec2 texturePosition;
                    uniform sampler2D textureSampler;
                    out vec4 outColour;
                    void main() {
                        outColour = texture(textureSampler, texturePosition);
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
        gl3.glVertexAttribPointer(0, 3, GL3.GL_DOUBLE, false, 5 * Double.BYTES, 0);
        gl3.glVertexAttribPointer(1, 2, GL3.GL_DOUBLE, false, 5 * Double.BYTES, 3 * Double.BYTES);
        gl3.glEnableVertexAttribArray(0);
        gl3.glEnableVertexAttribArray(1);
        gl3.glBindVertexArray(0);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        spritesLock.lock();
        // Clear screen
        GL3 gl3 = drawable.getGL().getGL3();
        Colour clearColour = getClearColour();
        gl3.glClearColor(clearColour.getRed(), clearColour.getGreen(), clearColour.getBlue(), clearColour.getAlpha());
        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        gl3.glBindVertexArray(VAOIDs[0]);
        // Define data metrics
        int spriteCount = 0;
        int textureID = 0;
        DoubleBuffer vertices = Buffers.newDirectDoubleBuffer(0);
        IntBuffer indices = Buffers.newDirectIntBuffer(0);
        for (Sprite sprite : sprites) {
            Vector sPosition = sprite.getPosition();
            double sDepth = sprite.getDepth();
            Vector sDimensions = sprite.getDimensions();
            Angle sAngle = sprite.getAngle();
            Vector sMidpoint = sPosition.add(sDimensions.scalarMultiply(0.5d));
            Vector sBL = Vector.Cartesian(sPosition.getX(), sPosition.getY());
            sBL = sBL.rotateAbout(sAngle, sMidpoint);
            Vector sBR = Vector.Cartesian(sPosition.getX() + sDimensions.getX(), sPosition.getY());
            sBR = sBR.rotateAbout(sAngle, sMidpoint);
            Vector sTR = Vector.Cartesian(sPosition.getX() + sDimensions.getX(), sPosition.getY() + sDimensions.getY());
            sTR = sTR.rotateAbout(sAngle, sMidpoint);
            Vector sTL = Vector.Cartesian(sPosition.getX(), sPosition.getY() + sDimensions.getY());
            sTL = sTL.rotateAbout(sAngle, sMidpoint);
            Animation sAnimation = App.Assets.getAnimation(sprite.getAnimationFilePath());
            if (sAnimation.getTextureID() != textureID && spriteCount > 0) {
                draw(gl3, spriteCount, textureID, vertices, indices);
                spriteCount = 0;
                vertices = Buffers.newDirectDoubleBuffer(0);
                indices = Buffers.newDirectIntBuffer(0);
            }
            textureID = sAnimation.getTextureID();
            if (textureID == 0) {
                if (!loadTextureID(gl3, sAnimation)) {
                    continue;
                }
                textureID = sAnimation.getTextureID();
            }
            Vector tPosition = sAnimation.getTexturePosition(sprite.getAnimationFrame());
            Vector tDimensions = sAnimation.getTextureDimensions();
            Vector tBL = Vector.Cartesian(tPosition.getX(), tPosition.getY());
            Vector tBR = Vector.Cartesian(tPosition.getX() + tDimensions.getX(), tPosition.getY());
            Vector tTR = Vector.Cartesian(tPosition.getX() + tDimensions.getX(), tPosition.getY() + tDimensions.getY());
            Vector tTL = Vector.Cartesian(tPosition.getX(), tPosition.getY() + tDimensions.getY());
            if (sprite.isFlippedHorizontally()) {
                Vector copy = tBR.clone();
                tBR = tBL.clone();
                tBL = copy.clone();
                copy = tTR.clone();
                tTR = tTL.clone();
                tTL = copy.clone();
            }
            if (sprite.isFlippedVertically()) {
                Vector copy = tBR.clone();
                tBR = tTR.clone();
                tTR = copy.clone();
                copy = tBL.clone();
                tBL = tTL.clone();
                tTL = copy.clone();
            }
            double[] sVertices = {
                    sBL.getX(), sBL.getY(), sDepth,
                    tBL.getX(), tBL.getY(),
                    sBR.getX(), sBR.getY(), sDepth,
                    tBR.getX(), tBR.getY(),
                    sTR.getX(), sTR.getY(), sDepth,
                    tTR.getX(), tTR.getY(),
                    sTL.getX(), sTL.getY(), sDepth,
                    tTL.getX(), tTL.getY(),
            };
            DoubleBuffer newVertices = Buffers.newDirectDoubleBuffer(vertices.capacity() + sVertices.length);
            newVertices.put(vertices);
            newVertices.put(sVertices);
            vertices = newVertices;
            vertices.flip();
            int[] sIndices = {
                    (spriteCount * 4) + 0, (spriteCount * 4) + 1, (spriteCount * 4) + 2,
                    (spriteCount * 4) + 2, (spriteCount * 4) + 3, (spriteCount * 4) + 0,
            };
            IntBuffer newIndices = Buffers.newDirectIntBuffer(indices.capacity() + sIndices.length);
            newIndices.put(indices);
            newIndices.put(sIndices);
            indices = newIndices;
            indices.flip();
            spriteCount++;
        }
        draw(gl3, spriteCount, textureID, vertices, indices);
        gl3.glBindVertexArray(0);
        try {
            spritesLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to unlock sprite scope lock");
        }
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
        for (int textureID : textureIDs) {
            int[] IDs = { textureID };
            gl3.glDeleteTextures(1, IDs, 0);
        }
        gl3.glDeleteVertexArrays(1, VAOIDs, 0);
        gl3.glDeleteBuffers(1, VBOIDs, 0);
        gl3.glDeleteBuffers(1, IBOIDs, 0);
        gl3.glDeleteProgram(shaderID);
    }

}
