/**
 * File:        TestScene2.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.06
 * Purpose:     Defines another scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Vec2D;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

public class TestScene2 extends Scene {

    private final LogSource logSource = new LogSource("Test Scene 2");

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 2");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 2");
        return true;
    }

    @Override
    public boolean processInput() {
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.ONE)) {
            App.SetCurrentScene(AppTest.Test_Scene_1);
        }
        return true;
    }

    @Override
    public void update(double deltaFrames) {

    }

    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 2");
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 2");
        return true;
    }

    @Override
    public void keyboardKeyPressed(KeyboardKey key) {
        App.Log.write(logSource, LogLevel.Info, "Key ", key, " pressed");
    }

    @Override
    public void keyboardKeyReleased(KeyboardKey key) {
        App.Log.write(logSource, LogLevel.Info, "Key ", key, " released");
    }

    @Override
    public void keyboardCharTyped(char c) {
        App.Log.write(logSource, LogLevel.Info, "Character '", c, "' typed");
    }

    @Override
    public void mouseButtonPressed(MouseButton button) {
        App.Log.write(logSource, LogLevel.Info, "Button ", button, " pressed");
    }

    @Override
    public void mouseButtonReleased(MouseButton button) {
        App.Log.write(logSource, LogLevel.Info, "Button ", button, " released");
    }

    @Override
    public void mouseCursorMoved(Vec2D position, Vec2D movement) {
        App.Log.write(logSource, LogLevel.Info, "Cursor moved to ", position, " by ", movement);
    }

    @Override
    public void mouseScrollMoved(Vec2D movement) {
        App.Log.write(logSource, LogLevel.Info, "Scrolled ", movement);
    }

}
