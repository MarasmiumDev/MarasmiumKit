/**
 * File:        AssetManager.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.27
 * Purpose:     Defines a utility class for reading, caching, and writing assets to/from files
 */

package dev.marasmium.kit.applib.assets;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Vector;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
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
     * The set of animations cached in memory mapped to their file paths
     */
    private final HashMap<String, Animation> animations = new HashMap<>();
    /**
     * The set of typefaces cached in memory mapped to their file paths
     */
    private final HashMap<String, Typeface> typefaces = new HashMap<>();

    /**
     * Initialize the MarasmiumKit application framework's asset management system
     * @param config The configuration of the asset management system
     * @return Whether the configuration was valid and the asset management system was initialized successfully
     */
    public boolean initialize(AssetManagerConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Assets, LogLevel.Error, "No configuration provided");
            return false;
        }
        if (!setBasePath(config.basePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Error, "Invalid base asset path");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Initialized asset management system");
        return true;
    }

    /**
     * Attempt to load an audio track from the disk by its file path and place it in the asset management system's cache
     * @param filePath The file path to load the audio track from in the base asset path
     * @return Whether the audio track was successfully loaded from the given file path
     */
    public boolean readAudioTrack(String filePath) {
        // Ensure the file path is accessible
        if (basePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No base asset path provided");
            return false;
        }
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
        return addAudioTrack(filePath, deserializeAudioTrack(fileData));
    }

    /**
     * Convert a byte array to an audio track
     * @param fileData The data to convert
     * @return The deserialized audio track or null if deserialization failed
     */
    public AudioTrack deserializeAudioTrack(byte[] fileData) {
        int sampleRate;
        int sampleSize;
        int channelCount;
        int dataSize;
        byte[] data;
        byte[] buffer = new byte[Integer.BYTES];
        int offset = 0;
        if (fileData.length < 4 * Integer.BYTES) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize audio track, data is smaller ",
                    "than header size");
            return null;
        }
        try {
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            sampleRate = ByteBuffer.wrap(buffer).getInt();
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            sampleSize = ByteBuffer.wrap(buffer).getInt();
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            channelCount = ByteBuffer.wrap(buffer).getInt();
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            dataSize = ByteBuffer.wrap(buffer).getInt();
        } catch (IndexOutOfBoundsException | ArrayStoreException | BufferUnderflowException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize audio track, data invalid");
            return null;
        }
        if (fileData.length < offset + dataSize) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize audio track, data is smaller ",
                    "than required size");
            return null;
        }
        data = new byte[dataSize];
        try {
            System.arraycopy(fileData, offset, data, 0, dataSize);
        } catch (IndexOutOfBoundsException | ArrayStoreException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to copy audio data");
            return null;
        }
        AudioTrack audioTrack = new AudioTrack();
        if (!audioTrack.initialize(sampleRate, sampleSize, channelCount, data)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to initialize audio track, invalid parameters");
            return null;
        }
        return audioTrack;
    }

    /**
     * Write the contents of an audio track to a file on disk
     * @param audioTrack The audio track to write
     * @param filePath The destination file path to write to in the base asset path
     * @return Whether the audio track was successfully written to the given file path
     */
    public boolean writeAudioTrack(AudioTrack audioTrack, String filePath) {
        // Ensure the output file is accessible
        if (basePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Error, "No base asset path provided");
            return false;
        }
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
        byte[] fileData = serializeAudioTrack(audioTrack);
        if (fileData == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to serialize audio track ", audioTrack);
            return false;
        }
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(fileData);
            outputStream.close();
        } catch (IOException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write audio track data to file at \"",
                    basePath + filePath, "\"");
            return false;
        }
        return true;
    }

    /**
     * Convert an audio track to a byte array
     * @param audioTrack The audio track to be serialized
     * @return The serialized audio track or null if serialization failed
     */
    public byte[] serializeAudioTrack(AudioTrack audioTrack) {
        if (audioTrack == null) {
            return null;
        }
        int sampleRate = audioTrack.getSampleRate();
        int sampleSize = audioTrack.getSampleSize();
        int channelCount = audioTrack.getChannelCount();
        int dataSize = audioTrack.getDataSize();
        byte[] data = audioTrack.getData();
        if (data == null) {
            return null;
        }
        int fileDataSize = (4 * Integer.BYTES) + data.length;
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocate(fileDataSize);
            buffer.putInt(sampleRate);
            buffer.putInt(sampleSize);
            buffer.putInt(channelCount);
            buffer.putInt(dataSize);
            buffer.put(data);
        } catch (IllegalArgumentException | BufferOverflowException | ReadOnlyBufferException _) {
            return null;
        }
        return buffer.array();
    }

    /**
     * Attempt to load an animation from the disk by its file path and place it in the asset management system's cache
     * @param filePath The file path to load the animation from in the base asset path
     * @return Whether the animation was successfully loaded from the given file path
     */
    public boolean readAnimation(String filePath) {
        // Ensure the file path is accessible
        if (basePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No base asset path provided");
            return false;
        }
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to load animation");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to load animation");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Loading animation from \"", basePath + filePath, "\"");
        File file = new File(basePath + filePath);
        if (!file.canRead()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to load animation at \"", basePath + filePath,
                    "\", cannot read from file");
            return false;
        }
        // Read all file data into memory
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to load animation at \"", basePath + filePath,
                    "\", cannot open file");
            return false;
        }
        byte[] fileData;
        try {
            fileData = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to read animation data from \"",
                    basePath + filePath, "\"");
            return false;
        }
        return addAnimation(filePath, deserializeAnimation(fileData));
    }

    /**
     * Convert a byte array to an animation
     * @param fileData The data to convert
     * @return The deserialized animation or null if deserialization failed
     */
    public Animation deserializeAnimation(byte[] fileData) {
        int targetFPS;
        Vector sheetDimensions = Vector.Zero();
        Vector frameDimensions = Vector.Zero();
        int frameCount;
        Colour[] data;
        byte[] buffer = new byte[Integer.BYTES];
        int offset = 0;
        if (fileData.length < 6 * Integer.BYTES) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize animation, data is smaller than ",
                    "header size");
            return null;
        }
        try {
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            targetFPS = ByteBuffer.wrap(buffer).getInt();
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            sheetDimensions.setX(ByteBuffer.wrap(buffer).getInt());
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            sheetDimensions.setY(ByteBuffer.wrap(buffer).getInt());
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            frameDimensions.setX(ByteBuffer.wrap(buffer).getInt());
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            frameDimensions.setY(ByteBuffer.wrap(buffer).getInt());
            System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
            offset += Integer.BYTES;
            frameCount = ByteBuffer.wrap(buffer).getInt();
        } catch (IndexOutOfBoundsException | ArrayStoreException | BufferUnderflowException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize animation, data invalid");
            return null;
        }
        int dataSize = Integer.BYTES * (int)(sheetDimensions.getElementProduct() * frameDimensions.getElementProduct());
        if (fileData.length < offset + dataSize) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize animation data, file is smaller ",
                    "than required size");
            return null;
        }
        data = new Colour[dataSize / Integer.BYTES];
        for (int i = 0; i < data.length; i++) {
            try {
                System.arraycopy(fileData, offset, buffer, 0, Integer.BYTES);
                offset += Integer.BYTES;
                data[i] = Colour.Bytes(ByteBuffer.wrap(buffer).getInt());
            } catch (IndexOutOfBoundsException | ArrayStoreException | BufferUnderflowException _) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to deserialize animation pixel data, data ",
                        "invalid");
                return null;
            }
        }
        Animation animation = new Animation();
        if (!animation.initialize(targetFPS, sheetDimensions, frameDimensions, frameCount, data)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to initialize animation, invalid parameters");
            return null;
        }
        return animation;
    }

    /**
     * Write the contents of an animation to a file on disk
     * @param animation The animation to write
     * @param filePath The destination file path to write to in the base asset path
     * @return Whether the animation was successfully written to the given file path
     */
    public boolean writeAnimation(Animation animation, String filePath) {
        // Ensure output file is accessible
        if (basePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No base asset path provided");
            return false;
        }
        if (animation == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write animation, none provided");
            return false;
        }
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write animation, no file path provided");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write animation, empty file path provided");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Writing animation ", animation, " to \"", basePath + filePath,
                "\"");
        File file = new File(basePath + filePath);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to create new file for animation at \"",
                            basePath + filePath, "\"");
                    return false;
                }
            } catch (IOException _) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to create new file for animation at\"",
                        basePath + filePath, "\"");
                return false;
            }
        }
        if (!file.canWrite()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Cannot write animation to file at \"",
                    basePath + filePath, "\"");
            return false;
        }
        // Write animation contents
        byte[] fileData = serializeAnimation(animation);
        if (fileData == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to serialize animation ", animation);
            return false;
        }
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(fileData);
            outputStream.close();
        } catch (IOException | BufferOverflowException | ReadOnlyBufferException _) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to write animation data to file at \"",
                    basePath + filePath, "\"");
            return false;
        }
        return true;
    }

    /**
     * Convert an animation to a byte array
     * @param animation The animation to be serialized
     * @return The serialized animation or null if serialization failed
     */
    public byte[] serializeAnimation(Animation animation) {
        if (animation == null) {
            return null;
        }
        int targetFPS = animation.getTargetFPS();
        Vector sheetDimensions = animation.getSheetDimensions();
        Vector frameDimensions = animation.getFrameDimensions();
        int frameCount = animation.getFrameCount();
        Colour[] data = animation.getData();
        if (sheetDimensions == null || frameDimensions == null || data == null) {
            return null;
        }
        int fileDataSize = (6 * Integer.BYTES) + (data.length * Integer.BYTES);
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocate(fileDataSize);
            buffer.putInt(targetFPS);
            buffer.putInt((int)sheetDimensions.getX());
            buffer.putInt((int)sheetDimensions.getY());
            buffer.putInt((int)frameDimensions.getX());
            buffer.putInt((int)frameDimensions.getY());
            buffer.putInt(frameCount);
            for (Colour c : data) {
                buffer.putInt(c.getRGBA());
            }
        } catch (IllegalArgumentException | BufferOverflowException | ReadOnlyBufferException _) {
            return null;
        }
        return buffer.array();
    }

    /**
     * Attempt to load a typeface from the disk by its file path and place it in the asset management system's cache
     * @param filePath The file path to load the typeface from in the base asset path
     * @return Whether the typeface was successfully loaded from the given file path
     */
    public boolean readTypeface(String filePath) {
        return false;
    }

    /**
     * Convert a byte array to a typeface
     * @param fileData The data to convert
     * @return The deserialized typeface or null if deserialization failed
     */
    public Typeface deserializeTypeface(byte[] fileData) {
        return null;
    }

    /**
     * Write the contents of a typeface to a file on disk
     * @param typeface The typeface to write
     * @param filePath The destination file path to write to in the base asset path
     * @return Whether the typeface was successfully written to the given file path
     */
    public boolean writeTypeface(Typeface typeface, String filePath) {
        return false;
    }

    /**
     * Convert a typeface to a byte array
     * @param typeface The typeface to convert
     * @return The serialized typeface or null if serialization failed
     */
    public byte[] serializeTypeface(Typeface typeface) {
        return null;
    }

    /**
     * Dispose of all assets and free the asset management system's memory
     */
    public boolean destroy() {
        boolean success = true;
        App.Log.write(LogSource.Assets, LogLevel.Info, "Destroying asset management system");
        basePath = null;
        // Free audio tracks
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing ", audioTracks.size(), " audio tracks");
        for (HashMap.Entry<String, AudioTrack> entry : audioTracks.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    entry.getValue().destroy();
                }
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to retrieve value to destroy in audio ",
                        "tracks");
                success = false;
            }
        }
        audioTracks.clear();
        // Free animations
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing ", animations.size(), " animations");
        for (HashMap.Entry<String, Animation> entry : animations.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    entry.getValue().destroy();
                }
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to retrieve value to destroy in animations");
                success = false;
            }
        }
        audioTracks.clear();
        // Free typefaces
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing ", typefaces.size(), " typefaces");
        for (HashMap.Entry<String, Typeface> entry : typefaces.entrySet()) {
            try {
                if (entry.getValue() != null) {
                    entry.getValue().destroy();
                }
            } catch (IllegalStateException _) {
                App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to retrieve value to destroy in typefaces");
                success = false;
            }
        }
        typefaces.clear();
        return success;
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
        if (basePath == null) {
            return null;
        }
        if (filePath == null) {
            return null;
        }
        if (filePath.isEmpty()) {
            return null;
        }
        // Load if not in memory
        if (!audioTracks.containsKey(filePath)) {
            if (!readAudioTrack(filePath)) {
                return null;
            }
        }
        return audioTracks.get(filePath);
    }

    /**
     * Add an audio track to the asset management system's cache
     * @param filePath The file path of the audio track to add in the base asset path
     * @param audioTrack The audio track to add
     * @return Whether the audio track was not in memory and was added successfully
     */
    public boolean addAudioTrack(String filePath, AudioTrack audioTrack) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to add audio track");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to add audio track");
            return false;
        }
        if (audioTrack == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No audio track provided to add");
            return false;
        }
        if (audioTracks.containsKey(filePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to add audio track, already cached");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Adding audio track at \"", basePath + filePath, "\"");
        audioTracks.put(filePath, audioTrack);
        return true;
    }

    /**
     * Remove an audio track from the asset management system's cache
     * @param filePath The file path of the audio track to free in the base asset path
     * @return Whether the audio track was in memory and was removed successfully
     */
    public boolean removeAudioTrack(String filePath) {
        if (basePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No base asset path provided");
            return false;
        }
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to free audio track");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to free audio track");
            return false;
        }
        if (!audioTracks.containsKey(filePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "File path provided to free audio track not loaded");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing audio track at \"", basePath + filePath, "\"");
        if (audioTracks.get(filePath) != null) {
            audioTracks.get(filePath).destroy();
        }
        return audioTracks.remove(filePath) != null;
    }

    /**
     * Retrieve a cached animation from memory or attempt to load it from disk by its file path
     * @param filePath The file path of the animation to retrieve in the base asset path
     * @return The requested animation from memory or disk or null if the animation could not be loaded
     */
    public Animation getAnimation(String filePath) {
        if (basePath == null) {
            return null;
        }
        if (filePath == null) {
            return null;
        }
        if (filePath.isEmpty()) {
            return null;
        }
        // Load if not in memory
        if (!animations.containsKey(filePath)) {
            if (!readAnimation(filePath)) {
                return null;
            }
        }
        return animations.get(filePath);
    }

    /**
     * Add an animation to the asset management system's cache
     * @param filePath The file path of the animation to add in the base asset path
     * @param animation The animation to add
     * @return Whether the animation was not in memory and was added successfully
     */
    public boolean addAnimation(String filePath, Animation animation) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to add animation");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to add animation");
            return false;
        }
        if (animation == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No animation provided to add");
            return false;
        }
        if (animations.containsKey(filePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to add animation, already cached");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Adding animation at \"", basePath + filePath, "\"");
        animations.put(filePath, animation);
        return true;
    }

    /**
     * Remove an animation from the asset management system's cache
     * @param filePath The file path of the animation to free in the base asset path
     * @return Whether the animation was in memory and was removed successfully
     */
    public boolean removeAnimation(String filePath) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to free animation");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to free animation");
            return false;
        }
        if (!animations.containsKey(filePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "File path provided to free animation not loaded");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Freeing animation at \"", basePath + filePath, "\"");
        if (animations.get(filePath) != null) {
            animations.get(filePath).destroy();
        }
        return animations.remove(filePath) != null;
    }

    /**
     * Retrieve a cached typeface from memory or attempt to load it from disk by its file path
     * @param filePath The file path of the typeface to retrieve in the base asset path
     * @return The requested typeface from memory or disk or null if the typeface could not be loaded
     */
    public Typeface getTypeface(String filePath) {
        if (basePath == null) {
            return null;
        }
        if (filePath == null) {
            return null;
        }
        if (filePath.isEmpty()) {
            return null;
        }
        // Load if not in memory
        if (!typefaces.containsKey(filePath)) {
            if (!readTypeface(filePath)) {
                return null;
            }
        }
        return typefaces.get(filePath);
    }

    /**
     * Add a typeface to the asset management system's cache
     * @param filePath The file path of the typeface to add in the base asset path
     * @param typeface The typeface to add
     * @return Whether the typeface was not in memory and was added successfully
     */
    public boolean addTypeface(String filePath, Typeface typeface) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to add typeface");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to add typeface");
            return false;
        }
        if (typeface == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No typeface provided to add");
            return false;
        }
        if (typefaces.containsKey(filePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Failed to add typeface, already cached");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Info, "Adding typeface at \"", basePath + filePath, "\"");
        typefaces.put(filePath, typeface);
        return true;
    }

    /**
     * Remove a typeface from the asset management system's cache
     * @param filePath The file path of the typeface to remove in the base asset path
     * @return Whether the typeface was in memory and was removed successfully
     */
    public boolean removeTypeface(String filePath) {
        if (filePath == null) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "No file path provided to free typeface");
            return false;
        }
        if (filePath.isEmpty()) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "Empty file path provided to free typeface");
            return false;
        }
        if (!typefaces.containsKey(filePath)) {
            App.Log.write(LogSource.Assets, LogLevel.Warning, "File path provided to free typeface not loaded");
            return false;
        }
        App.Log.write(LogSource.Assets, LogLevel.Warning, "Freeing typeface at \"", basePath + filePath, "\"");
        if (typefaces.get(filePath) != null) {
            typefaces.get(filePath).destroy();
        }
        return typefaces.remove(filePath) != null;
    }

}
