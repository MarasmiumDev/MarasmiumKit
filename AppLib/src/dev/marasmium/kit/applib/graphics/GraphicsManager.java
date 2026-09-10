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
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The main class of the MarasmiumKit application framework's graphics system
 */
public class GraphicsManager implements GLEventListener {

    /**
     * The number of vertices of a sprite to be rendered by the graphics system
     */
    public static final int VerticesPerSprite = 4;
    /**
     * The number of floating point values per vertex in a sprite to be rendered by the graphics system
     */
    public static final int FloatsPerVertex = 5;
    /**
     * The number of indices of sprite vertex data to process when rendering sprites
     */
    public static final int IndicesPerSprite = 6;

    /**
     * The target (fractional) number of graphics frames to process per millisecond
     */
    private float targetFPMS = 0.0f;
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
    private final HashMap<Camera, ArrayList<Sprite>> spriteGroups = new HashMap<>();
    /**
     * Scope lock for thread-safety modifying/reading the set of sprites in the current frame
     */
    private final ReentrantLock spriteGroupsLock = new ReentrantLock();
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

    /**
     * Make the graphics system thread safe to submit sprites for rendering
     */
    public void beginFrame() {
        spriteGroupsLock.lock();
        spriteGroups.clear();
    }

    /**
     * Submit a sprite to the graphics system for rendering
     * @param camera The camera to render this sprite through
     * @param sprite The sprite to render
     */
    public void submit(Camera camera, Sprite sprite) {
        if (!spriteGroups.containsKey(camera)) {
            spriteGroups.put(camera, new ArrayList<>());
        }
        spriteGroups.get(camera).add(sprite);
    }

    /**
     * Submit a set of sprites to the graphics system for rendering
     * @param camera The camera to render these sprites through
     * @param sprites The sprites to render
     */
    public void submit(Camera camera, List<Sprite> sprites) {
        for (Sprite sprite : sprites) {
            submit(camera, sprite);
        }
    }

