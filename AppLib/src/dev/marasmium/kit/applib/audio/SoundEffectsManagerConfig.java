/**
 * File:        SoundEffectsManagerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's sound effects
 *              manager
 */

package dev.marasmium.kit.applib.audio;

/**
 * Configuration/settings structure for the sound effects audio subsystem
 */
public class SoundEffectsManagerConfig {

    /**
     * The default volume to play sound effects at
     */
    public float defaultVolume;

    /**
     * Construct a sound effects manager configuration structure with default settings
     */
    public SoundEffectsManagerConfig() {
        defaultVolume = 1.0f;
    }

}
