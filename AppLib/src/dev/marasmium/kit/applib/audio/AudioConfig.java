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
    public final SoundEffectsManagerConfig soundEffects = new SoundEffectsManagerConfig();
    /**
     * The configuration of the music audio subsystem
     */
    public final MusicManagerConfig music = new MusicManagerConfig();

    /**
     * Construct an audio system configuration structure with default settings
     */
    public AudioConfig() {
        speaker.initialize(0);
    }

}
