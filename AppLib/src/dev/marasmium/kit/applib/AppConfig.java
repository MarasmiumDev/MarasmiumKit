/**
 * File:        AppConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.23
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.logging.LogManagerConfig;
import dev.marasmium.kit.applib.windowing.WindowManagerConfig;

/**
 * Configuration/settings structure for the MarasmiumKit's application framework
 */
public class AppConfig {

    /**
     * The configuration of the application framework's logging system
     */
    public final LogManagerConfig log;
    /**
     * The configuration of the application framework's windowing system
     */
    public final WindowManagerConfig window;
    /**
     * The initial scene to be presented by the application framework
     */
    public Scene initialScene;

    /**
     * Construct an application framework configuration structure with default settings
     */
    public AppConfig(Scene initialScene) {
        log = new LogManagerConfig();
        window = new WindowManagerConfig();
        this.initialScene = initialScene;
    }

}
