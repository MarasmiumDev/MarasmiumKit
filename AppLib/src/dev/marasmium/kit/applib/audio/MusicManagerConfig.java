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
    public float volume;

    /**
     * Construct a music manager configuration structure with default settings
     */
    public MusicManagerConfig() {
        volume = 1.0f;
    }

}
