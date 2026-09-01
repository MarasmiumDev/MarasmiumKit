/**
 * File:        SoundEffectsConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's sound effects
 *              manager
 */

package dev.marasmium.kit.applib.audio;

/**
 * Configuration/settings structure for the sound effects audio subsystem
 */
public class SoundEffectsConfig {

    /**
     * The default volume to play sound effects at
     */
    public double defaultVolume = 0.0d;

    /**
     * Apply the default settings to this sound effects configuration structure
     */
    public void applyDefaults() {
        defaultVolume = 1.0d;
    }

}
