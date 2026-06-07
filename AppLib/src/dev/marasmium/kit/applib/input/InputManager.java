/**
 * File:        InputManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines the main class of the MarasmiumKit application framework's user-input management system
 */

package dev.marasmium.kit.applib.input;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vec2D;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.util.ArrayList;
import java.util.List;

/**
 * The main class of the MarasmiumKit application framework's user-input management system
 */
public class InputManager implements InputListener {

    /**
     * Whether the user-input management system has been initialized
     */
    private boolean initialized = false;
    /**
     * The set of input listeners subscribed to user-input event callbacks
     */
    private final List<InputListener> listeners = new ArrayList<>();
    /**
     * Keyboard user-input management subsystem
     */
    public final KeyboardManager keyboard = new KeyboardManager();
    /**
     * Mouse user-input management subsystem
     */
    public final MouseManager mouse = new MouseManager();

    /**
     * Initialize the user-input management system and its subsystems
     * @return Whether the user-input management system's subsystems was initialized successfully
     */
    public boolean initialize() {
        if (initialized) {
            return false;
        }
        // Initialize the keyboard manager subsystem
        if (!keyboard.initialize()) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to initialize keyboard user-input manager");
            return false;
        }
        // Initialize the mouse manager subsystem
        if (!mouse.initialize()) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to initialize mouse user-input manager");
            return false;
        }
        App.Log.write(LogSource.Input, LogLevel.Info, "Initialized user-input management system");
        initialized = true;
        return true;
    }

    /**
     * Update the user-input management system's subsystems
     */
    public void update() {
        keyboard.update();
        mouse.update();
    }

    /**
     * Free the user-input management system's memory and destroy its subsystems
     * @return Whether the user-input management system was destroyed successfully
     */
    public boolean destroy() {
        if (!initialized) {
            return false;
        }
        App.Log.write(LogSource.Input, LogLevel.Info, "Destroying user-input management system");
        boolean success = true;
        // Free the mouse manager subsystem
        if (!mouse.destroy()) {
            App.Log.write(LogSource.Input, LogLevel.Warning, "Failed to destroy mouse user-input manager");
            success = false;
        }
        // Free the keyboard manager subsystem
        if (!keyboard.destroy()) {
            App.Log.write(LogSource.Input, LogLevel.Warning, "Failed to destroy keyboard user-input manager");
            success = false;
        }
        initialized = false;
        return success;
    }

    /**
     * Test whether the user-input management system has been initialized
     * @return Whether the user-input management system is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Subscribe a user-input listener to input event callbacks
     * @param listener The user-input listener to add
     * @return Whether the listener was added successfully
     */
    public boolean addListener(InputListener listener) {
        if (listeners.contains(listener)) {
            App.Log.write(LogSource.Input, LogLevel.Warning, "Failed to add user-input listener, already present");
            return false;
        }
        App.Log.write(LogSource.Input, LogLevel.Info, "Adding user-input listener");
        listeners.add(listener);
        return true;
    }

    /**
     * Unsubscribe a user-input listener from input event callbacks
     * @param listener The user-input listener to remove
     * @return Whether the listener was removed successfully
     */
    public boolean removeListener(InputListener listener) {
        if (!listeners.contains(listener)) {
            App.Log.write(LogSource.Input, LogLevel.Warning, "Failed to remove user-input listener, not present");
            return false;
        }
        App.Log.write(LogSource.Input, LogLevel.Info, "Removing user-input listener");
        listeners.remove(listener);
        return true;
    }

    /**
     * Callback for a key pressed on the keyboard
     * @param key The pressed key
     */
    @Override
    public void keyboardKeyPressed(KeyboardKey key) {
        for (InputListener listener : listeners) {
            listener.keyboardKeyPressed(key);
        }
    }

    /**
     * Callback for a key released on the keyboard
     * @param key The released key
     */
    @Override
    public void keyboardKeyReleased(KeyboardKey key) {
        for (InputListener listener : listeners) {
            listener.keyboardKeyReleased(key);
        }
    }

    /**
     * Callback for a character typed on the keyboard
     * @param c The character typed
     */
    @Override
    public void keyboardCharTyped(char c) {
        for (InputListener listener : listeners) {
            listener.keyboardCharTyped(c);
        }
    }

    /**
     * Callback for a button pressed on the mouse
     * @param button The button pressed
     */
    @Override
    public void mouseButtonPressed(MouseButton button) {
        for (InputListener listener : listeners) {
            listener.mouseButtonPressed(button);
        }
    }

    /**
     * Callback for a button released on the mouse
     * @param button The button released
     */
    @Override
    public void mouseButtonReleased(MouseButton button) {
        for (InputListener listener : listeners) {
            listener.mouseButtonReleased(button);
        }
    }

    /**
     * Callback for mouse cursor motion
     * @param position The new position of the mouse cursor on the window
     * @param movement The horizontal and vertical distance the mouse cursor travelled since the last logic update
     */
    @Override
    public void mouseCursorMoved(Vec2D position, Vec2D movement) {
        for (InputListener listener : listeners) {
            listener.mouseCursorMoved(position, movement);
        }
    }

    /**
     * Callback for mouse scroll motion
     * @param movement The horizontal and vertical distance scrolled since the last logic update
     */
    @Override
    public void mouseScrollMoved(Vec2D movement) {
        for (InputListener listener : listeners) {
            listener.mouseScrollMoved(movement);
        }
    }

}
