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
 */
public class AudioTrack {

    /**
     * The sample rate of this audio track in Hz
     */
    private int sampleRate = 0;
    /**
     * The sample size of this audio track in bytes
     */
    private int sampleSize = 0;
    /**
     * The number of channels in this audio track
     */
    private int channelCount = 0;
    /**
     * This track's audio data
     */
    private byte[] data = null;

    /**
     * Initialize this audio track with its sample rate, sample size, channel count, and audio data
     * @param sampleRate The sample rate for this audio track in Hz
     * @param sampleSize The sample size for this audio track in bytes
     * @param channelCount The number of channels in this audio track
     * @param data This track's audio data
     * @return Whether the parameters were all valid and this audio track was initialized successfully
     */
    public boolean initialize(int sampleRate, int sampleSize, int channelCount, byte[] data) {
        if (!setSampleRate(sampleRate)) {
            return false;
        }
        if (!setSampleSize(sampleSize)) {
            return false;
        }
        if (!setChannelCount(channelCount)) {
            return false;
        }
        if (!setData(data)) {
            return false;
        }
        return true;
    }

    /**
     * Free this audio track's memory
     */
    public void destroy() {
        sampleRate = 0;
        sampleSize = 0;
        channelCount = 0;
        data = null;
    }

    /**
     * Get the sample rate to play this audio track at
     * @return This audio track's sample rate in Hz
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Set the sample rate to play this audio track at
     * @param sampleRate This audio track's new sample rate in Hz
     * @return Whether the given sample rate was valid
     */
    public boolean setSampleRate(int sampleRate) {
        if (sampleRate <= 0) {
            return false;
        }
        this.sampleRate = sampleRate;
        return true;
    }

    /**
     * Get the sample size for this audio track
     * @return The sample size for this audio track in bytes
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Set the sample size for this audio track
     * @param sampleSize The new sample size for this audio track in bytes
     * @return Whether the given sample size was valid
     */
    public boolean setSampleSize(int sampleSize) {
        if (sampleSize <= 0) {
            return false;
        }
        this.sampleSize = sampleSize;
        return true;
    }

    /**
     * Get the number of samples of audio data in this audio track
     * @return The number of samples in this audio track
     */
    public int getSampleCount() {
        if (data == null) {
            return 0;
        }
        if (sampleSize <= 0) {
            return 0;
        }
        return data.length / sampleSize;
    }

    /**
     * Get the number of channels in this audio track
     * @return The number of channels in this audio track
     */
    public int getChannelCount() {
        return channelCount;
    }

    /**
     * Set the number of channels in this audio track
     * @param channelCount The new number of channels in this audio track
     * @return Whether the given channel count was valid
     */
    public boolean setChannelCount(int channelCount) {
        if (channelCount <= 0) {
            return false;
        }
        this.channelCount = channelCount;
        return true;
    }

    /**
     * Get the duration of this audio track played at its frame rate in seconds
     * @return This audio track's duration in seconds
     */
    public double getDuration() {
        if (data == null) {
            return 0;
        }
        if (sampleRate <= 0 || sampleSize <= 0 || channelCount <= 0) {
            return 0;
        }
        return (double)data.length / (double)(sampleRate * sampleSize * channelCount);
    }

    /**
     * Get this track's audio data
     * @return This track's audio data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set this track's audio data
     * @param data This track's new audio data
     * @return Whether the data was valid and of acceptable size
     */
    public boolean setData(byte[] data) {
        if (data == null) {
            return false;
        }
        if (data.length == 0) {
            return false;
        }
        if (data.length % (sampleSize * channelCount) != 0) {
            return false;
        }
        this.data = new byte[data.length];
        try {
            System.arraycopy(data, 0, this.data, 0, data.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException | NullPointerException _) {
            return false;
        }
        return true;
    }

    /**
     * Get the size of this track's audio data
     * @return The size of this track's audio data in bytes
     */
    public int getDataSize() {
        return data.length;
    }

    /**
     * Convert this audio track to a string containing its format, size, and duration
     * @return The string representation of this audio track
     */
    @Override
    public String toString() {
        if (data == null) {
            return "audio(null)";
        }
        return "audio(" + sampleRate + "Hz, " + sampleSize + "BPS, " + channelCount + " channels, " + data.length
                + "B, " + getDuration() + "s)";
    }

}
