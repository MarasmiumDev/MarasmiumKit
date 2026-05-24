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
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.applib.windowing.Monitor;

import java.util.Arrays;
import java.util.List;

public class TestScene1 extends Scene {

    private final LogSource logSource = new LogSource("Test Scene 1");
    private long startTime = 0L;
    private final boolean[] testFlags = new boolean[64];

    @Override
    public boolean initialize() {
        App.Log.write(logSource, LogLevel.Info, "Initializing test scene 1");
        return true;
    }
    @Override
    public boolean enter(Scene lastScene) {
        App.Log.write(logSource, LogLevel.Info, "Entering test scene 1");
        startTime = System.currentTimeMillis();
        Arrays.fill(testFlags, false);
        return true;
    }
    @Override
    public boolean processInput() {
        if (System.currentTimeMillis() - startTime > (1500 * 1) && !testFlags[1]) {
            App.Log.write(logSource, LogLevel.Info, "Window title: \"", App.Window.getTitle(), "\"");
            testFlags[1] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500 * 2) && !testFlags[2]) {
            if (App.Window.setTitle("TestScene1")) {
                App.Log.write(logSource, LogLevel.Info, "Set window title");
            } else {
                App.Log.write(logSource, LogLevel.Info, "Failed to set window title");
            }
            testFlags[2] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 3) && !testFlags[3]) {
            App.Log.write(logSource, LogLevel.Info, "New window title: \"", App.Window.getTitle(), "\"");
            testFlags[3] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 4) && !testFlags[4]) {
            App.Log.write(logSource, LogLevel.Info, "Window dimensions: ", App.Window.getDimensions());
            testFlags[4] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 5) && !testFlags[5]) {
            if (App.Window.setDimensions(Vec2D.Cartesian(500.0d, 500.0d))) {
                App.Log.write(logSource, LogLevel.Info, "Set window dimensions");
            } else {
                App.Log.write(logSource, LogLevel.Info, "Failed to set window dimensions");
            }
            testFlags[5] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 6) && !testFlags[6]) {
            App.Log.write(logSource, LogLevel.Info, "New window dimensions: ", App.Window.getDimensions());
            testFlags[6] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 7) && !testFlags[7]) {
            App.Log.write(logSource, LogLevel.Info, "Window fullscreen mode: ", App.Window.isFullscreen());
            testFlags[7] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 8) && !testFlags[8]) {
            if (App.Window.setFullscreen(true)) {
                App.Log.write(logSource, LogLevel.Info, "Set window to fullscreen mode");
            } else {
                App.Log.write(logSource, LogLevel.Info, "Failed to set window to fullscreen mode");
            }
            testFlags[8] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 9) && !testFlags[9]) {
            App.Log.write(logSource, LogLevel.Info, "Fullscreen dimensions: ", App.Window.getDimensions());
            testFlags[9] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 10) && !testFlags[10]) {
            if (App.Window.setFullscreen(false)) {
                App.Log.write(logSource, LogLevel.Info, "Set window to windowed mode");
            } else {
                App.Log.write(logSource, LogLevel.Info, "Failed to set window to windowed mode");
            }
            testFlags[10] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 11) && !testFlags[11]) {
            App.Log.write(logSource, LogLevel.Info, "Monitors:");
            List<Monitor> monitors = App.Window.getMonitors();
            for (Monitor monitor : monitors) {
                App.Log.write(logSource, LogLevel.Info, "Monitor: ", monitor);
            }
            testFlags[11] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 12) && !testFlags[12]) {
            App.Log.write(logSource, LogLevel.Info, "Current monitor: ", App.Window.getMonitor());
            testFlags[12] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 13) && !testFlags[13]) {
            App.Window.setMonitor(App.Window.getMonitors().get(2));
            App.Log.write(logSource, LogLevel.Info, "Set monitor index");
            testFlags[13] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 14) && !testFlags[14]) {
            if (App.Window.setFullscreen(true)) {
                App.Log.write(logSource, LogLevel.Info, "Set window to fullscreen mode on monitor 2");
            } else {
                App.Log.write(logSource, LogLevel.Info, "Failed to set window to fullscreen mode on monitor 2");
            }
            testFlags[14] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 15) && !testFlags[15]) {
            App.Window.setMonitor(App.Window.getMonitors().get(0));
            App.Log.write(logSource, LogLevel.Info, "Set monitor index while in fullscreen mode");
            testFlags[15] = true;
        }
        if (System.currentTimeMillis() - startTime > (1500L * 16) && !testFlags[16]) {
            if (App.Window.setFullscreen(false)) {
                App.Log.write(logSource, LogLevel.Info, "Set window to windowed mode from new monitor");
            } else {
                App.Log.write(logSource, LogLevel.Info, "Failed to set window to windowed mode from new monitor");
            }
            testFlags[16] = true;
        }
        return true;
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
