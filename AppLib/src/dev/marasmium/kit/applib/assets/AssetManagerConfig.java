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
    public String basePath;

    /**
     * Construct an asset management system configuration structure with default settings
     */
    public AssetManagerConfig() {
        basePath = "./Assets/";
    }

}
