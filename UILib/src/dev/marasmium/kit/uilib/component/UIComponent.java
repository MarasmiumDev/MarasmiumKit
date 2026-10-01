/**
 * File:        UIComponent.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     Defines an abstract user-interface component in a UI group
 */

package dev.marasmium.kit.uilib.component;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.applib.graphics.Sprite;
import dev.marasmium.kit.uilib.UIGroup;

public abstract class UIComponent extends UIGroup {

    protected int componentID = 0;
    protected final Sprite sprite = new Sprite();
    protected Vector minimumDimensions = null;
    protected Vector maximumDimensions = null;

    private Vector windowDimensions = null;

    @Override
    public abstract void processInput();

    @Override
    public abstract void draw();

    @Override
    public abstract void update(float deltaFrames);

    @Override
    public void destroy() {
        super.destroy();
        componentID = 0;
        sprite.destroy();
        minimumDimensions = null;
        maximumDimensions = null;
        windowDimensions = null;
    }

    @Override
    public float getBaseDepth() {
        return parent.getBaseDepth();
    }

    @Override
    public String getTypefaceFilePath() {
        return parent.getTypefaceFilePath();
    }

    @Override
    public float getTextSize() {
        return parent.getTextSize();
    }

    @Override
    public float getTextPadding() {
        return parent.getTextPadding();
    }

    @Override
    public int getGroupID() {
        return parent.getGroupID();
    }

    @Override
    public Camera getCamera() {
        return parent.getCamera();
    }

    public int getComponentID() {
        return componentID;
    }

    public void setComponentID(int componentID) {
        this.componentID = componentID;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Vector getPosition() {
        if (windowDimensions == null || sprite.getPosition() == null) {
            return null;
        }
        return sprite.getPosition().elementDivide(windowDimensions);
    }

    public boolean setPosition(Vector position) {
        windowDimensions = App.Window.getDimensions().clone();
        if (windowDimensions == null || position == null) {
            return false;
        }
        return sprite.setPosition(position.elementMultiply(windowDimensions));
    }

    public Vector getDimensions() {
        if (windowDimensions == null || sprite.getDimensions() == null) {
            return null;
        }
        return sprite.getDimensions().elementDivide(windowDimensions);
    }

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

    public Vector getMinimumDimensions() {
        return minimumDimensions;
    }

    public void setMinimumDimensions(Vector minimumDimensions) {
        this.minimumDimensions = minimumDimensions;
    }

    public Vector getMaximumDimensions() {
        return maximumDimensions;
    }

    public void setMaximumDimensions(Vector maximumDimensions) {
        this.maximumDimensions = maximumDimensions;
    }

}
