/**
 * File:        AudioTrack.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.28
 * Purpose:     Defines a data structure representing an audio track to be played by the MarasmiumKit application
 *              framework
 */

package dev.marasmium.kit.assetlib.audio;

public class AudioTrack {

    private int frameRate;
    private int frameSize;
    private int channelCount;
    private byte[] data;

    public AudioTrack(int frameRate, int frameSize, int channelCount, byte[] data) {
        this.frameRate = frameRate;
        this.frameSize = frameSize;
        this.channelCount = channelCount;
        this.data = data.clone();
    }

    public int getFrameRate() {
        return frameRate;
    }

    public int getFrameCount() {
        return data.length / frameSize;
    }

    public int getFrameSize() {
        return frameSize;
    }

    public int getSampleRate() {
        return frameRate * channelCount;
    }

    public int getSampleCount() {
        return data.length * channelCount / frameSize;
    }

    public int getSampleSize() {
        return frameSize / channelCount;
    }

    public int getChannelCount() {
        return channelCount;
    }

    public int getDataSize() {
        return data.length;
    }

    public byte[] getData() {
        return data.clone();
    }

    public double getDuration() {
        return (double)data.length / (double)(frameRate * frameSize * channelCount);
    }

    @Override
    public String toString() {
        return "audio(" + frameRate + "Hz, " + frameSize + "BPF, " + channelCount + " channels, " + data.length + "B, "
                + getDuration() + "s)";
    }

}