    /**
     * Sort the sprites submitted this frame by their depth and make the graphics system thread safe for rendering
     * @return Whether the graphics system could be made thread safe
     */
    public boolean endFrame() {
        for (HashMap.Entry<Camera, ArrayList<Sprite>> entry : spriteGroups.entrySet()) {
            entry.getValue().sort(Comparator.comparingDouble(Sprite::getDepth));
        }
        try {
            spriteGroupsLock.unlock();
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
        targetFPMS = 0.0f;
        targetMSPF = 0;
        maxUPF = 0;
        clearColourLock.lock();
        clearColour = null;
        try {
            clearColourLock.unlock();
        } catch (IllegalMonitorStateException _) {
            success = false;
        }
        spriteGroupsLock.lock();
        spriteGroups.clear();
        try {
            spriteGroupsLock.unlock();
        } catch (IllegalMonitorStateException _) {
            success = false;
        }
        return success;
    }

    /**
     * Load an animation's texture data into OpenGL video memory
     * @param gl3 An instance of OpenGL to upload texture data to
     * @param animation The animation to upload the texture from
     * @return Whether the texture was loaded successfully
     */
    private boolean loadTextureID(GL3 gl3, Animation animation) {
        // Get the dimensions and colour data of the animation's texture
        int width = (int)(animation.getSheetDimensions().getX() * animation.getFrameDimensions().getX());
        int height = (int)(animation.getSheetDimensions().getY() * animation.getFrameDimensions().getY());
        ByteBuffer pixels = Buffers.newDirectByteBuffer(width * height * Integer.BYTES);
        for (Colour pixel : animation.getData()) {
            pixels.put((byte)pixel.getRed());
            pixels.put((byte)pixel.getGreen());
            pixels.put((byte)pixel.getBlue());
            pixels.put((byte)pixel.getAlpha());
        }
        pixels.flip();
        // Upload the texture to OpenGL
        int[] textureIDs = new int[1];
        gl3.glGenTextures(1, textureIDs, 0);
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, textureIDs[0]);
        gl3.glPixelStorei(GL3.GL_UNPACK_ALIGNMENT, 1);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl3.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl3.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA8, width, height, 0, GL3.GL_RGBA, GL3.GL_UNSIGNED_BYTE,
                pixels);
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        animation.setTextureID(textureIDs[0]);
        return true;
    }

    /**
     * Render the set of sprites currently cached in the graphics system's memory
     * @param gl3 An instance of OpenGL to render sprites with
     * @param spriteCount The number of sprites to be rendered
     * @param textureID The texture ID to display on the sprites
     * @param cameraMatrix The camera matrix to display sprites through
     * @param vertices The vertex data of the sprites to render
     * @param indices The indices of the vertex data to render
     */
    private void draw(GL3 gl3, int spriteCount, int textureID, float[] cameraMatrix, FloatBuffer vertices,
                      IntBuffer indices) {
        // Upload geometry
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, VBOIDs[0]);
        int verticesSize = spriteCount * VerticesPerSprite * FloatsPerVertex * Float.BYTES;
        if (verticesSize > vertexBufferSize) {
            vertexBufferSize = Math.max(verticesSize, vertexBufferSize * 2);
            App.Log.write(LogSource.Graphics, LogLevel.Info, "Resizing vertex buffer to ", vertexBufferSize, "B");
            gl3.glBufferData(GL3.GL_ARRAY_BUFFER, vertexBufferSize, null, GL3.GL_DYNAMIC_DRAW);
        }
        gl3.glBufferSubData(GL3.GL_ARRAY_BUFFER, 0, verticesSize, vertices);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, IBOIDs[0]);
        int indicesSize = spriteCount * IndicesPerSprite * Integer.BYTES;
        if (indicesSize > indexBufferSize) {
            indexBufferSize = Math.max(indicesSize, indexBufferSize * 2);
            App.Log.write(LogSource.Graphics, LogLevel.Info, "Resizing index buffer to ", indexBufferSize, "B");
            gl3.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indexBufferSize, null, GL3.GL_DYNAMIC_DRAW);
        }
        gl3.glBufferSubData(GL3.GL_ELEMENT_ARRAY_BUFFER, 0, indicesSize, indices);
        // Set texture and camera matrix
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, textureID);
        gl3.glUseProgram(shaderID);
        int cameraUniformLocation = gl3.glGetUniformLocation(shaderID, "cameraMatrix");
        gl3.glUniformMatrix4fv(cameraUniformLocation, 1, false, cameraMatrix, 0);
        // Draw geometry
        gl3.glDrawElements(GL3.GL_TRIANGLES, IndicesPerSprite * spriteCount, GL3.GL_UNSIGNED_INT, 0);
        gl3.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);
        gl3.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        gl3.glUseProgram(0);
        gl3.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    /**
     * Get the target (fractional) number of graphics frames to process per millisecond
     * @return The target number of frames per millisecond
     */
    public float getTargetFPMS() {
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
        targetFPMS = (float)targetFPS / 1000.0f;
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

    /**
     * Set up OpenGL rendering parameters, VAO, VBO, IBO, and shaders
     * @param drawable An instance of the OpenGL context
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl3 = drawable.getGL().getGL3();
        // Set OpenGL flags
        App.Log.write(LogSource.Graphics, LogLevel.Info, "Initializing OpenGL parameters");
        String OpenGLVersion = gl3.glGetString(GL3.GL_VERSION);
        App.Log.write(LogSource.Graphics, LogLevel.Info, "OpenGL version \"", OpenGLVersion, "\"");
        gl3.glEnable(GL3.GL_BLEND);
        gl3.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);
        // Create shader program
        int vertexShaderID = gl3.glCreateShader(GL3.GL_VERTEX_SHADER);
        final String[] vertexSources = {
                """
                    #version 330 core
                    layout (location = 0) in vec3 inSpritePosition;
                    layout (location = 1) in vec2 inTexturePosition;
                    uniform mat4 cameraMatrix;
                    out vec2 texturePosition;
                    void main() {
                        texturePosition = inTexturePosition;
                        gl_Position = cameraMatrix * vec4(inSpritePosition, 1.0);
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
        gl3.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, FloatsPerVertex * Float.BYTES, 0);
        gl3.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, FloatsPerVertex * Float.BYTES, 3 * Float.BYTES);
        gl3.glEnableVertexAttribArray(0);
        gl3.glEnableVertexAttribArray(1);
        gl3.glBindVertexArray(0);
    }

    /**
     * Draw the sprites currently in the graphics system's memory
     * @param drawable An instance of the OpenGL context
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        spriteGroupsLock.lock();
        // Clear screen
        GL3 gl3 = drawable.getGL().getGL3();
        Colour clearColour = getClearColour();
        gl3.glClearColor(clearColour.getRed(), clearColour.getGreen(), clearColour.getBlue(), clearColour.getAlpha());
        gl3.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        gl3.glBindVertexArray(VAOIDs[0]);
        // Define sprite group metrics
        int spriteCount = 0;
        int textureID = 0;
        FloatBuffer vertices = Buffers.newDirectFloatBuffer(0);
        IntBuffer indices = Buffers.newDirectIntBuffer(0);
        for (HashMap.Entry<Camera, ArrayList<Sprite>> spriteGroup : spriteGroups.entrySet()) {
            Camera camera = spriteGroup.getKey();
            ArrayList<Sprite> sprites = spriteGroup.getValue();
            for (Sprite sprite : sprites) {
                // Copy sprite metrics
                Vector spritePosition = sprite.getPosition();
                float spriteDepth = sprite.getDepth();
                Vector spriteDimensions = sprite.getDimensions();
                Angle spriteAngle = sprite.getAngle();
                Vector spriteCentre = spritePosition.add(spriteDimensions.scalarMultiply(0.5f));
                Vector spriteBottomLeft = Vector.Cartesian(spritePosition.getX(), spritePosition.getY())
                        .rotateAbout(spriteAngle, spriteCentre);
                Vector spriteBottomRight = Vector.Cartesian(spritePosition.getX() + spriteDimensions.getX(),
                        spritePosition.getY()).rotateAbout(spriteAngle, spriteCentre);
                Vector spriteTopRight = Vector.Cartesian(spritePosition.getX() + spriteDimensions.getX(),
                        spritePosition.getY() + spriteDimensions.getY()).rotateAbout(spriteAngle, spriteCentre);
                Vector spriteTopLeft = Vector.Cartesian(spritePosition.getX(),
                        spritePosition.getY() + spriteDimensions.getY()).rotateAbout(spriteAngle, spriteCentre);
                // Copy animation metrics and upload texture if necessary
                Animation animation = App.Assets.getAnimation(sprite.getAnimationFilePath());
                if (animation.getTextureID() != textureID && spriteCount > 0) {
                    draw(gl3, spriteCount, textureID, camera.getProjectionMatrix(), vertices, indices);
                    spriteCount = 0;
                    vertices = Buffers.newDirectFloatBuffer(0);
                    indices = Buffers.newDirectIntBuffer(0);
                }
                textureID = animation.getTextureID();
                if (textureID == 0) {
                    if (!loadTextureID(gl3, animation)) {
                        continue;
                    }
                    textureID = animation.getTextureID();
                }
                // Copy texture positioning metrics
                Vector texturePosition = animation.getFrameTexturePosition(sprite.getAnimationFrame());
                Vector textureDimensions = animation.getFrameTextureDimensions();
                Vector textureBottomLeft = Vector.Cartesian(texturePosition.getX(), texturePosition.getY());
                Vector textureBottomRight = Vector.Cartesian(texturePosition.getX() + textureDimensions.getX(),
                        texturePosition.getY());
                Vector textureTopRight = Vector.Cartesian(texturePosition.getX() + textureDimensions.getX(),
                        texturePosition.getY() + textureDimensions.getY());
                Vector textureTopLeft = Vector.Cartesian(texturePosition.getX(),
                        texturePosition.getY() + textureDimensions.getY());
                if (sprite.isFlippedHorizontally()) {
                    Vector copy = textureBottomRight.clone();
                    textureBottomRight = textureBottomLeft.clone();
                    textureBottomLeft = copy.clone();
                    copy = textureTopRight.clone();
                    textureTopRight = textureTopLeft.clone();
                    textureTopLeft = copy.clone();
                }
                if (sprite.isFlippedVertically()) {
                    Vector copy = textureBottomRight.clone();
                    textureBottomRight = textureTopRight.clone();
                    textureTopRight = copy.clone();
                    copy = textureBottomLeft.clone();
                    textureBottomLeft = textureTopLeft.clone();
                    textureTopLeft = copy.clone();
                }
                // Allocate vertices and indices
                float[] sVertices = {
                        spriteBottomLeft.getX(), spriteBottomLeft.getY(), spriteDepth,
                        textureBottomLeft.getX(), textureBottomLeft.getY(),
                        spriteBottomRight.getX(), spriteBottomRight.getY(), spriteDepth,
                        textureBottomRight.getX(), textureBottomRight.getY(),
                        spriteTopRight.getX(), spriteTopRight.getY(), spriteDepth,
                        textureTopRight.getX(), textureTopRight.getY(),
                        spriteTopLeft.getX(), spriteTopLeft.getY(), spriteDepth,
                        textureTopLeft.getX(), textureTopLeft.getY(),
                };
                FloatBuffer newVertices = Buffers.newDirectFloatBuffer(vertices.capacity() + sVertices.length);
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
            draw(gl3, spriteCount, textureID, camera.getProjectionMatrix(), vertices, indices);
            gl3.glBindVertexArray(0);
        }
        try {
            spriteGroupsLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Graphics, LogLevel.Error, "Failed to unlock sprite scope lock");
        }
    }

    /**
     * Resize the OpenGL context in the application framework's window
     * @param drawable An instance of the OpenGL context
     * @param x The x-coordinate of the bottom-left corner of the window
     * @param y The y-coordinate of the bottom-left corner of the window
     * @param width The width of the window
     * @param height The height of the window
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl3 = drawable.getGL().getGL3();
        gl3.glViewport(0, 0, width, height);
    }

    /**
     * Dispose of the OpenGL resources loaded by the graphics system
     * @param drawable An instance of the OpenGL context
     */
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
