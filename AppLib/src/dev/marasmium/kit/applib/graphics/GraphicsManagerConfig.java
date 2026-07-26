/**
 * File:        GraphicsManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's graphics system
 */

package dev.marasmium.kit.applib.graphics;

/**
 * A configuration/settings structure for the MarasmiumKit application framework's graphics system
 */
public class GraphicsManagerConfig {

    /**
     * The target number of graphics frames and logic updates to process per second
     */
    public int targetFPS;
    /**
     * The maximum number of logic updates allowed per graphics frame
     */
    public int maxUPF;

    /**
     * Create a graphics system configuration with default settings
     */
    public GraphicsManagerConfig() {
        targetFPS = 60;
        maxUPF = 8;
    }

}
