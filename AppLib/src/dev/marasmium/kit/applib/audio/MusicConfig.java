/**
 * File:        MusicConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's music manager
 */

package dev.marasmium.kit.applib.audio;

/**
 * Configuration/settings structure for the music audio subsystem
 */
public class MusicConfig {

    /**
     * The initial volume to play music at
     */
    public double volume;

    /**
     * Construct a music audio subsystem configuration structure with default settings
     */
    public MusicConfig() {
        volume = 1.0d;
    }

}
