/**
 * File:        AssetManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.27
 * Purpose:     Defines a utility class for reading, caching, and writing assets to/from files
 */

package dev.marasmium.kit.applib.assets;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.HashMap;

/**
 * The main class of the MarasmiumKit application framework's asset management system
 */
public class AssetManager {

    /**
     * The base path (directory) containing all assets for the application framework
     */
    private String basePath = null;
    /**
     * The set of audio tracks cached in memory mapped to their file paths
     */
    private final HashMap<String, AudioTrack> audioTracks = new HashMap<>();

    /**
     * Initialize the MarasmiumKit application framework's asset management system
     * @param config The configuration of the asset management system
     * @return Whether the configuration was valid and the asset management system was initialized successfully
     */
    public boolean initialize(AssetManagerConfig config) {
        if (!setBasePath(config.basePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Error, "Invalid base asset path");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Initialized asset management system");
        return true;
    }

    /**
     * Write the contents of an audio track to a file on disk
     * @param audioTrack The audio track to write
     * @param filePath The destination file path to write to in the base asset path
     * @return Whether the audio track was successfully written to the given file path
     */
    public boolean writeAudioTrack(AudioTrack audioTrack, String filePath) {
        // Ensure the output file is accessible
        if (audioTrack == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write audio track, none provided");
            return false;
        }
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write audio track, no file path provided");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write audio track, empty file path provided");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Writing audio track ", audioTrack, " to \"",
                basePath + filePath, "\"");
        File file = new File(basePath + filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to create new file for audio track at \"",
                            basePath + filePath, "\"");
                    return false;
                }
            } catch (IOException _) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to create new file for audio track at \"",
                        basePath + filePath, "\"");
                return false;
            }
        }
        if (!file.canWrite()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Cannot write audio track to file at \"",
                    basePath + filePath, "\"");
            return false;
        }
        // Write audio track contents
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(ByteBuffer.allocate(4).putInt(audioTrack.sampleRate()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(audioTrack.sampleSize()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(audioTrack.channelCount()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(audioTrack.getDataSize()).array());
            outputStream.write(audioTrack.data());
            outputStream.close();
        } catch (IOException | BufferOverflowException | ReadOnlyBufferException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write audio track data to file at \"",
                    basePath + filePath, "\"");
            return false;
        }
        return true;
    }

    /**
     * Dispose of all assets and free the asset management system's memory
     */
    public void destroy() {
        App.Log.write(LogSource.Assets, LogLevel.Info, "Destroying asset management system");
        basePath = null;
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing ", audioTracks.size(), " audio tracks");
        audioTracks.clear();
    }

    /**
     * Get the base path (directory) containing all assets for the application framework
     * @return The application framework's base asset path
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * Set the base path (directory) containing all assets for the application framework
     * @param basePath The application framework's new base asset path
     * @return Whether the new base path was valid
     */
    public boolean setBasePath(String basePath) {
        // Ensure the path is valid
        if (basePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No base asset path provided");
            return false;
        }
        if (basePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty base asset path provided");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Setting base asset path \"", basePath, "\"");
        File base = new File(basePath);
        if (!base.exists()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to set base asset path, new path does not exist");
            return false;
        }
        if (!base.isDirectory()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to set base asset path, new path is not a ",
                    "directory");
            return false;
        }
        this.basePath = basePath;
        return true;
    }

    /**
     * Retrieve a cached audio track from memory or attempt to load it from disk by its file path
     * @param filePath The file path of the audio track to retrieve in the base asset path
     * @return The requested audio track either from memory or disk or null if the audio track could not be loaded
     */
    public AudioTrack getAudioTrack(String filePath) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to retrieve audio track");
            return null;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to retrieve audio track");
            return null;
        }
        // Load if not in memory
        if (!audioTracks.containsKey(filePath)) {
            if (!loadAudioTrack(filePath)) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to load audio track from \"",
                        basePath + filePath, "\"");
                return null;
            }
        }
        return audioTracks.get(filePath);
    }

    /**
     * Remove an audio track from the asset management system's cache
     * @param filePath The file path of the audio track to free in the base asset path
     * @return Whether the audio track was in memory and was removed successfully
     */
    public boolean freeAudioTrack(String filePath) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to free audio track");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to free audio track");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing audio track at \"", basePath + filePath, "\"");
        return audioTracks.remove(filePath) != null;
    }

    /**
     * Attempt to load an audio track from the disk by its file path and place it in the asset management system's cache
     * @param filePath The file path to load the audio track from in the base asset path
     * @return Whether the audio track was successfully loaded from the given file path
     */
    private boolean loadAudioTrack(String filePath) {
        // Ensure the file path is accessible
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to load audio track");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to load audio track");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Loading audio track at \"", basePath + filePath, "\"");
        File file = new File(basePath + filePath);
        if (!file.canRead()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to load audio track at \"", basePath + filePath,
                    "\", cannot read from file");
            return false;
        }
        // Read all file data into memory
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to load audio track at \"", basePath + filePath,
                    "\", cannot open file");
            return false;
        }
        byte[] fileData;
        try {
            fileData = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to read audio data from \"", basePath + filePath,
                    "\"");
            return false;
        }
        // Parse file data
        int sampleRate;
        int sampleSize;
        int channelCount;
        int dataSize;
        byte[] data;
        byte[] buffer = new byte[4];
        int offset = 0;
        if (fileData.length < 16) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to parse audio data from \"", basePath + filePath,
                    "\", file is smaller than header size");
            return false;
        }
        for (int i = 0; i < 4; i++) {
            buffer[i] = fileData[offset + i];
        }
        offset += 4;
        sampleRate = ByteBuffer.wrap(buffer).getInt();
        for (int i = 0; i < 4; i++) {
            buffer[i] = fileData[offset + i];
        }
        offset += 4;
        sampleSize = ByteBuffer.wrap(buffer).getInt();
        for (int i = 0; i < 4; i++) {
            buffer[i] = fileData[offset + i];
        }
        offset += 4;
        channelCount = ByteBuffer.wrap(buffer).getInt();
        for (int i = 0; i < 4; i++) {
            buffer[i] = fileData[offset + i];
        }
        offset += 4;
        dataSize = ByteBuffer.wrap(buffer).getInt();
        if (fileData.length < offset + dataSize) {
            return false;
        }
        data = new byte[dataSize];
        for (int i = 0; i < dataSize; i++) {
            data[i] = fileData[offset + i];
        }
        // Construct audio track with parsed data and place in cache
        AudioTrack audioTrack = new AudioTrack(sampleRate, sampleSize, channelCount, data);
        App.Log.write(LogSource.Assets, LogLevel.Info, "Loaded audio track ", audioTrack, " from \"",
                basePath + filePath, "\"");
        audioTracks.put(filePath, audioTrack);
        return true;
    }

}
