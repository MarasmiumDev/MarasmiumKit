/**
 * File:        UIComponent.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     Defines an abstract user-interface component in a user-interface group
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.uilib.UIGroup;

/**
 * An abstract component in a user-interface group
 */
public abstract class UIComponent extends UIGroup {

    /**
     * The ID of this component
     */
    protected int componentID = 0;
    /**
     * The sprite representing the background of this component
     */
    protected final Sprite sprite = new Sprite();
    /**
     * The minimum dimensions of this component in pixels
     */
    protected Vector minimumDimensions = null;
    /**
     * The maximum dimensions of this component in pixels
     */
    protected Vector maximumDimensions = null;

    /**
     * Backup of the application's window dimensions
     */
    private Vector windowDimensions = null;

    /**
     * Free this component's memory
     */
    @Override
    public void destroy() {
        super.destroy();
        componentID = 0;
        sprite.destroy();
        minimumDimensions = null;
        maximumDimensions = null;
        windowDimensions = null;
    }

    /**
     * Get the lowest depth this component can be drawn at
     * @return This component's base depth
     */
    @Override
    public float getBaseDepth() {
        if (parent == null) {
            return Float.NaN;
        }
        return parent.getBaseDepth();
    }

    /**
     * Get the typeface text is drawn in on this component
     * @return This component's typeface
     */
    @Override
    public String getTypefaceFilePath() {
        if (parent == null) {
            return null;
        }
        return parent.getTypefaceFilePath();
    }

    /**
     * Get the size text is drawn at on this component in percent of the typeface's original size
     * @return This component's text size in percent of the typeface's original size
     */
    @Override
    public float getTextSize() {
        if (parent == null) {
            return Float.NaN;
        }
        return parent.getTextSize();
    }

    /**
     * Get the padding placed around text drawn on this component in pixels
     * @return This component's text padding in pixels
     */
    @Override
    public float getTextPadding() {
        if (parent == null) {
            return Float.NaN;
        }
        return parent.getTextPadding();
    }

    /**
     * Get the ID of this component's parent group
     * @return This component's group ID
     */
    @Override
    public int getGroupID() {
        if (parent == null) {
            return 0;
        }
        return parent.getGroupID();
    }

    /**
     * Get the camera used to draw this component
     * @return This component's camera
     */
    @Override
    public Camera getCamera() {
        if (parent == null) {
            return null;
        }
        return parent.getCamera();
    }

    /**
     * Get the ID of this component
     * @return This component's ID
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Set the ID of this component
     * @param componentID This component's new ID
     */
    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    /**
     * Get the sprite representing this component's background
     * @return This component's sprite
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Get this component's horizontal and vertical position in percent of the application's window dimensions
     * @return This component's position in percent of the application's window dimensions
     */
    public Vector getPosition() {
        if (windowDimensions == null || sprite.getPosition() == null) {
            return null;
        }
        return sprite.getPosition().elementDivide(windowDimensions);
    }

    /**
     * Set this component's horizontal and vertical position in percent of the application's window dimensions
     * @param position This component's new position in percent of the application's window dimensions
     * @return Whether the given position was valid
     */
    public boolean setPosition(Vector position) {
        windowDimensions = App.Window.getDimensions().clone();
        if (windowDimensions == null || position == null) {
            return false;
        }
        return sprite.setPosition(position.elementMultiply(windowDimensions));
    }

    /**
     * Get this component's horizontal and vertical dimensions in percent of the application's window dimensions
     * @return This component's dimensions in percent of the application's window dimensions
     */
    public Vector getDimensions() {
        if (windowDimensions == null || sprite.getDimensions() == null) {
            return null;
        }
        return sprite.getDimensions().elementDivide(windowDimensions);
    }

    /**
     * Set this component's horizontal and vertical dimensions in percent of the application's window dimensions
     * @param dimensions This component's new dimensions in percent of the application's window dimensions
     * @return Whether the given dimensions were valid
     */
    public boolean setDimensions(Vector dimensions) {
        windowDimensions = App.Window.getDimensions().clone();
        if (windowDimensions == null || dimensions == null) {
            return false;
        }
        Vector pixelDimensions = dimensions.elementMultiply(windowDimensions);
        if (minimumDimensions != null) {
            if (pixelDimensions.getX() < minimumDimensions.getX()) {
                pixelDimensions.setX(minimumDimensions.getX());
            }
            if (pixelDimensions.getY() < minimumDimensions.getY()) {
                pixelDimensions.setY(minimumDimensions.getY());
            }
        }
        if (maximumDimensions != null) {
            if (pixelDimensions.getX() > maximumDimensions.getX()) {
                pixelDimensions.setX(maximumDimensions.getX());
            }
            if (pixelDimensions.getY() > maximumDimensions.getY()) {
                pixelDimensions.setY(maximumDimensions.getY());
            }
        }
        return sprite.setDimensions(pixelDimensions);
    }

    /**
     * Get this component's minimum horizontal and vertical dimensions in pixels
     * @return This component's minimum dimensions in pixels
     */
    public Vector getMinimumDimensions() {
        return minimumDimensions;
    }

    /**
     * Set this component's minimum horizontal and vertical dimensions in pixels
     * @param minimumDimensions This component's new minimum dimensions in pixels
     * @return Whether the given dimensions were valid
     */
    public boolean setMinimumDimensions(Vector minimumDimensions) {
        if (minimumDimensions == null) {
            this.minimumDimensions = null;
            return setDimensions(getDimensions().clone());
        }
        if (minimumDimensions.getX() < 0.0f || minimumDimensions.getY() < 0.0f) {
            return false;
        }
        this.minimumDimensions = minimumDimensions;
        return setDimensions(getDimensions().clone());
    }

    /**
     * Get this component's maximum horizontal and vertical dimensions in pixels
     * @return This component's maximum dimensions in pixels
     */
    public Vector getMaximumDimensions() {
        return maximumDimensions;
    }

    /**
     * Set this component's maximum horizontal and vertical dimensions in pixels
     * @param maximumDimensions This component's new maximum dimensions in pixels
     * @return Whether the given dimensions were valid
     */
    public boolean setMaximumDimensions(Vector maximumDimensions) {
        if (maximumDimensions == null) {
            this.maximumDimensions = null;
            return setDimensions(getDimensions().clone());
        }
        if (maximumDimensions.getX() < 0.0f || maximumDimensions.getY() < 0.0f) {
            return false;
        }
        this.maximumDimensions = maximumDimensions;
        return setDimensions(getDimensions().clone());
    }

}
