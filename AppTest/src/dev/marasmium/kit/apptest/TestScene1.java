/**
 * File:        TestScene1.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.23
 * Purpose:     Defines the initial scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Vec2D;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

public class TestScene1 extends Scene {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private double deltaFrames = 0.0d;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }
    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        deltaFrames = 0.0d;
        return true;
    }
    @Override
    public boolean processInput() {
        // Switch between scenes
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.TWO)) {
            App.SetCurrentScene(AppTest.Test_Scene_2);
        }
        // Test synchronous user-input functions
        if (App.Input.keyboard.isKeyDown(KeyboardKey.A)) {
            App.Log.write(logSource, LogLevel.Info, "A key down");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.S)) {
            App.Log.write(logSource, LogLevel.Info, "S key pressed");
        }
        if (App.Input.keyboard.isKeyReleased(KeyboardKey.S)) {
            App.Log.write(logSource, LogLevel.Info, "S key released");
        }
        String typedChars = App.Input.keyboard.getTypedChars();
        if (!typedChars.isEmpty()) {
            App.Log.write(logSource, LogLevel.Info, "Typed characters: \"", typedChars, "\"");
        }
        if (App.Input.mouse.isButtonDown(MouseButton.LEFT)) {
            App.Log.write(logSource, LogLevel.Info, "Left mouse button down");
        }
        if (App.Input.mouse.isButtonPressed(MouseButton.RIGHT)) {
            App.Log.write(logSource, LogLevel.Info, "Right mouse button pressed");
        }
        if (App.Input.mouse.isButtonReleased(MouseButton.RIGHT)) {
            App.Log.write(logSource, LogLevel.Info, "Right mouse button released");
        }
        Vec2D cursorMovement = App.Input.mouse.getCursorMovement();
        if (!cursorMovement.isZero()) {
            App.Log.write(logSource, LogLevel.Info, "Cursor moved to ", App.Input.mouse.getCursorPosition(), " by ",
                    cursorMovement);
        }
        Vec2D scrollMovement = App.Input.mouse.getScrollMovement();
        if (!scrollMovement.isZero()) {
            App.Log.write(logSource, LogLevel.Info, "Scroll moved by ", App.Input.mouse.getScrollMovement());
        }
        return true;
    }
    @Override
    public void update(double deltaFrames) {
        // Test timing
        this.deltaFrames += deltaFrames;
        if (this.deltaFrames > App.Graphics.getTargetFPS()) {
            this.deltaFrames = 0.0d;
            App.Log.write(logSource, LogLevel.Info, "1 second elapsed");
        }
    }
    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        return true;
    }
    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        deltaFrames = 0.0d;
        return true;
    }

}
