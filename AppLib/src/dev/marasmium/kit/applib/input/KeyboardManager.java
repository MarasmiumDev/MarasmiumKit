/**
 * File:        KeyboardManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines a manager class for handling keyboard user-input
 */

package dev.marasmium.kit.applib.input;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manager class for handling keyboard user-input
 */
public class KeyboardManager implements KeyListener {

    /**
     * Data structure representing the current and last state of a key on the keyboard
     */
    private static class KeyState {

        /**
         * Whether the key is currently pressed down
         */
        public boolean isDown = false;
        /**
         * Whether the key was pressed down in the last logic update
         */
        public boolean wasDown = false;

    }

    /**
     * Whether the keyboard user-input manager has been initialized
     */
    private boolean initialized = false;
    /**
     * Set of Java AWT virtual key codes mapped to their names in the MarasmiumKit application framework
     */
    private HashMap<Integer, KeyboardKey> AWTKeyCodes = null;
    /**
     * The current and previous logic update's  states of the keys on the keyboard which have been interacted with since
     * the application framework was initialized
     */
    private ConcurrentHashMap<KeyboardKey, KeyState> keyStates = null;
    /**
     * The set of characters typed on the keyboard since the last logic update
     */
    private String typedChars = "";
    /**
     * Scope lock for modifying and reading the set of typed characters asynchronously
     */
    private ReentrantLock typedCharsLock = null;

    /**
     * Initialize the keyboard user-input manager's memory and subscribe to input events from the AWT window panel
     * @return Whether the keyboard user-input manager was initialized successfully
     */
    public boolean initialize() {
        if (initialized) {
            return false;
        }
        // Register all keyboard keys with their AWT virtual key codes
        AWTKeyCodes = new HashMap<>();
        for (KeyboardKey key : KeyboardKey.values()) {
            try {
                Field field = KeyEvent.class.getField(key.toString());
                int AWTKeyCode = field.getInt(null);
                AWTKeyCodes.put(AWTKeyCode, key);
            } catch (Exception _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to map \"", key, "\" key");
                return false;
            }
        }
        // Initialize memory and subscribe to input events
        keyStates = new ConcurrentHashMap<>();
        typedCharsLock = new ReentrantLock();
        App.Window.getPanel().addKeyListener(this);
        App.Log.write(LogSource.Input, LogLevel.Info, "Initialized keyboard user-input management system");
        initialized = true;
        return true;
    }

    /**
     * Update the keyboard user-input manager's internal state of the keyboard
     */
    public void update() {
        // Update key up/down states
        for (Map.Entry<KeyboardKey, KeyState> entry : keyStates.entrySet()) {
            KeyState keyState = entry.getValue();
            if (keyState.isDown != keyState.wasDown) {
                if (keyState.isDown) {
                    App.Input.keyboardKeyPressed(entry.getKey());
                } else {
                    App.Input.keyboardKeyReleased(entry.getKey());
                }
                keyState.wasDown = keyState.isDown;
            }
        }
        // Update typed characters
        typedCharsLock.lock();
        for (char c : typedChars.toCharArray()) {
            App.Input.keyboardCharTyped(c);
        }
        typedChars = "";
        try {
            typedCharsLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Typed characters scope-lock failed to release");
        }
    }

    /**
     * Free the keyboard user-input manager's memory and unsubscribe it from AWT input events
     * @return Whether the keyboard user-input manager was destroyed successfully
     */
    public boolean destroy() {
        if (!initialized) {
            return false;
        }
        App.Log.write(LogSource.Input, LogLevel.Info, "Destroying keyboard user-input management system");
        boolean success = true;
        // Detach key listener
        App.Window.getPanel().removeKeyListener(this);
        // Free memory
        AWTKeyCodes.clear();
        AWTKeyCodes = null;
        keyStates.clear();
        keyStates = null;
        typedChars = "";
        typedCharsLock = null;
        initialized = false;
        return success;
    }

    /**
     * Test whether a key was down on the keyboard in the last logic update
     * @param key The key to test
     * @return Whether the given key was down in the last logic update
     */
    private boolean wasKeyDown(KeyboardKey key) {
        if (!keyStates.containsKey(key)) {
            return false;
        }
        return keyStates.get(key).wasDown;
    }

    /**
     * Test whether the keyboard user-input management system has been initialized
     * @return Whether the keyboard user-input management system is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Test whether a key is currently down on the keyboard
     * @param key The key to test
     * @return Whether the given key is currently down
     */
    public boolean isKeyDown(KeyboardKey key) {
        if (!keyStates.containsKey(key)) {
            return false;
        }
        return keyStates.get(key).isDown;
    }

    /**
     * Test whether a key was just pressed down on the keyboard
     * @param key The key to test
     * @return Whether the given key was just pressed down
     */
    public boolean isKeyPressed(KeyboardKey key) {
        return isKeyDown(key) && !wasKeyDown(key);
    }

    /**
     * Test whether a key was just released on the keyboard
     * @param key The key to test
     * @return Whether the given key was just released
     */
    public boolean isKeyReleased(KeyboardKey key) {
        return !isKeyDown(key) && wasKeyDown(key);
    }

    /**
     * Get the set of characters typed on the keyboard since the last logic update
     * @return The set of characters typed since the last update
     */
    public String getTypedChars() {
        typedCharsLock.lock();
        String typedChars = this.typedChars;
        try {
            typedCharsLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Typed characters scope lock failed to release");
        }
        return typedChars;
    }

    /**
     * Callback function for Java AWT key press events
     * @param e The event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        KeyboardKey key = AWTKeyCodes.get(e.getKeyCode());
        if (key == null) {
            return;
        }
        if (!keyStates.containsKey(key)) {
            keyStates.put(key, new KeyState());
        }
        keyStates.get(key).isDown = true;
    }

    /**
     * Callback function for Java AWT key release events
     * @param e The event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {
        KeyboardKey key = AWTKeyCodes.get(e.getKeyCode());
        if (key == null) {
            return;
        }
        if (!keyStates.containsKey(key)) {
            keyStates.put(key, new KeyState());
        }
        keyStates.get(key).isDown = false;
    }

    /**
     * Callback function for Java AWT keyboard typing events
     * @param e The event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        typedCharsLock.lock();
        typedChars += e.getKeyChar();
        try {
            typedCharsLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Typed characters scope lock failed to release");
        }
    }

}
