/**
 * File:        GraphicsManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.05.31
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's graphics system
 */

package dev.marasmium.kit.applib.graphics;

import dev.marasmium.kit.applib.data.Colour;

/**
 * A configuration/settings structure for the MarasmiumKit application framework's graphics system
 */
public class GraphicsManagerConfig {

    /**
     * The target number of graphics frames and logic updates to process per second
     */
    public int targetFPS = 0;
    /**
     * The maximum number of logic updates allowed per graphics frame
     */
    public int maxUPF = 0;
    /**
     * The colour to clear the window to each frame
     */
    public Colour clearColour = null;

    /**
     * Apply the default settings to this graphics configuration structure
     */
    public void applyDefaults() {
        targetFPS = 60;
        maxUPF = 8;
        clearColour = Colour.Black;
    }

}
