/**
 * File:        Animation.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.06
 * Purpose:     Defines a data structure representing an animation/sprite sheet
 */

package dev.marasmium.kit.applib.assets;

import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

/**
 * Data structure representing an animation which can be applied to a sprite to be drawn by the MarasmiumKit application
 * framework
 */
public class Animation {

    /**
     * The target number of frames of this animation to draw per second
     */
    private int targetFPS = 0;
    /**
     * The dimensions of this animation's texture in frames
     */
    private Vector sheetDimensions = null;
    /**
     * The dimensions of this animation's frames in pixels
     */
    private Vector frameDimensions = null;
    /**
     * The number of frames in this animation
     */
    private int frameCount = 0;
    /**
     * The pixel colour data in this animation's texture
     */
    private Colour[] data = null;
    /**
     * The OpenGL texture ID for this animation's texture
     */
    private int textureID = 0;

    /**
     * Initialize this animation with a target FPS, sheet and frame dimensions, frame count, and pixel data
     * @param targetFPS The target FPS to draw of this animation
     * @param sheetDimensions The dimensions of this animation's texture in frames
     * @param frameDimensions The dimensions of this animation's frame's in pixels
     * @param frameCount The number of frames to store in this animation
     * @param data The pixel colour data for this animation's texture
     * @return Whether this animation was initialized successfully
     */
    public boolean initialize(int targetFPS, Vector sheetDimensions, Vector frameDimensions, int frameCount,
                              Colour[] data) {
        if (!setTargetFPS(targetFPS)) {
            return false;
        }
        if (!setSheetDimensions(sheetDimensions)) {
            return false;
        }
        if (!setFrameDimensions(frameDimensions)) {
            return false;
        }
        if (!setFrameCount(frameCount)) {
            return false;
        }
        if (!setData(data)) {
            return false;
        }
        setTextureID(0);
        return true;
    }

    /**
     * Free this animation's memory
     */
    public void destroy() {
        targetFPS = 0;
        sheetDimensions = null;
        frameDimensions = null;
        frameCount = 0;
        data = null;
        textureID = 0;
    }

    /**
     * Get the target number of frames of this animation to display per second when playing
     * @return The target FPS of this animation
     */
    public int getTargetFPS() {
        return targetFPS;
    }

    /**
     * Set the target number of frames of this animation to display per second when playing
     * @param targetFPS The new target FPS for this animation
     * @return Whether the given target FPS was valid
     */
    public boolean setTargetFPS(int targetFPS) {
        if (targetFPS <= 0) {
            return false;
        }
        this.targetFPS = targetFPS;
        return true;
    }

    /**
     * Get the dimensions of this animation's texture in frames
     * @return The dimensions of this animation's texture in frames
     */
    public Vector getSheetDimensions() {
        return sheetDimensions;
    }

    /**
     * Set the dimensions of this animation's texture in frames
     * @param sheetDimensions The new dimensions of this animation's texture in frames
     * @return Whether the given sheet dimensions were valid
     */
    public boolean setSheetDimensions(Vector sheetDimensions) {
        if (sheetDimensions == null) {
            return false;
        }
        if (sheetDimensions.getX() <= 1.0d || sheetDimensions.getY() <= 1.0d) {
            return false;
        }
        this.sheetDimensions = sheetDimensions;
        return true;
    }

    /**
     * Get the dimensions of this animation's frames in pixels
     * @return The dimensions of this animation's frames in pixels
     */
    public Vector getFrameDimensions() {
        return frameDimensions;
    }

    /**
     * Set the dimensions of this animation's frames in pixels
     * @param frameDimensions The new dimensions of this animation's frames in pixels
     * @return Whether the given frame dimensions were valid
     */
    public boolean setFrameDimensions(Vector frameDimensions) {
        if (frameDimensions == null) {
            return false;
        }
        if (frameDimensions.getX() <= 1.0d || frameDimensions.getY() <= 1.0d) {
            return false;
        }
        this.frameDimensions = frameDimensions;
        return true;
    }

    /**
     * Get the number of frames contained in this animation
     * @return The number of frames in this animation
     */
    public int getFrameCount() {
        return frameCount;
    }

    /**
     * Set the number of frames contained in this animation
     * @param frameCount The new number of frames in this animation
     * @return Whether the given frame count was valid
     */
    public boolean setFrameCount(int frameCount) {
        if (frameCount <= 0 || frameCount > (int)sheetDimensions.getElementProduct()) {
            return false;
        }
        this.frameCount = frameCount;
        return true;
    }

    /**
     * Get the pixel colour data contained in this animation
     * @return This animation's pixel colour data
     */
    public Colour[] getData() {
        return data;
    }

    /**
     * Set the pixel colour data contained in this animation
     * @param data The new pixel colour data for this animation
     * @return Whether the given data was valid and of appropriate size
     */
    public boolean setData(Colour[] data) {
        if (data == null) {
            return false;
        }
        if (data.length != (int)(sheetDimensions.getElementProduct() * frameDimensions.getElementProduct())) {
            return false;
        }
        this.data = new Colour[data.length];
        try {
            System.arraycopy(data, 0, this.data, 0, data.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException | NullPointerException _) {
            return false;
        }
        return true;
    }

    /**
     * Get the OpenGL texture ID assigned to this animation's texture
     * @return This animation's OpenGL texture ID
     */
    public int getTextureID() {
        return textureID;
    }

    /**
     * Set the OpenGL texture ID assigned to this animation's texture
     * @param textureID this animation's new OpenGL texture ID
     */
    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    /**
     * Get the position of a given frame of this animation in OpenGL texture coordinates
     * @param frameIndex The index of the frame of this animation to get the coordinates of
     * @return The OpenGL texture coordinates of the given frame in this animation's texture
     */
    public Vector getFrameTexturePosition(int frameIndex) {
        int xSheet = frameIndex % ((int)sheetDimensions.getX());
        int ySheet = (frameIndex - xSheet) / (int)sheetDimensions.getX();
        Vector textureDimensions = getFrameTextureDimensions();
        return Vector.Cartesian(xSheet * textureDimensions.getX(), ySheet * textureDimensions.getY());
    }

    /**
     * Get the dimensions of this animation's frames in OpenGL texture coordinates
     * @return The dimensions of this animation's frames in OpenGL texture coordinates
     */
    public Vector getFrameTextureDimensions() {
        return Vector.Cartesian(1.0f / sheetDimensions.getX(), 1.0f / sheetDimensions.getY());
    }

    /**
     * Convert this animation to a string
     * @return The string representation of this animation
     */
    @Override
    public String toString() {
        if (sheetDimensions == null || frameDimensions == null || data == null) {
            return "animation(null)";
        }
        return "animation(" + targetFPS + "FPS, " + frameCount + " of " + sheetDimensions + "frames, " + frameDimensions
                + "pixels, texture ID " + textureID + ")";
    }

}
