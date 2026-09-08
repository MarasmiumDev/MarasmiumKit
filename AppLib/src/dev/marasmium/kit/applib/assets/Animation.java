/**
 * File:        Animation.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.06
 * Purpose:     Defines a data structure representing an animation/sprite sheet
 */

package dev.marasmium.kit.applib.assets;

import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

public class Animation {

    private int targetFPS = 0;
    private Vector sheetDimensions = null;
    private Vector frameDimensions = null;
    private Colour[] data = null;
    private int textureID = 0;

    public boolean initialize(int targetFPS, Vector sheetDimensions, Vector frameDimensions, Colour[] data) {
        if (!setTargetFPS(targetFPS)) {
            return false;
        }
        if (!setSheetDimensions(sheetDimensions)) {
            return false;
        }
        if (!setFrameDimensions(frameDimensions)) {
            return false;
        }
        if (!setData(data)) {
            return false;
        }
        return true;
    }

    public void destroy() {
        targetFPS = 0;
        sheetDimensions = null;
        frameDimensions = null;
        data = null;
        textureID = 0;
    }

    public int getTargetFPS() {
        return targetFPS;
    }

    public boolean setTargetFPS(int targetFPS) {
        if (targetFPS <= 0) {
            return false;
        }
        this.targetFPS = targetFPS;
        return true;
    }

    public Vector getSheetDimensions() {
        return sheetDimensions;
    }

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

    public Vector getFrameDimensions() {
        return frameDimensions;
    }

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

    public Colour[] getData() {
        return data;
    }

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

    public int getTextureID() {
        return textureID;
    }

    public void setTextureID(int textureID) {
        this.textureID = textureID;
    }

    public Vector getFrameTextureCoordinates(int frameIndex) {
        int sheetX = frameIndex % ((int)sheetDimensions.getX());
        int sheetY = (frameIndex - sheetX) / (int)sheetDimensions.getX();
        Vector textureDimensions = getFrameTextureDimensions();
        return Vector.Cartesian(sheetX * textureDimensions.getX(), sheetY * textureDimensions.getY());
    }

    public Vector getFrameTextureDimensions() {
        return Vector.Cartesian(1.0d / sheetDimensions.getX(), 1.0d / sheetDimensions.getY());
    }

    @Override
    public String toString() {
        if (sheetDimensions == null || frameDimensions == null || data == null) {
            return "animation(null)";
        }
        return "animation(" + targetFPS + "FPS, " + sheetDimensions + "frames, " + frameDimensions + "pixels)";
    }

}
