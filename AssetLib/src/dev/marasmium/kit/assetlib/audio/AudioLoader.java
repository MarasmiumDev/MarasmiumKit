/**
 * File:        AudioLoader.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.28
 * Purpose:     Loads, caches, and writes audio tracks to be played by the MarasmiumKit's application framework
 */

package dev.marasmium.kit.assetlib.audio;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;

public class AudioLoader {

    private static final HashMap<String, AudioTrack> tracks = new HashMap<>();

    public static AudioTrack GetTrack(String filePath) {
        if (!tracks.containsKey(filePath)) {
            if (!LoadTrackFromDisk(filePath)) {
                if (!LoadTrackFromJar(filePath)) {
                    return null;
                }
            }
        }
        return tracks.get(filePath);
    }

    public static boolean FreeTrack(String filePath) {
        return tracks.remove(filePath) != null;
    }

    public static boolean WriteTrack(String filePath, AudioTrack track) {
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
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(ByteBuffer.allocate(4).putInt(track.getFrameRate()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(track.getFrameSize()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(track.getChannelCount()).array());
            outputStream.write(ByteBuffer.allocate(4).putInt(track.getDataSize()).array());
            outputStream.write(track.getData());
            outputStream.close();
        } catch (IOException _) {
            return false;
        }
        return true;
    }

    private static boolean LoadTrackFromDisk(String filePath) {
        if (filePath == null) {
            return false;
        }
        File file = new File(filePath);
        if (!file.canRead()) {
            return false;
        }
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

    private static boolean LoadTrackFromJar(String filePath) {
        if (filePath == null) {
            return false;
        }
        InputStream inputStream = AudioLoader.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            return false;
        }
        byte[] fileData;
        try {
            fileData = inputStream.readAllBytes();
            inputStream.close();
        } catch (IOException | OutOfMemoryError _) {
            return false;
        }
        return ReadTrack(filePath, fileData);
    }

    private static boolean ReadTrack(String filePath, byte[] fileData) {
        // Initialize memory
        int frameRate = 0;
        int frameSize = 0;
        int channelCount = 0;
        int dataSize = 0;
        byte[] data;
        byte[] buffer = new byte[4];
        int offset = 0;
        // Parse file data
        if (fileData.length < 20) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            buffer[i] = fileData[offset + i];
        }
        offset += 4;
        frameRate = ByteBuffer.wrap(buffer).getInt();
        for (int i = 0; i < 4; i++) {
            buffer[i] = fileData[offset + i];
        }
        offset += 4;
        frameSize = ByteBuffer.wrap(buffer).getInt();
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
        AudioTrack track = new AudioTrack(frameRate, frameSize, channelCount, data);
        tracks.put(filePath, track);
        return true;
    }

}
