/**
 * File:        WindowManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.10
 * Purpose:     Defines a configuration/settings structure for the application framework's windowing system
 */

package dev.marasmium.kit.applib.windowing;

import dev.marasmium.kit.applib.data.Vector;

/**
 * Configuration/settings structure for the windowing system
 */
public class WindowManagerConfig {

    /**
     * The initial title to appear on the window when in windowed mode
     */
    public String title;
    /**
     * The initial dimensions for the window in pixels when in windowed mode
     */
    public Vector dimensions;
    /**
     * Whether the window should initially appear in windowed mode
     */
    public boolean fullscreen;
    /**
     * The initial monitor for the window to appear on when in fullscreen mode
     */
    public Monitor monitor;

    /**
     * Construct a windowing system configuration structure with default settings
     */
    public WindowManagerConfig() {
        title = "MarasmiumKit App";
        dimensions = Vector.Cartesian(1280.0d, 720.0d);
        fullscreen = false;
        monitor = new Monitor();
    }

}
