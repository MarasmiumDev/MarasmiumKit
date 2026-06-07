/**
 * File:        MouseManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.06
 * Purpose:     Defines a manager class for handling mouse user-input
 */

package dev.marasmium.kit.applib.input;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Vec2D;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.awt.event.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manager class for handling mouse user-input
 */
public class MouseManager implements MouseListener, MouseMotionListener, MouseWheelListener {

    /**
     * Data structure representing the current and last state of a button on the mouse
     */
    private static class ButtonState {

        /**
         * Whether the button is currently pressed down
         */
        public boolean isDown = false;
        /**
         * Whether the button was pressed down in the last logic update
         */
        public boolean wasDown = false;

    }

    /**
     * Whether the mouse user-input manager has been initialized
     */
    private boolean initialized = false;
    /**
     * Set of Java AWT virtual button codes mapped to their names in the MarasmiumKit application framework
     */
    private HashMap<Integer, MouseButton> AWTButtonCodes = null;
    /**
     * The current and previous logic update's states of the buttons on the mouse which have been interacted with since
     * the application framework was initialized
     */
    private ConcurrentHashMap<MouseButton, ButtonState> buttonStates = null;
    /**
     * The current position of the mouse cursor on the application framework's window
     */
    private Vec2D cursorPosition = null;
    /**
     * The position of the mouse cursor on the application framework's window in the last logic update
     */
    private Vec2D lastCursorPosition = null;
    /**
     * Scope lock for modifying and reading the position of the mouse cursor
     */
    private ReentrantLock cursorPositionLock = null;
    /**
     * The current logic update's accumulated scroll movement
     */
    private Vec2D scrollMovement = null;
    /**
     * Scope lock for modifying and reading the mouse's scroll movement
     */
    private ReentrantLock scrollMovementLock = null;

