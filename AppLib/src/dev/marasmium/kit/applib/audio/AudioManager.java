/**
 * File:        AudioManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines the main class of the MarasmiumKit application framework's audio system
 */

package dev.marasmium.kit.applib.audio;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

public class AudioManager {

    public final SoundEffectsManager soundEffects = new SoundEffectsManager();
    public final MusicManager music = new MusicManager();

    public boolean initialize(AudioConfig config) {
        // Initialize the sound effect manager subsystem
        if (!soundEffects.initialize(config.soundEffects)) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "Failed to initialize sound effect manager");
            return false;
        }
        // Initialize the music manager subsystem
        if (!music.initialize(config.music)) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "Failed to initialize music manager");
            return false;
        }
        App.Log.write(LogSource.Audio, LogLevel.Info, "Initialize audio system");
        return true;
    }

    public boolean destroy() {
        App.Log.write(LogSource.Audio, LogLevel.Info, "Destroying audio system");
        boolean success = true;
        // Free the sound effect manager subsystem
        if (!soundEffects.destroy()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to destroy sound effect manager");
            success = false;
        }
        // Free the music manager subsystem
        if (!music.destroy()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to destroy music manager");
            success = false;
        }
        return success;
    }

}
