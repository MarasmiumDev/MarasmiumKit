/**
 * File:        AppConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.23
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.logging.LogManagerConfig;

/**
 * Configuration/settings structure for the MarasmiumKit's application framework
 */
public class AppConfig {

    /**
     * The configuration of the application framework's logging system
     */
    public final LogManagerConfig log;

    /**
     * Construct an application framework configuration structure with default settings
     */
    public AppConfig() {
        log = new LogManagerConfig();
    }

}
