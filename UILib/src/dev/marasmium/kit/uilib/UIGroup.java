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

/**
 * A collection of user-interface components
 */
public class UIGroup implements UIListener {

    /**
     * The parent user-interface listener subscribed to user-interface events from this group
     */
    protected UIListener parent = null;
    /**
     * The set of user-interface components in this group
     */
    protected final ArrayList<UIComponent> components = new ArrayList<>();
    /**
     * The smallest depth to draw user-interface components at in this group
     */
    protected float baseDepth = 0.0f;
    /**
     * The typeface to draw text on this group's user-interface components
     */
    protected String typefaceFilePath = null;
    /**
     * The size to draw text at on this group's user-interface components
     */
    protected float textSize = 0.0f;
    /**
     * The padding to be placed around text drawn on this group's user-interface components
     */
    protected float textPadding = 0.0f;
    /**
     * Whether this group's user-interface components may be currently enabled
     */
    protected boolean enabled = false;
    /**
     * Whether this group's user-interface components may be currently visible
     */
    protected boolean visible = false;

    /**
     * The ID of this group
     */
    private int groupID = 0;
    /**
     * The camera used to draw this group's user-interface components
     */
    private final Camera camera = new Camera();
    /**
     * The next ID to assign to a user-interface component added to this group
     */
    private int nextComponentID = 0;
    /**
     * A copy of the dimensions of the application's window
     */
    private Vector windowDimensions = null;

    /**
     * Initialize this user-interface group's memory
     * @param parent The parent user-interface listener for this group
     * @param groupID The ID for this group
     * @param baseDepth The smallest depth to draw user-interface components in this group at
     * @param typefaceFilePath The typeface to draw text in on this group's user-interface components
     * @param textPadding The padding to place around text drawn on this group's user-interface components
     * @param textSize The size to draw text at on this group's user-interface components
     * @return Whether all parameters were valid and this group was initialized successfully
     */
    public boolean initialize(UIListener parent, int groupID, float baseDepth, String typefaceFilePath,
                              float textPadding, float textSize) {
        if (!setParent(parent)) {
            return false;
        }
        setBaseDepth(baseDepth);
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
        setGroupID(groupID);
        if (!camera.initialize(Vector.Zero(), 1.0f, Angle.Zero())) {
            return false;
        }
        nextComponentID = 1;
        return true;
    }

    /**
     * Process user-input to this group's user-interface components
     */
    public void processInput() {
        if (!enabled) {
            return;
        }
        for (UIComponent component : components) {
            component.processInput();
        }
    }

    /**
     * Draw this group's user-interface components' graphics
     */
    public void draw() {
        if (!visible) {
            return;
        }
        for (UIComponent component : components) {
            component.draw();
        }
    }

    /**
     * Update this group's user-interface components' logic
     * @param deltaFrames The number of frames elapsed since the last call to update
     */
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

    /**
     * Free this group's memory and destroy its child user-interface components
     */
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

    /**
     * Update the pixel dimensions of this group's child user-interface components to scale with the application's
     * window dimensions
     * @return Whether all the user-interface components' dimensions were updated successfully
     */
    private boolean updateWindowDimensions() {
        if (App.Window.getDimensions() == null) {
            return false;
        }
        windowDimensions = App.Window.getDimensions().clone();
        for (UIComponent component : components) {
            if (component == null) {
                continue;
            }
            Vector position = component.getPosition();
            Vector dimensions = component.getDimensions();
            if (!component.setDimensions(dimensions)) {
                return false;
            }
            if (!component.setPosition(position)) {
                return false;
            }
        }
        camera.setPosition(windowDimensions.scalarMultiply(0.5f));
        return true;
    }

    /**
     * Pass a user-interface component event to this group's parent user-interface listener
     * @param groupID The ID of the user-interface group the relevant component is in
     * @param componentID The ID of the relevant component
     * @param event The event identifier
     */
    @Override
    public void componentEvent(int groupID, int componentID, UIEvent event) {
        parent.componentEvent(groupID, componentID, event);
    }

    /**
     * Get the parent user-interface listener of this group
     * @return This group's parent
     */
    @Override
    public UIListener getParent() {
        return parent;
    }

    /**
     * Get the smallest depth which this group's user-interface components are drawn at
     * @return This group's base depth
     */
    @Override
    public float getBaseDepth() {
        return baseDepth;
    }

