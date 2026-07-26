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
     * Set of Java AWT virtual key codes mapped to their names in the MarasmiumKit application framework
     */
    private final HashMap<Integer, KeyboardKey> AWTKeyCodes = new HashMap<>();
    /**
     * The current and previous logic update's  states of the keys on the keyboard which have been interacted with since
     * the application framework was initialized
     */
    private final ConcurrentHashMap<KeyboardKey, KeyState> keyStates = new ConcurrentHashMap<>();
    /**
     * The set of characters typed on the keyboard since the last logic update
     */
    private String typedChars = null;
    /**
     * Scope lock for modifying and reading the set of typed characters asynchronously
     */
    private final ReentrantLock typedCharsLock = new ReentrantLock();

    /**
     * Initialize the keyboard user-input manager's memory and subscribe to input events from the AWT window panel
     * @return Whether the keyboard user-input manager was initialized successfully
     */
    public boolean initialize() {
        // Register all keyboard keys with their AWT virtual key codes
        for (KeyboardKey key : KeyboardKey.values()) {
            Field field;
            try {
                field = KeyEvent.class.getField(key.toString());
            } catch (NoSuchFieldException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to map \"", key, "\" key, no AWT field");
                return false;
            }
            int AWTKeyCode;
            try {
                AWTKeyCode = field.getInt(null);
            } catch (IllegalArgumentException | IllegalAccessException | NullPointerException
                     | ExceptionInInitializerError _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to map \"", key, "\" key, inaccessible ",
                        "AWT field");
                return false;
            }
            AWTKeyCodes.put(AWTKeyCode, key);
        }
        // Initialize memory and subscribe to input events
        typedChars = "";
        App.Window.getPanel().addKeyListener(this);
        App.Log.write(LogSource.Input, LogLevel.Info, "Initialized keyboard user-input management system");
        return true;
    }

    /**
     * Update the keyboard user-input manager's internal state of the keyboard
     */
    public void update() {
        // Update key up/down states
        for (HashMap.Entry<KeyboardKey, KeyState> entry : keyStates.entrySet()) {
            KeyboardKey key;
            try {
                key = entry.getKey();
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to retrieve keyboard key entry");
                continue;
            }
            KeyState keyState;
            try {
                keyState = entry.getValue();
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to retrieve keyboard key state entry");
                continue;
            }
            if (keyState.isDown != keyState.wasDown) {
                if (keyState.isDown) {
                    App.Input.keyboardKeyPressed(key);
                } else {
                    App.Input.keyboardKeyReleased(key);
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
        App.Log.write(LogSource.Input, LogLevel.Info, "Destroying keyboard user-input management system");
        boolean success = true;
        // Detach key listener
        App.Window.getPanel().removeKeyListener(this);
        // Free memory
        AWTKeyCodes.clear();
        keyStates.clear();
        typedChars = null;
        return success;
    }

    /**
     * Test whether a key was down on the keyboard in the last logic update
     * @param key The key to test
     * @return Whether the given key was down in the last logic update
     */
    private boolean wasKeyDown(KeyboardKey key) {
        if (key == null) {
            return false;
        }
        KeyState state = keyStates.get(key);
        if (state == null) {
            return false;
        }
        return state.wasDown;
    }

    /**
     * Test whether a key is currently down on the keyboard
     * @param key The key to test
     * @return Whether the given key is currently down
     */
    public boolean isKeyDown(KeyboardKey key) {
        if (key == null) {
            return false;
        }
        KeyState state = keyStates.get(key);
        if (state == null) {
            return false;
        }
        return state.isDown;
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
        if (typedChars == null) {
            return "";
        }
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
        if (e == null) {
            return;
        }
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
        if (e == null) {
            return;
        }
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
        if (e == null) {
            return;
        }
        typedCharsLock.lock();
        if (typedChars == null) {
            return;
        }
        typedChars += e.getKeyChar();
        try {
            typedCharsLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Typed characters scope lock failed to release");
        }
    }

}
