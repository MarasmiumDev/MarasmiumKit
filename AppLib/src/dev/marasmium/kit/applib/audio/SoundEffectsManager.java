/**
 * File:        SoundEffectsManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a manager class for asynchronous sound effects in the MarasmiumKit app
 */

package dev.marasmium.kit.applib.audio;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;
import dev.marasmium.kit.assetlib.audio.AudioLoader;
import dev.marasmium.kit.assetlib.audio.AudioTrack;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.util.HashMap;

/**
 * The main class of the sound effects audio subsystem for managing multiple sound effects
 */
public class SoundEffectsManager {

    /**
     * The default volume to play sound effects at
     */
    private double defaultVolume = 0.0d;
    /**
     * The collection of audio output streams mapped to the background threads playing them
     */
    private final HashMap<Thread, SourceDataLine> playThreads = new HashMap<>();

    /**
     * Initialize the sound effects audio subsystem's memory
     * @param config The configuration for the subsystem
     * @return Whether the subsystem was initialized successfully
     */
    public boolean initialize(SoundEffectsConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "No configuration provided for sound effects audio ",
                    "subsystem");
            return false;
        }
        if (!setDefaultVolume(config.defaultVolume)) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "Failed to set initial default volume for sound effects ",
                    "audio subsystem");
            return false;
        }
        App.Log.write(LogSource.Audio, LogLevel.Info, "Initialized sound effects audio subsystem");
        return true;
    }

    /**
     * Play a sound effect by its file path at a given volume
     * @param filePath The file path (or resource URI) of the sound effect to play
     * @param volume The volume to play the sound effect at (0.0 - 1.0)
     * @return Whether the sound effect could be started
     */
    public boolean play(String filePath, double volume) {
        // Ensure given file path is populated
        if (filePath == null) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "No file path provided for sound effect");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Empty file path provided for sound effect");
            return false;
        }
        // Load the sound effect audio track and its format
        AudioTrack track = AudioLoader.GetTrack(filePath);
        if (track == null) {
            return false;
        }
        AudioFormat format = new AudioFormat(track.sampleRate(), 8 * track.sampleSize(), track.channelCount(),
                true, false);
        // Open a new audio line in the loaded format
        DataLine.Info playerInfo = new DataLine.Info(SourceDataLine.class, format);
        Mixer.Info speakerInfo = AudioSystem.getMixerInfo()[App.Audio.getSpeaker().getIndex()];
        Mixer speaker;
        try {
            speaker = AudioSystem.getMixer(speakerInfo);
        } catch (IllegalArgumentException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to access speaker to play sound effect \"",
                    filePath, "\"");
            return false;
        }
        if (!speaker.isLineSupported(playerInfo)) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Audio output not supported to play sound effect \"",
                    filePath, "\"");
            return false;
        }
        SourceDataLine player;
        try {
            player = (SourceDataLine)speaker.getLine(playerInfo);
            player.open(format);
        } catch (LineUnavailableException | IllegalArgumentException | IllegalStateException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to open audio output to play sound effect \"",
                    filePath, "\"");
            return false;
        }
        // Set the volume of the line and start playing the sound effect
        FloatControl gainControl;
        try {
            gainControl = (FloatControl)player.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (IllegalArgumentException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to access volume control for sound effect \"",
                    filePath, "\"");
            return false;
        }
        float gain = volume == 0.0d ? gainControl.getMinimum() : 20.0f * (float)Math.log10(volume);
        gain = Math.max(gainControl.getMinimum(), gain);
        gain = Math.min(gainControl.getMaximum(), gain);
        try {
            gainControl.setValue(gain);
        } catch (IllegalArgumentException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to set volume for sound effect \"", filePath,
                    "\"");
            return false;
        }
        Thread playThread = new Thread(() -> {
            player.start();
            try {
                player.write(track.data(), 0, track.getDataSize());
            } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException _) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to write audio data for sound effect \"",
                        filePath, "\"");
            }
            player.drain();
            player.close();
        });
        try {
            playThread.start();
        } catch (IllegalThreadStateException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to start playback thread for sound effect \"",
                    filePath, "\"");
        }
        playThreads.put(playThread, player);
        return true;
    }

    /**
     * Play a sound effect by its file path at the default volume
     * @param filePath The file path (or resource URI) of the sound effect to play
     * @return Whether the sound effect could be started
     */
    public boolean play(String filePath) {
        return play(filePath, defaultVolume);
    }

    /**
     * Remove any finished background threads used to play sound effects
     */
    public void update() {
        playThreads.entrySet().removeIf(entry -> !entry.getKey().isAlive());
    }

    /**
     * Stop all currently playing sound effects and remove their background threads
     * @return Whether all sound effects were successfully stopped
     */
    public boolean stop() {
        App.Log.write(LogSource.Audio, LogLevel.Info, "Stopping all sound effects");
        boolean success = true;
        for (HashMap.Entry<Thread, SourceDataLine> entry : playThreads.entrySet()) {
            entry.getValue().close();
            entry.getKey().interrupt();
            try {
                entry.getKey().join();
            } catch (InterruptedException _) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop sound effect playback thread");
                success = false;
            }
        }
        playThreads.clear();
        return success;
    }

    /**
     * Stop all currently playing sound effects and free the sound effects audio subsystem's memory
     * @return Whether the subsystem was destroyed successfully
     */
    public boolean destroy() {
        App.Log.write(LogSource.Audio, LogLevel.Info, "Destroying sound effects audio subsystem");
        boolean success = true;
        defaultVolume = 0.0d;
        if (!stop()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop all playback threads");
            success = false;
        }
        return success;
    }

    /**
     * Get the default volume to play sound effects at
     * @return The default sound effects' volume
     */
    public double getDefaultVolume() {
        return defaultVolume;
    }

    /**
     * Set the default volume to play sound effects at
     * @param defaultVolume The new default volume to play sound effects at (0.0 - 1.0)
     * @return Whether the given volume was valid
     */
    public boolean setDefaultVolume(double defaultVolume) {
        if (defaultVolume < 0.0d || defaultVolume > 1.0d) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Invalid default sound effect volume");
            return false;
        }
        this.defaultVolume = defaultVolume;
        App.Log.write(LogSource.Audio, LogLevel.Info, "Set default sound effect volume ", (int)(defaultVolume * 100.0d),
                "%");
        return true;
    }

}
