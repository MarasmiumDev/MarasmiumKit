/**
 * File:        AudioConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's audio system
 */

package dev.marasmium.kit.applib.audio;

public class AudioConfig {

    public final SoundEffectsConfig soundEffects;
    public final MusicConfig music;
    public double volume;

    public AudioConfig() {
        soundEffects = new SoundEffectsConfig();
        music = new MusicConfig();
        volume = 1.0d;
    }

}
