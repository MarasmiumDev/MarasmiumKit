/**
 * File:        AudioTrack.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.28
 * Purpose:     Defines a data structure representing an audio track to be played by the MarasmiumKit application
 *              framework
 */

package dev.marasmium.kit.applib.assets;

/**
 * Data structure representing an audio track to be played by the MarasmiumKit application framework
 * @param sampleRate The sample rate to play the audio data at
 * @param sampleSize The size of each sample of audio data in bytes
 * @param channelCount The number of channels in each frame of audio data
 * @param data The audio data
 */
public record AudioTrack(int sampleRate, int sampleSize, int channelCount, byte[] data) {

    /**
     * Get the number of samples of audio data in this audio track
     * @return The number of samples in this audio track
     */
    public int getSampleCount() {
        return data.length / sampleSize;
    }

    /**
     * Get the size of this audio track's audio data in bytes
     * @return The size of this audio track's data in bytes
     */
    public int getDataSize() {
        return data.length;
    }

    /**
     * Get the duration of this audio track played at its frame rate in seconds
     * @return This audio track's duration in seconds
     */
    public double getDuration() {
        return (double) data.length / (double) (sampleRate * sampleSize * channelCount);
    }

    /**
     * Convert this audio track to a string containing its format, size, and duration
     * @return The string representation of this audio track
     */
    @Override
    public String toString() {
        return "audio(" + sampleRate + "Hz, " + sampleSize + "BPS, " + channelCount + " channels, " + data.length
                + "B, " + getDuration() + "s)";
    }

    /**
     * Get the sample rate to play this audio track at
     * @return This audio track's sample rate
     */
    @Override
    public int sampleRate() {
        return sampleRate;
    }

    /**
     * Get the size of each sample of audio data in bytes
     * @return The size of each sample
     */
    @Override
    public int sampleSize() {
        return sampleSize;
    }

    /**
     * Get the number of channels in each frame of audio data in this audio track
     * @return The number of channels in this audio track
     */
    @Override
    public int channelCount() {
        return channelCount;
    }

    /**
     * Get a copy of this audio track's data
     * @return This audio track's data
     */
    @Override
    public byte[] data() {
        return data.clone();
    }

}
