/**
 * File:        TestScene1.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.23
 * Purpose:     Defines the initial scene of the testing MarasmiumKit app
 */

package dev.marasmium.kit.apptest;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.Scene;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.input.KeyboardKey;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private int frames = 0;
    private double delta = 0.0d;

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        frames = 0;
        delta = 0.0d;
        return true;
    }

    @Override
    public boolean processInput() {
        // Switch to scene two on 2 key pressed
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Two)) {
            App.SetCurrentScene(AppTest.Test_Scene_2);
        }
        // Switch to fullscreen
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.F)) {
            App.Log.write(logSource, LogLevel.Info, "Toggle fullscreen");
            App.Window.setFullscreen(!App.Window.isFullscreen());
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.D)) {
            App.Window.setDimensions(Vector.Cartesian(640.0d, 360.0d));
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.G)) {
            App.Window.setDimensions(Vector.Cartesian(1280.0d, 720.0d));
        }
        return true;
    }

    @Override
    public void draw() {
        frames++;
    }

    @Override
    public void update(double deltaFrames) {
        if (delta > App.Graphics.getTargetFPS() * 2) {
            App.Log.write(logSource, LogLevel.Info, "Average FPS: ", frames / 2);
            frames = 0;
            delta = 0.0d;
        }
        delta += deltaFrames;
    }

    @Override
    public boolean leave(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        return true;
    }
    
}
