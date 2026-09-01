/**
 * File:        AudioConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's audio system
 */

package dev.marasmium.kit.applib.audio;

/**
 * Configuration/settings structure for the MarasmiumKit application framework's audio system
 */
public class AudioConfig {

    /**
     * The initial audio output device to use
     */
    public final AudioDevice speaker = new AudioDevice();
    /**
     * The configuration of the sound effects audio subsystem
     */
    public final SoundEffectsConfig soundEffects = new SoundEffectsConfig();
    /**
     * The configuration of the music audio subsystem
     */
    public final MusicConfig music = new MusicConfig();

    /**
     * Apply the default settings to this audio configuration structure
     * @return Whether the default settings were applied successfully
     */
    public boolean applyDefaults() {
        if (!speaker.initialize(0)) {
            return false;
        }
        soundEffects.applyDefaults();
        music.applyDefaults();
        return true;
    }

}
