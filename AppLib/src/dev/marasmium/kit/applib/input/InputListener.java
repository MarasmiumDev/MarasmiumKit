/**
 * File:        InputListener.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.06
 * Purpose:     Defines an interface for user-input event callbacks
 */

package dev.marasmium.kit.applib.input;

import dev.marasmium.kit.applib.data.Vector;

/**
 * Abstract interface for user-input event callbacks
 */
public interface InputListener {

    /**
     * Callback for a key pressed on the keyboard
     * @param key The pressed key
     */
    default void keyboardKeyPressed(KeyboardKey key) {}

    /**
     * Callback for a key released on the keyboard
     * @param key The released key
     */
    default void keyboardKeyReleased(KeyboardKey key) {}

    /**
     * Callback for a character typed on the keyboard
     * @param c The character typed
     */
    default void keyboardCharTyped(char c) {}

    /**
     * Callback for a button pressed on the mouse
     * @param button The button pressed
     */
    default void mouseButtonPressed(MouseButton button) {}

    /**
     * Callback for a button released on the mouse
     * @param button The button released
     */
    default void mouseButtonReleased(MouseButton button) {}

    /**
     * Callback for mouse cursor motion
     * @param position The new position of the mouse cursor on the window
     * @param movement The horizontal and vertical distance the mouse cursor travelled since the last logic update
     */
    default void mouseCursorMoved(Vector position, Vector movement) {}

    /**
     * Callback for mouse scroll motion
     * @param movement The horizontal and vertical distance scrolled since the last logic update
     */
    default void mouseScrollMoved(Vector movement) {}

}
