/**
 * File:        AudioLoader.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.28
 * Purpose:     Loads, caches, and writes audio tracks to be played by the MarasmiumKit's application framework
 */

package dev.marasmium.kit.assetlib.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.util.HashMap;

/**
 * Utility class for reading, caching, and writing audio tracks to be played by the MarasmiumKit's application framework
 */
public class AudioLoader {

    /**
     * The audio tracks cached in memory mapped to their file names/resource URIs
     */
    private static final HashMap<String, AudioTrack> tracks = new HashMap<>();

    /**
     * Retrieve an audio track from cache or attempt to load it from disk or jar resources by its file path
     * @param filePath The file path or resource URI of the audio track to retrieve or load
     * @return The audio requested audio track or null if it was not cached and could not be loaded from disk or jar
     * resources
     */
    public static AudioTrack GetTrack(String filePath) {
        if (filePath == null) {
            return null;
        }
        if (filePath.isEmpty()) {
            return null;
        }
        if (!tracks.containsKey(filePath)) {
            if (!LoadTrackFromDisk(filePath)) {
                if (!LoadTrackFromJar(filePath)) {
                    return null;
                }
            }
        }
        return tracks.get(filePath);
    }

    /**
     * Remove an audio track from the cache by its file name or resource URI
     * @param filePath The file path or resource URI of the audio track to free
     * @return Whether the audio track was present in the cache to be removed
     */
    public static boolean FreeTrack(String filePath) {
        if (filePath == null) {
            return false;
        }
        if (filePath.isEmpty()) {
            return false;
        }
        return tracks.remove(filePath) != null;
    }

    /**
     * Remove all audio tracks from the cache
     */
    public static void FreeTracks() {
        tracks.clear();
    }

    /**
     * Write an audio track to a given file path on disk
     * @param filePath The file path to write the audio track to
     * @param track The audio track to write
     * @return Whether the file path was accessible and the audio track was written to it successfully
     */
    public static boolean WriteTrack(String filePath, AudioTrack track) {
        // Ensure the file path is accessible
        if (filePath == null) {
            return false;
        }
        if (filePath.isEmpty()) {
            return false;
        }
        if (track == null) {
            return false;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return false;
                }
            } catch (IOException _) {
                return false;
            }
        }
        if (!file.canWrite()) {
            return false;
        }
        // Write audio track
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(ByteBuffer.allocate(4).putInt(track.sampleRate()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(track.sampleSize()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(track.channelCount()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(track.getDataSize()).array());
            outputStream.write(track.data());
            outputStream.close();
        } catch (IOException | BufferOverflowException | ReadOnlyBufferException _) {
            return false;
        }
        return true;
    }

    /**
     * Attempt to load an audio track from a file path on the disk
     * @param filePath The file path to load the audio track from
     * @return Whether the file was accessible and an audio track was loaded from it successfully
     */
    private static boolean LoadTrackFromDisk(String filePath) {
        // Ensure file path is accessible
        if (filePath == null) {
            return false;
        }
        if (filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        if (!file.canRead()) {
            return false;
        }
        // Read the file into memory to parse
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException _) {
            return false;
        }
        byte[] fileData;
        try {
            fileData = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException _) {
            return false;
        }
        return ReadTrack(filePath, fileData);
    }

    /**
     * Attempt to load an audio track from a jar resource URI
     * @param URI The jar resource URI to load the audio track from
     * @return Whether the jar resource was loaded successfully
     */
    private static boolean LoadTrackFromJar(String URI) {
        // Ensure the URI is accessible
        if (URI == null) {
            return false;
        }
        if (URI.isEmpty()) {
            return false;
        }
        InputStream inputStream = AudioLoader.class.getResourceAsStream(URI);
        if (inputStream == null) {
            return false;
        }
        // Read the resource into memory to be parsed
        byte[] fileData;
        try {
            fileData = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException | OutOfMemoryError _) {
            return false;
        }
        return ReadTrack(URI, fileData);
    }

    /**
     * Parse an audio track's format and data from data loaded into memory
     * @param filePath The file path or resource URI from which the data was loaded
     * @param fileData The data to parse
     * @return Whether the data was parsed into an audio track successfully
     */
    private static boolean ReadTrack(String filePath, byte[] fileData) {
        // Ensure valid arguments
        if (filePath == null) {
            return false;
        }
        if (filePath.isEmpty()) {
            return false;
        }
        if (fileData == null) {
            return false;
        }
        // Initialize memory
        int sampleRate;
        int sampleSize;
        int channelCount;
        int dataSize;
        byte[] data;
        byte[] buffer = new byte[4];
        int offset = 0;
        // Parse file data
        if (fileData.length < 16) {
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
        // Place track in cache map
        AudioTrack track = new AudioTrack(sampleRate, sampleSize, channelCount, data);
        tracks.put(filePath, track);
        return true;
    }

    /**
     * Unused constructor for singleton utility class
     */
    private AudioLoader() {}

}