    /**
     * Get the typeface text is drawn in on this group's user-interface components
     * @return This group's typeface
     */
    @Override
    public String getTypefaceFilePath() {
        return typefaceFilePath;
    }

    /**
     * Get the size text is drawn in on this group's user-interface components in percent of the typeface's original
     * size
     * @return This group's text size
     */
    @Override
    public float getTextSize() {
        return textSize;
    }

    /**
     * Get the padding placed around text drawn on this group's user-interface components in pixels
     * @return This group's text padding
     */
    @Override
    public float getTextPadding() {
        return textPadding;
    }

    /**
     * Get the group's ID
     * @return This group's ID
     */
    @Override
    public int getGroupID() {
        return groupID;
    }

    /**
     * Get the camera used to draw this group's user-interface components
     * @return This group's camera
     */
    @Override
    public Camera getCamera() {
        return camera;
    }

    /**
     * Set the parent user-interface listener for this group
     * @param parent This group's new parent
     * @return Whether the given parent was valid
     */
    public boolean setParent(UIListener parent) {
        if (parent == null) {
            return false;
        }
        this.parent = parent;
        return true;
    }

    /**
     * Get this group's set of child user-interface components
     * @return This group's components
     */
    public ArrayList<UIComponent> getComponents() {
        return components;
    }

    /**
     * Get a user-interface component by its ID in this group
     * @param componentID The user-interface component ID to find
     * @return The user-interface component with the given ID or null if none was found
     */
    public UIComponent getComponent(int componentID) {
        for (UIComponent component : components) {
            if (component.getComponentID() == componentID) {
                return component;
            }
        }
        return null;
    }

    /**
     * Add a user-interface component to be managed by this group
     * @param component The component to add
     * @return Whether the given component was added successfully
     */
    public boolean addComponent(UIComponent component) {
        if (components.contains(component)) {
            return false;
        }
        component.setComponentID(nextComponentID++);
        component.setParent(this);
        components.add(component);
        return true;
    }

    /**
     * Remove a user-interface component from management by this group
     * @param component The component to remove
     * @return Whether the given component was removed successfully
     */
    public boolean removeComponent(UIComponent component) {
        if (!components.contains(component)) {
            return false;
        }
        component.setComponentID(0);
        component.setParent(null);
        components.remove(component);
        return true;
    }

    /**
     * Set the lowest depth to draw this group's user-interface components at
     * @param baseDepth The new base depth for this group
     */
    public void setBaseDepth(float baseDepth) {
        for (UIComponent component : components) {
            component.setBaseDepth(baseDepth);
        }
        this.baseDepth = baseDepth;
    }

    /**
     * Set the typeface to draw text in on this group's user-interface components
     * @param typefaceFilePath This group's new typeface
     * @return Whether the given typeface was valid
     */
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

    /**
     * Set the size to draw text at on this group's user-interface components in percent of the typeface's original size
     * @param textSize This group's new text size in percent of the typeface's original size
     * @return Whether the given text size was valid
     */
    public boolean setTextSize(float textSize) {
        if (textSize <= 0.0f) {
            return false;
        }
        this.textSize = textSize;
        return true;
    }

    /**
     * Set the padding to be placed around text drawn on this group's user interface components in pixels
     * @param textPadding This group's new text padding in pixels
     * @return Whether the given text padding was valid
     */
    public boolean setTextPadding(float textPadding) {
        if (textPadding <= 0.0f) {
            return false;
        }
        this.textPadding = textPadding;
        return true;
    }

    /**
     * Test whether this group's user-interface components may be enabled
     * @return Whether this group is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set whether this group's user-interface components may be enabled
     * @param enabled Whether this group should be enabled
     */
    public void setEnabled(boolean enabled) {
        for (UIComponent component : components) {
            component.setEnabled(enabled);
        }
        this.enabled = enabled;
    }

    /**
     * Test whether this group's user-interface components may be visible
     * @return Whether this group is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set whether this group's user-interface components may be visible
     * @param visible Whether this group should be visible
     */
    public void setVisible(boolean visible) {
        for (UIComponent component : components) {
            component.setVisible(visible);
        }
        this.visible = visible;
    }

    /**
     * Set this group's ID
     * @param groupID This group's new ID
     */
    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

}
