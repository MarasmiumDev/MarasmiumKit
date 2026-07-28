/**
 * File:        TestScene2.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.06
 * Purpose:     Defines another scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.input.KeyboardKey;
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
        // Switch to scene one on 1 key pressed
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.One)) {
            App.SetCurrentScene(AppTest.Test_Scene_1);
        }
        return true;
    }

    @Override
    public void draw() {

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

}
