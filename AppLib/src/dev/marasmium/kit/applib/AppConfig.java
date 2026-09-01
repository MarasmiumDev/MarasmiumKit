/**
 * File:        AppConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.04.23
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit's application framework
 */

package dev.marasmium.kit.applib;

import dev.marasmium.kit.applib.assets.AssetManagerConfig;
import dev.marasmium.kit.applib.audio.AudioConfig;
import dev.marasmium.kit.applib.graphics.GraphicsManagerConfig;
import dev.marasmium.kit.applib.logging.LogManagerConfig;
import dev.marasmium.kit.applib.networking.NetClientConfig;
import dev.marasmium.kit.applib.windowing.WindowManagerConfig;

/**
 * Configuration/settings structure for the MarasmiumKit's application framework
 */
public class AppConfig {

    /**
     * The configuration of the application framework's logging system
     */
    public final LogManagerConfig log = new LogManagerConfig();
    /**
     * The configuration of the application framework's windowing system
     */
    public final WindowManagerConfig window = new WindowManagerConfig();
    /**
     * The configuration of the application framework's network client
     */
    public final NetClientConfig network = new NetClientConfig();
    /**
     * The configuration of the application framework's asset management system
     */
    public final AssetManagerConfig assets = new AssetManagerConfig();
    /**
     * The configuration of the application framework's audio system
     */
    public final AudioConfig audio = new AudioConfig();
    /**
     * The configuration of the application framework's graphics system
     */
    public final GraphicsManagerConfig graphics = new GraphicsManagerConfig();
    /**
     * The initial scene to be presented by the application framework
     */
    public Scene initialScene = null;

    /**
     * Construct a MarasmiumKit application with an initial scene for the application to display
     * @param initialScene The application's initial scene
     */
    public AppConfig(Scene initialScene) {
        this.initialScene = initialScene;
    }

    /**
     * Apply the default settings to this MarasmiumKit application configuration structure
     * @return Whether the default settings were applied successfully
     */
    public boolean applyDefaults() {
        log.applyDefaults();
        if (!window.applyDefaults()) {
            return false;
        }
        network.applyDefaults();
        assets.applyDefaults();
        audio.applyDefaults();
        graphics.applyDefaults();
        return true;
    }

}
