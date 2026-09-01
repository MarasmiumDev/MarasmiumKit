/**
 * File:        AssetManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.27
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's asset management
 *              system
 */

package dev.marasmium.kit.applib.assets;

/**
 * Configuration/settings structure for the MarasmiumKit application framework's asset management system
 */
public class AssetManagerConfig {

    /**
     * The base path (directory) containing all audio and animation assets for the application framework
     */
    public String basePath = null;

    /**
     * Apply the default settings to this asset management configuration structure
     */
    public void applyDefaults() {
        basePath = "./Assets/";
    }

}
