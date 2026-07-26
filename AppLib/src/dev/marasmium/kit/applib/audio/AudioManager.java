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
import dev.marasmium.kit.assetlib.audio.AudioLoader;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * The main class of the MarasmiumKit application framework's audio system
 */
public class AudioManager {

    /**
     * The index of the audio output device to play audio on
     */
    private int speakerIndex = 0;
    /**
     * The sound effects audio subsystem
     */
    public final SoundEffectsManager soundEffects = new SoundEffectsManager();
    /**
     * The music audio subsystem
     */
    public final MusicManager music = new MusicManager();

    /**
     * Initialize the sound effects and music audio subsystems
     * @param config The configuration for the audio system
     * @return Whether the audio system was initialized successfully
     */
    public boolean initialize(AudioConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "No configuration provided for audio system");
            return false;
        }
        setSpeaker(config.speaker);
        // Initialize the sound effect manager subsystem
        if (!soundEffects.initialize(config.soundEffects)) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "Failed to initialize sound effects audio subsystem");
            return false;
        }
        // Initialize the music manager subsystem
        if (!music.initialize(config.music)) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "Failed to initialize music audio subsystem");
            return false;
        }
        App.Log.write(LogSource.Audio, LogLevel.Info, "Initialize audio system");
        return true;
    }

    /**
     * Update the audio subsystems' logic
     */
    public void update() {
        soundEffects.update();
    }

    /**
     * Destroy the sound effects and music audio subsystems and free the audio system's memory
     * @return Whether the audio system was destroyed successfully
     */
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
        // Free all cached audio tracks
        AudioLoader.FreeTracks();
        return success;
    }

    /**
     * Get the set of available speakers in the local audio environment
     * @return The set of available speakers
     */
    public ArrayList<AudioDevice> getSpeakers() {
        // Get mixers in local audio environment
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        ArrayList<AudioDevice> speakers = new ArrayList<>();
        for (int index = 0; index < mixers.length; index++) {
            // Ensure mixer is available for audio output
            DataLine.Info output = new DataLine.Info(SourceDataLine.class, null);
            Mixer mixer;
            try {
                mixer = AudioSystem.getMixer(mixers[index]);
            } catch (IllegalArgumentException _) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to access speaker ", index);
                continue;
            }
            if (!mixer.isLineSupported(output)) {
                continue;
            }
            // Retrieve speaker information
            AudioDevice speaker = new AudioDevice();
            if (!speaker.setIndex(index)) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to access speaker ", index);
                continue;
            }
            speakers.add(speaker);
        }
        return speakers;
    }

    /**
     * Get the current speaker used by the audio system
     * @return The audio system's current speaker
     */
    public AudioDevice getSpeaker() {
        // Acquire list of available speakers
        ArrayList<AudioDevice> speakers = getSpeakers();
        if (speakers.isEmpty()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "No speakers available for audio output");
            return null;
        }
        // Locate current speaker in list
        for (AudioDevice speaker : speakers) {
            if (speaker.getIndex() == speakerIndex) {
                return speaker;
            }
        }
        AudioDevice first;
        try {
            first = speakers.getFirst();
        } catch (NoSuchElementException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "No speakers available for audio output");
            return null;
        }
        return first;
    }

    /**
     * Set the speaker to be used by the audio system
     * @param speaker The new speaker to be used by the audio system
     * @return Whether the speaker was set successfully
     */
    public boolean setSpeaker(AudioDevice speaker) {
        // Ensure speaker availability
        if (speaker == null) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "No speaker provided");
            return false;
        }
        App.Log.write(LogSource.Audio, LogLevel.Info, "Setting audio output speaker ", speaker);
        if (!getSpeakers().contains(speaker)) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Speaker not available for audio output");
            return false;
        }
        boolean success = true;
        // Update speaker and restart sound effects and music
        this.speakerIndex = speaker.getIndex();
        if (!soundEffects.stop()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop sound effects when switching speakers");
            success = false;
        }
        if (music.isPlaying()) {
            if (!music.pause()) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to pause music track when switching speakers");
                success = false;
            }
            if (!music.play()) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to resume music track when switching ",
                        "speakers");
                success = false;
            }
        }
        App.Log.write(LogSource.Audio, LogLevel.Info, "Set audio output speaker ", speaker);
        return success;
    }

}
