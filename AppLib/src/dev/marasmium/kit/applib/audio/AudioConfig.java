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
    public final AudioDevice speaker;
    /**
     * The configuration of the sound effects audio subsystem
     */
    public final SoundEffectsManagerConfig soundEffects;
    /**
     * The configuration of the music audio subsystem
     */
    public final MusicManagerConfig music;

    /**
     * Construct an audio system configuration structure with default settings
     */
    public AudioConfig() {
        speaker = new AudioDevice();
        speaker.initialize(0);
        soundEffects = new SoundEffectsManagerConfig();
        music = new MusicManagerConfig();
    }

}
