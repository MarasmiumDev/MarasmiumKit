/**
 * File:        MusicManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's music manager
 */

package dev.marasmium.kit.applib.audio;

/**
 * Configuration/settings structure for the music audio subsystem
 */
public class MusicManagerConfig {

    /**
     * The initial volume to play music at
     */
    public float volume = 0.0f;

    /**
     * Apply the default settings to this music configuration structure
     */
    public void applyDefaults() {
        volume = 1.0f;
    }

}
