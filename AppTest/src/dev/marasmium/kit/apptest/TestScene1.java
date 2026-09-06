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
import dev.marasmium.kit.applib.input.MouseButton;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.networking.NetListener;
import dev.marasmium.kit.applib.networking.NetMessage;
import dev.marasmium.kit.applib.windowing.Monitor;

public class TestScene1 extends Scene implements NetListener {

    private final LogSource logSource = new LogSource("Test Scene 1");

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }

    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        return true;
    }

    @Override
    public boolean processInput() {
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.T)) {
            App.Window.setTitle("Test 1");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.Y)) {
            App.Window.setTitle("Test 2");
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.A)) {
            App.Window.setDimensions(Vector.Cartesian(400.0d, 300.0d));
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.S)) {
            App.Window.setDimensions(Vector.Cartesian(1280.0d, 720.0d));
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.F)) {
            App.Window.setFullscreen(!App.Window.isFullscreen());
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.J)) {
            Monitor m = new Monitor();
            m.initialize(0);
            App.Window.setMonitor(m);
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.K)) {
            Monitor m = new Monitor();
            m.initialize(1);
            App.Window.setMonitor(m);
        }
        if (App.Input.keyboard.isKeyPressed(KeyboardKey.L)) {
            Monitor m = new Monitor();
            m.initialize(2);
            App.Window.setMonitor(m);
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
        App.Log.write(logSource, LogLevel.Info, "Leaving test scene 1");
        return true;
    }

    @Override
    public boolean destroy() {
        App.Log.write(logSource, LogLevel.Info, "Destroying test scene 1");
        return true;
    }

    @Override
    public boolean netConnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Network connected");
        return true;
    }

    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        App.Log.write(logSource, LogLevel.Info, "Received message: ", message);
    }

    @Override
    public void netDisconnected(int clientID) {
        App.Log.write(logSource, LogLevel.Info, "Network disconnected");
    }
    
}
