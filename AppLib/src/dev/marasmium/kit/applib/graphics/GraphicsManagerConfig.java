/**
 * File:        GraphicsManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's graphics system
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;

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
     * The dimensions of the graphics frames to draw scaled to window dimensions in pixels
     */
    public Vector frameDimensions;
    /**
     * The background colour to clear graphics frames to
     */
    public Colour backgroundColour;

    /**
     * Create a graphics system configuration with default settings
     */
    public GraphicsManagerConfig() {
        targetFPS = 60;
        maxUPF = 8;
        frameDimensions = Vector.Cartesian(1280.0d, 720.0d);
        backgroundColour = Colour.Black;
    }

}
