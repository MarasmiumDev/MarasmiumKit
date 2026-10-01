/**
 * File:        UIGroup.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.26
 * Purpose:     Defines a data structure for managing and drawing a collection of user-interface components
 */

package dev.marasmium.kit.uilib;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.graphics.Camera;
import dev.marasmium.kit.uilib.component.UIComponent;

import java.util.ArrayList;

public class UIGroup implements UIListener {

    protected UIListener parent = null;
    protected final ArrayList<UIComponent> components = new ArrayList<>();
    protected float baseDepth = 0.0f;
    protected String typefaceFilePath = null;
    protected float textSize = 0.0f;
    protected float textPadding = 0.0f;
    protected boolean enabled = false;
    protected boolean visible = false;

    private int groupID = 0;
    private final Camera camera = new Camera();
    private int nextComponentID = 0;
    private Vector windowDimensions = null;

    public boolean initialize(UIListener parent, int groupID, float depth, String typefaceFilePath, float textPadding,
                              float textSize) {
        setBaseDepth(depth);
        if (!setTypefaceFilePath(typefaceFilePath)) {
            return false;
        }
        if (!setTextSize(textSize)) {
            return false;
        }
        if (!setTextPadding(textPadding)) {
            return false;
        }
        setEnabled(true);
        setVisible(true);
        if (!setParent(parent)) {
            return false;
        }
        setGroupID(groupID);
        if (!camera.initialize(Vector.Zero(), 1.0f, Angle.Zero())) {
            return false;
        }
        nextComponentID = 1;
        return true;
    }

    public void processInput() {
        if (!enabled) {
            return;
        }
        for (UIComponent component : components) {
            component.processInput();
        }
    }

    public void draw() {
        if (!visible) {
            return;
        }
        for (UIComponent component : components) {
            component.draw();
        }
    }

    public void update(float deltaFrames) {
        camera.update(deltaFrames);
        for (UIComponent component : components) {
            component.update(deltaFrames);
        }
        if (App.Window.getDimensions() == null) {
            updateWindowDimensions();
        } else if (!App.Window.getDimensions().equals(windowDimensions)) {
            updateWindowDimensions();
        }
    }

    public void destroy() {
        for (UIComponent component : components) {
            component.destroy();
        }
        components.clear();
        baseDepth = 0.0f;
        typefaceFilePath = null;
        textSize = 0.0f;
        textPadding = 0.0f;
        enabled = false;
        visible = false;
        parent = null;
        groupID = 0;
        camera.destroy();
        nextComponentID = 0;
        windowDimensions = null;
    }

    private boolean updateWindowDimensions() {
        if (App.Window.getDimensions() == null) {
            return false;
        }
        boolean success = true;
        windowDimensions = App.Window.getDimensions().clone();
        for (UIComponent component : components) {
            if (component == null) {
                continue;
            }
            Vector position = component.getPosition();
            Vector dimensions = component.getDimensions();
            if (!component.setPosition(position)) {
                success = false;
            }
            if (!component.setDimensions(dimensions)) {
                success = false;
            }
        }
        camera.setPosition(windowDimensions.scalarMultiply(0.5f));
        return success;
    }

    @Override
    public void buttonEvent(int groupID, int buttonID, UIEvent event) {
        parent.buttonEvent(groupID, buttonID, event);
    }

    @Override
    public void switchEvent(int groupID, int switchID, UIEvent event) {
        parent.switchEvent(groupID, switchID, event);
    }

    @Override
    public UIListener getParent() {
        return parent;
    }

    @Override
    public float getBaseDepth() {
        return baseDepth;
    }

    @Override
    public String getTypefaceFilePath() {
        return typefaceFilePath;
    }

    @Override
    public float getTextSize() {
        return textSize;
    }

    @Override
    public float getTextPadding() {
        return textPadding;
    }

    @Override
    public int getGroupID() {
        return groupID;
    }

    @Override
    public Camera getCamera() {
        return camera;
    }

    public boolean setParent(UIListener parent) {
        if (parent == null) {
            return false;
        }
        this.parent = parent;
        return true;
    }

    public ArrayList<UIComponent> getComponents() {
        return components;
    }

    public UIComponent getComponent(int componentID) {
        for (UIComponent component : components) {
            if (component.getComponentID() == componentID) {
                return component;
            }
        }
        return null;
    }

    public boolean addComponent(UIComponent component) {
        if (components.contains(component)) {
            return false;
        }
        component.setComponentID(nextComponentID++);
        component.setParent(this);
        components.add(component);
        return true;
    }

    public boolean removeComponent(UIComponent component) {
        if (!components.contains(component)) {
            return false;
        }
        component.setComponentID(0);
        component.setParent(null);
        components.remove(component);
        return true;
    }

    public void setBaseDepth(float baseDepth) {
        for (UIComponent component : components) {
            component.setBaseDepth(baseDepth);
        }
        this.baseDepth = baseDepth;
    }

    public boolean setTypefaceFilePath(String typefaceFilePath) {
        if (typefaceFilePath == null) {
            return false;
        }
        if (typefaceFilePath.isEmpty()) {
            return false;
        }
        this.typefaceFilePath = typefaceFilePath;
        return true;
    }

    public boolean setTextSize(float textSize) {
        if (textSize <= 0.0f) {
            return false;
        }
        this.textSize = textSize;
        return true;
    }

    public boolean setTextPadding(float textPadding) {
        if (textPadding <= 0.0f) {
            return false;
        }
        this.textPadding = textPadding;
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        for (UIComponent component : components) {
            component.setEnabled(enabled);
        }
        this.enabled = enabled;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        for (UIComponent component : components) {
            component.setVisible(visible);
        }
        this.visible = visible;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

}
