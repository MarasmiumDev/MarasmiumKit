/**
 * File:        MusicManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.16
 * Purpose:     Defines a manager class for the background music track in the MarasmiumKit app
 */

package dev.marasmium.kit.applib.audio;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.assets.AudioTrack;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;

/**
 * The main class of the music audio subsystem for managing background music tracks
 */
public class MusicManager {

    /**
     * The current volume to play music at
     */
    private double volume = 0.0d;
    /**
     * Whether a music track is currently playing
     */
    private volatile boolean playing = false;
    /**
     * The file path of the music track currently playing
     */
    private String filePath = null;
    /**
     * Data output stream to audio output devices
     */
    private SourceDataLine player = null;
    /**
     * Thread to play music in the background
     */
    private Thread playThread = null;
    /**
     * The byte offset in the current music track's data
     */
    private volatile int offset = 0;
    /**
     * Whether the current music track is paused
     */
    private boolean paused = false;

    /**
     * Initialize the music audio subsystem's memory
     * @param config The configuration for the subsystem
     * @return Whether the subsystem was initialized successfully
     */
    public boolean initialize(MusicConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "No configuration provided for music audio subsystem");
            return false;
        }
        if (!setVolume(config.volume)) {
            App.Log.write(LogSource.Audio, LogLevel.Error, "Failed to set initial music volume");
            return false;
        }
        App.Log.write(LogSource.Audio, LogLevel.Info, "Initialized music audio subsystem");
        return true;
    }

    /**
     * Stop any current music track and load and play a new one by its file path
     * @param filePath The file path (or resource URI) of the music track to play
     * @return Whether the music track could be started
     */
    public boolean play(String filePath) {
        if (filePath == null) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Cannot play music track, no file path given");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Cannot play music track, empty file path given");
            return false;
        }
        // Stop any current music track
        if (playing) {
            if (!stop()) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop current music track to play \"",
                        filePath, "\"");
                return false;
            }
        }
        playing = true;
        paused = false;
        // Load the music track and its format
        AudioTrack track = App.Assets.getAudioTrack(filePath);
        App.Log.write(LogSource.Audio, LogLevel.Info, "Loaded: ", track);
        if (track == null) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to load music track \"", filePath, "\"");
            return false;
        }
        AudioFormat format = new AudioFormat(track.getSampleRate(), 8 * track.getSampleSize(), track.getChannelCount(),
                true, false);
        // Open a new audio line in the loaded format
        DataLine.Info playerInfo = new DataLine.Info(SourceDataLine.class, format);
        int speakerIndex = App.Audio.getSpeaker().getIndex();
        Mixer.Info[] speakerInfo = AudioSystem.getMixerInfo();
        if (speakerIndex < 0 || speakerIndex >= speakerInfo.length) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Invalid speaker index for music track \"", filePath,
                    "\"");
            return false;
        }
        Mixer speaker;
        try {
            speaker = AudioSystem.getMixer(speakerInfo[speakerIndex]);
        } catch (IllegalArgumentException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to access output speaker to play music track \"",
                    filePath, "\"");
            return false;
        }
        if (!speaker.isLineSupported(playerInfo)) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Audio lines in format of music track \"", filePath,
                    "\" unsupported");
            return false;
        }
        try {
            player = (SourceDataLine)speaker.getLine(playerInfo);
            player.open(format);
        } catch (LineUnavailableException | IllegalArgumentException | IllegalStateException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to open audio line for music track \"", filePath,
                    "\"");
            player = null;
            return false;
        }
        // Set the volume of the music track and start playing
        if (!setVolume(volume)) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to set volume for music track \"", filePath, "\"");
            return false;
        }
        this.filePath = filePath;
        App.Log.write(LogSource.Audio, LogLevel.Info, "Playing music track \"", filePath, "\"");
        playThread = new Thread(() -> {
            player.start();
            byte[] data = track.getData();
            int chunkSize = track.getSampleSize() * track.getChannelCount() * 64;
            int written;
            int copyOffset;
            // Write audio data in chunks
            while (playing) {
                try {
                    written = player.write(data, offset, data.length > offset + chunkSize
                            ? chunkSize : data.length - offset);
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException _) {
                    App.Log.write(LogSource.Audio, LogLevel.Warning, "Playback failed for music track \"", filePath,
                            "\"");
                    break;
                }
                copyOffset = (offset + written) % data.length;
                offset = copyOffset;
            }
            player.flush();
            player.close();
        });
        try {
            playThread.start();
        } catch (IllegalThreadStateException _) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to start playback thread for music track \"",
                    filePath, "\"");
            return false;
        }
        return true;
    }

    /**
     * Play the current music track
     * @return Whether a current music track was present and successfully started
     */
    public boolean play() {
        if (!play(filePath)) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to resume music track");
            return false;
        }
        return true;
    }

    /**
     * Pause the currently playing music track
     * @return Whether a current music track was present and successfully paused
     */
    public boolean pause() {
        if (!playing) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to pause music track, none playing");
            return false;
        }
        // Save the current music track and stop it
        String filePath = this.filePath;
        int offset = this.offset;
        if (!stop()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop music track \"", filePath, "\" to pause");
            return false;
        }
        this.filePath = filePath;
        this.offset = offset;
        paused = true;
        App.Log.write(LogSource.Audio, LogLevel.Info, "Paused music track \"", filePath, "\"");
        return true;
    }

    /**
     * Stop the currently playing music track
     * @return Whether a current music track was present and successfully stopped
     */
    public boolean stop() {
        boolean success = true;
        playing = false;
        paused = false;
        if (filePath != null) {
            App.Log.write(LogSource.Audio, LogLevel.Info, "Stopping music track \"", filePath, "\"");
        }
        if (player != null) {
            player.flush();
            player.close();
        }
        if (playThread != null) {
            try {
                playThread.join();
            } catch (InterruptedException _) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop playback thread for music track \"",
                        filePath, "\"");
                success = false;
            }
            playThread = null;
        }
        player = null;
        filePath = null;
        offset = 0;
        return success;
    }

    /**
     * Stop any currently playing music track and free the music audio subsystem's memory
     * @return Whether the music audio subsystem was destroyed successfully
     */
    public boolean destroy() {
        boolean success = true;
        App.Log.write(LogSource.Audio, LogLevel.Info, "Destroying music audio subsystem");
        if (!stop()) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to stop current music track");
            success = false;
        }
        volume = 0.0d;
        return success;
    }

    /**
     * Get the current music volume
     * @return The current music volume
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Set the music volume
     * @param volume The new music volume (0.0 - 1.0)
     * @return Whether the music volume was valid and could be set
     */
    public boolean setVolume(double volume) {
        if (volume < 0.0d || volume > 1.0d) {
            App.Log.write(LogSource.Audio, LogLevel.Warning, "Invalid music volume");
            return false;
        }
        // Set volume of current music track
        if (player != null) {
            FloatControl gainControl;
            try {
                gainControl = (FloatControl)player.getControl(FloatControl.Type.MASTER_GAIN);
            } catch (IllegalArgumentException _) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "No volume control available for music tracks");
                return false;
            }
            float gain = volume == 0 ? gainControl.getMinimum() : 20.0f * (float)Math.log10(volume);
            gain = Math.max(gainControl.getMinimum(), gain);
            gain = Math.min(0.0f, gain);
            try {
                gainControl.setValue(gain);
            } catch (IllegalArgumentException _) {
                App.Log.write(LogSource.Audio, LogLevel.Warning, "Failed to set volume for music track");
            }
        }
        this.volume = volume;
        App.Log.write(LogSource.Audio, LogLevel.Info, "Set music volume ", (int)(volume * 100.0d), "%");
        return true;
    }

    /**
     * Test whether there is a music track currently playing
     * @return Whether music is currently playing
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Test whether there is a music track currently paused
     * @return Whether music is currently paused
     */
    public boolean isPaused() {
        return paused;
    }

}