    /**
     * Initialize the mouse user-input manager's memory and subscribe to input events from the AWT window panel
     * @return Whether the mouse user-input manager was initialized successfully
     */
    public boolean initialize() {
        if (initialized) {
            return false;
        }
        // Register all mouse buttons with their AWT virtual button codes
        AWTButtonCodes = new HashMap<>();
        for (MouseButton button : MouseButton.values()) {
            try {
                Field field = MouseEvent.class.getField(button.toString());
                int AWTButtonCode = field.getInt(null);
                AWTButtonCodes.put(AWTButtonCode, button);
            } catch (Exception _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to map \"", button, "\" mouse button");
                return false;
            }
        }
        // Initialize memory and subscribe to input events
        buttonStates = new ConcurrentHashMap<>();
        cursorPosition = Vec2D.Cartesian(0.0d, 0.0d);
        lastCursorPosition = Vec2D.Cartesian(0.0d, 0.0d);
        cursorPositionLock = new ReentrantLock();
        scrollMovement = Vec2D.Cartesian(0.0d, 0.0d);
        scrollMovementLock = new ReentrantLock();
        App.Window.getPanel().addMouseListener(this);
        App.Window.getPanel().addMouseMotionListener(this);
        App.Window.getPanel().addMouseWheelListener(this);
        App.Log.write(LogSource.Input, LogLevel.Info, "Initialized mouse user-input management system");
        initialized = true;
        return true;
    }

    /**
     * Update the mouse user-input manager's internal state of the mouse
     */
    public void update() {
        // Update button up/down states
        for (Map.Entry<MouseButton, ButtonState> entry : buttonStates.entrySet()) {
            ButtonState buttonState = entry.getValue();
            if (buttonState.isDown != buttonState.wasDown) {
                if (buttonState.isDown) {
                    App.Input.mouseButtonPressed(entry.getKey());
                } else {
                    App.Input.mouseButtonReleased(entry.getKey());
                }
                buttonState.wasDown = buttonState.isDown;
            }
        }
        // Update cursor position
        cursorPositionLock.lock();
        if (!cursorPosition.equals(lastCursorPosition)) {
            App.Input.mouseCursorMoved(cursorPosition, cursorPosition.subtract(lastCursorPosition));
            lastCursorPosition = Vec2D.Cartesian(cursorPosition.getX(), cursorPosition.getY());
        }
        try {
            cursorPositionLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release cursor position scope lock");
        }
        // Update scroll movement
        scrollMovementLock.lock();
        if (!scrollMovement.isZero()) {
            App.Input.mouseScrollMoved(scrollMovement);
            scrollMovement = Vec2D.Cartesian(0.0d, 0.0d);
        }
        try {
            scrollMovementLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release scroll movement scope lock");
        }
    }

    /**
     * Free the mouse user-input manager's memory and unsubscribe it from AWT input events
     * @return Whether the mouse user-input manager was destroyed successfully
     */
    public boolean destroy() {
        if (!initialized) {
            return false;
        }
        App.Log.write(LogSource.Input, LogLevel.Info, "Destroying mouse user-input management system");
        boolean success = true;
        // Detach mouse listeners
        App.Window.getPanel().removeMouseListener(this);
        App.Window.getPanel().removeMouseMotionListener(this);
        App.Window.getPanel().removeMouseWheelListener(this);
        // Free memory
        AWTButtonCodes.clear();
        AWTButtonCodes = null;
        buttonStates.clear();
        buttonStates = null;
        cursorPosition = null;
        lastCursorPosition = null;
        cursorPositionLock = null;
        scrollMovement = null;
        scrollMovementLock = null;
        initialized = false;
        return success;
    }

    /**
     * Test whether a button was down on the mouse in the last logic update
     * @param button The button to test
     * @return Whether the given button was down in the last logic update
     */
    private boolean wasButtonDown(MouseButton button) {
        if (!buttonStates.containsKey(button)) {
            return false;
        }
        return buttonStates.get(button).wasDown;
    }

    /**
     * Test whether the mouse user-input management system has been initialized
     * @return Whether the mouse user-input management system is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Test whether a button is currently down on the mouse
     * @param button The button to test
     * @return Whether the given button is currently down
     */
    public boolean isButtonDown(MouseButton button) {
        if (!buttonStates.containsKey(button)) {
            return false;
        }
        return buttonStates.get(button).isDown;
    }

    /**
     * Test whether a button was just pressed down on the mouse
     * @param button The button to test
     * @return Whether the given button was just pressed down
     */
    public boolean isButtonPressed(MouseButton button) {
        return isButtonDown(button) && !wasButtonDown(button);
    }

    /**
     * Test whether a button was just released on the mouse
     * @param button The button to test
     * @return Whether the given button was just released
     */
    public boolean isButtonReleased(MouseButton button) {
        return !isButtonDown(button) && wasButtonDown(button);
    }

    /**
     * Get the current position of the mouse cursor on the application framework's window
     * @return The current position of the mouse cursor
     */
    public Vec2D getCursorPosition() {
        cursorPositionLock.lock();
        Vec2D cursorPosition = this.cursorPosition;
        try {
            cursorPositionLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release cursor position scope lock");
        }
        return cursorPosition;
    }

    /**
     * Get the horizontal and vertical distance the mouse cursor has moved since the last logic update
     * @return The most recent movement of the mouse cursor
     */
    public Vec2D getCursorMovement() {
        return getCursorPosition().subtract(lastCursorPosition);
    }

    /**
     * Get the mouse's accumulated scroll movement since the last logic update
     * @return The most recent mouse scroll movement
     */
    public Vec2D getScrollMovement() {
        scrollMovementLock.lock();
        Vec2D scrollMovement = this.scrollMovement;
        try {
            scrollMovementLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release scroll movement scope lock");
        }
        return scrollMovement;
    }

    /**
     * Callback for Java AWT mouse click events (unused)
     * @param e The event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {}

    /**
     * Callback for Java AWT button press events
     * @param e The event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        MouseButton button = AWTButtonCodes.get(e.getButton());
        if (button == null) {
            return;
        }
        if (!buttonStates.containsKey(button)) {
            buttonStates.put(button, new ButtonState());
        }
        buttonStates.get(button).isDown = true;
    }

    /**
     * Callback for Java AWT button release events
     * @param e The event to be processed
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        MouseButton button = AWTButtonCodes.get(e.getButton());
        if (button == null) {
            return;
        }
        if (!buttonStates.containsKey(button)) {
            buttonStates.put(button, new ButtonState());
        }
        buttonStates.get(button).isDown = false;
    }

    /**
     * Callback for cursor window-entering events (unused)
     * @param e The event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {}

    /**
     * Callback for cursor window-exiting events (unused)
     * @param e The event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {}

    /**
     * Callback for cursor dragging events (passed e to mouseMoved() function)
     * @param e The event to be processed
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * Callback for cursor movement events
     * @param e The event to be processed
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        cursorPositionLock.lock();
        cursorPosition = Vec2D.Cartesian(e.getX(), App.Window.getDimensions().getY() - (double)e.getY());
        try {
            cursorPositionLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release cursor position scope lock");
        }
    }

    /**
     * Callback for scroll movement events
     * @param e The event to be processed
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Vec2D movement = Vec2D.Cartesian(0.0d, 0.0d);
        if (!e.isShiftDown()) {
            movement.setY(e.getPreciseWheelRotation());
        } else {
            movement.setX(e.getPreciseWheelRotation());
        }
        scrollMovementLock.lock();
        scrollMovement = scrollMovement.add(movement);
        try {
            scrollMovementLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release scroll movement scope lock");
        }
    }

}
