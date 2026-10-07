/**
 * File:        UIListener.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.09.28
 * Purpose:     Defines callback functions for user-interface component events and getters
 */

package dev.marasmium.kit.uilib;

import dev.marasmium.kit.applib.graphics.Camera;

/**
 * Abstract interface for user-interface component event callback functions and getters
 */
public interface UIListener {

    /**
     * Get the parent listener of this listener
     * @return This listener's parent
     */
    default UIListener getParent() {
        return null;
    }

    /**
     * Get the smallest depth this listener's child components are drawn at
     * @return This listener's base depth
     */
    default float getBaseDepth() {
        return 0.0f;
    }

    /**
     * Get the typeface used to draw text on this listener's child components
     * @return This listener's typeface
     */
    default String getTypefaceFilePath() {
        return null;
    }

    /**
     * Get the size of text to be drawn on this listener's child components in percent of the typeface's original size
     * @return This listener's text size in percent of the typeface's original size
     */
    default float getTextSize() {
        return 0.0f;
    }

    /**
     * Get the padding to be placed around text drawn on this listener's child components in pixels
     * @return This listener's text padding in pixels
     */
    default float getTextPadding() {
        return 0.0f;
    }

    /**
     * Get the ID of the user-interface group implementing this listener
     * @return This listener's group ID
     */
    default int getGroupID() {
        return 0;
    }

    /**
     * Get the camera used for drawing this listener's  child components
     * @return This listener's camera
     */
    default Camera getCamera() {
        return null;
    }

    /**
     * Callback function for a component event in this listener's group
     * @param groupID The ID of the user-interface group the relevant component is in
     * @param componentID The ID of the relevant component
     * @param event The event identifier
     */
    default void componentEvent(int groupID, int componentID, UIEvent event) {}

}
