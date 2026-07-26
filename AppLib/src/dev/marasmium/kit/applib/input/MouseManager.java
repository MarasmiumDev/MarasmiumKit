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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.util.HashMap;
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
     * Set of Java AWT virtual button codes mapped to their names in the MarasmiumKit application framework
     */
    private final HashMap<Integer, MouseButton> AWTButtonCodes = new HashMap<>();
    /**
     * The current and previous logic update's states of the buttons on the mouse which have been interacted with since
     * the application framework was initialized
     */
    private final ConcurrentHashMap<MouseButton, ButtonState> buttonStates = new ConcurrentHashMap<>();
    /**
     * The current position of the mouse cursor on the application framework's window
     */
    private final Vec2D cursorPosition = Vec2D.Cartesian(0.0d, 0.0d);
    /**
     * The position of the mouse cursor on the application framework's window in the last logic update
     */
    private final Vec2D lastCursorPosition = Vec2D.Cartesian(0.0d, 0.0d);
    /**
     * Scope lock for modifying and reading the position of the mouse cursor
     */
    private final ReentrantLock cursorPositionLock = new ReentrantLock();
    /**
     * The current logic update's accumulated scroll movement
     */
    private final Vec2D scrollMovement = Vec2D.Cartesian(0.0d, 0.0d);
    /**
     * Scope lock for modifying and reading the mouse's scroll movement
     */
    private final ReentrantLock scrollMovementLock = new ReentrantLock();

    /**
     * Initialize the mouse user-input manager's memory and subscribe to input events from the AWT window panel
     * @return Whether the mouse user-input manager was initialized successfully
     */
    public boolean initialize() {
        // Register all mouse buttons with their AWT virtual button codes
        for (MouseButton button : MouseButton.values()) {
            Field field;
            try {
                field = MouseEvent.class.getField(button.toString());
            } catch (NoSuchFieldException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to map \"", button, "\" mouse button, no AWT ",
                        "field");
                return false;
            }
            int AWTButtonCode;
            try {
                AWTButtonCode = field.getInt(null);
            } catch (IllegalArgumentException | IllegalAccessException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to map \"", button, "\" mouse button, ",
                        "inaccessible AWT field");
                return false;
            }
            AWTButtonCodes.put(AWTButtonCode, button);
        }
        // Initialize memory and subscribe to input events
        App.Window.getPanel().addMouseListener(this);
        App.Window.getPanel().addMouseMotionListener(this);
        App.Window.getPanel().addMouseWheelListener(this);
        App.Log.write(LogSource.Input, LogLevel.Info, "Initialized mouse user-input management system");
        return true;
    }

    /**
     * Update the mouse user-input manager's internal state of the mouse
     */
    public void update() {
        // Update button up/down states
        for (HashMap.Entry<MouseButton, ButtonState> entry : buttonStates.entrySet()) {
            MouseButton button;
            try {
                button = entry.getKey();
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to retrieve mouse button entry");
                continue;
            }
            ButtonState buttonState;
            try {
                buttonState = entry.getValue();
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Input, LogLevel.Error, "Failed to remove mouse button state entry");
                continue;
            }
            if (buttonState.isDown != buttonState.wasDown) {
                if (buttonState.isDown) {
                    App.Input.mouseButtonPressed(button);
                } else {
                    App.Input.mouseButtonReleased(button);
                }
                buttonState.wasDown = buttonState.isDown;
            }
        }
        // Update cursor position
        cursorPositionLock.lock();
        if (!cursorPosition.equals(lastCursorPosition)) {
            App.Input.mouseCursorMoved(cursorPosition, cursorPosition.subtract(lastCursorPosition));
            lastCursorPosition.setX(cursorPosition.getX());
            lastCursorPosition.setY(cursorPosition.getY());
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
            scrollMovement.setX(0.0d);
            scrollMovement.setY(0.0d);
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
        App.Log.write(LogSource.Input, LogLevel.Info, "Destroying mouse user-input management system");
        boolean success = true;
        // Detach mouse listeners
        App.Window.getPanel().removeMouseListener(this);
        App.Window.getPanel().removeMouseMotionListener(this);
        App.Window.getPanel().removeMouseWheelListener(this);
        // Free memory
        AWTButtonCodes.clear();
        buttonStates.clear();
        return success;
    }

    /**
     * Test whether a button was down on the mouse in the last logic update
     * @param button The button to test
     * @return Whether the given button was down in the last logic update
     */
    private boolean wasButtonDown(MouseButton button) {
        if (button == null) {
            return false;
        }
        ButtonState state = buttonStates.get(button);
        if (state == null) {
            return false;
        }
        return state.wasDown;
    }

    /**
     * Test whether a button is currently down on the mouse
     * @param button The button to test
     * @return Whether the given button is currently down
     */
    public boolean isButtonDown(MouseButton button) {
        if (button == null) {
            return false;
        }
        ButtonState state = buttonStates.get(button);
        if (state == null) {
            return false;
        }
        return state.isDown;
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
        if (e == null) {
            return;
        }
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
        if (e == null) {
            return;
        }
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
        if (e == null) {
            return;
        }
        cursorPositionLock.lock();
        cursorPosition.setX(e.getX());
        cursorPosition.setY(App.Window.getDimensions().getY() - (double)e.getY());
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
        if (e == null) {
            return;
        }
        // Choose scroll movement direction
        Vec2D movement = Vec2D.Cartesian(0.0d, 0.0d);
        if (!e.isShiftDown()) {
            movement.setY(e.getPreciseWheelRotation());
        } else {
            movement.setX(e.getPreciseWheelRotation());
        }
        // Add movement
        scrollMovementLock.lock();
        scrollMovement.setX(scrollMovement.getX() + movement.getX());
        scrollMovement.setY(scrollMovement.getY() + movement.getY());
        try {
            scrollMovementLock.unlock();
        } catch (IllegalMonitorStateException _) {
            App.Log.write(LogSource.Input, LogLevel.Error, "Failed to release scroll movement scope lock");
        }
    }

}
