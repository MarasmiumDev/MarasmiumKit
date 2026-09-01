/**
 * File:        AudioDevice.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.07.22
 * Purpose:     Defines a structure representing an audio device
 */

package dev.marasmium.kit.applib.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

/**
 * Structure representing an audio device (input or output)
 */
public class AudioDevice {

    /**
     * The index of this audio device in the local audio environment's array of available devices
     */
    private int index = 0;
    /**
     * The system-reported name of this audio device
     */
    private String name = null;

    /**
     * Initialize this audio device with an index and validate it
     * @param index The index of this audio device in the local audio environment array of available devices
     * @return Whether the given index could be validated
     */
    public boolean initialize(int index) {
        return setIndex(index);
    }

    /**
     * Free this audio device's memory
     */
    public void destroy() {
        index = 0;
        name = null;
    }

    /**
     * Ensure that this audio device's index is still available in the local audio environment's array of available
     * devices, and change to the default (0) if unavailable
     * @return Whether the audio device was available
     */
    private boolean validate() {
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();
        // Ensure that there are audio devices available
        if (mixers.length == 0) {
            index = -1;
            name = "";
            return false;
        }
        // Check the index and set the audio device name
        boolean success = true;
        if (index < 0 || index >= mixers.length) {
            index = 0;
            success = false;
        }
        name = mixers[index].getName();
        return success;
    }

    /**
     * Get the index of this audio device in the local audio environment's array of available devices
     * @return This audio device's index
     */
    public int getIndex() {
        validate();
        return index;
    }

    /**
     * Set the index of this audio device in the local audio environment's array of available devices
     * @param index The audio device's new index
     * @return Whether the given index was valid
     */
    public boolean setIndex(int index) {
        this.index = index;
        return validate();
    }

    /**
     * Get the system-reported name of this audio device
     * @return This audio device's name
     */
    public String getName() {
        validate();
        return name;
    }

    /**
     * Convert this audio device to a string
     * @return The string representation of this audio device
     */
    @Override
    public String toString() {
        if (name == null) {
            return "speaker(null)";
        }
        return "speaker(" + index + ", \"" + name + "\")";
    }

    /**
     * Test whether this audio device represents the same device as another
     * @param o The object to compare this audio device against (must be an instance of AudioDevice)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AudioDevice)) {
            return false;
        }
        return ((AudioDevice)o).getIndex() == getIndex();
    }

}
