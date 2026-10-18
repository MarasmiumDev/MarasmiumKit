/**
 * File:        Configuration.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.09
 * Purpose:     Defines a data structure representing a configuration
 */

package dev.marasmium.kit.applib.assets;

import dev.marasmium.kit.applib.data.Angle;
import dev.marasmium.kit.applib.data.Colour;
import dev.marasmium.kit.applib.data.Line;
import dev.marasmium.kit.applib.data.Vector;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Data structure representing a configuration containing key-value pairs
 */
public class Configuration implements Cloneable {

    /**
     * The key-value pairs of this configuration
     */
    private HashMap<String, byte[]> values = null;

    /**
     * Convert this configuration to a string
     * @return The string representation of this configuration
     */
    @Override
    public String toString() {
        if (values == null) {
            return "configuration(null)";
        }
        return "configuration(" + values.size() + " values, " + getValueDataSize() + "B)";
    }

    /**
     * Make a copy of this configuration with the same key-value pairs
     * @return A clone of this configuration
     */
    @Override
    public Configuration clone() {
        Configuration configuration;
        try {
            configuration = (Configuration)super.clone();
        } catch (CloneNotSupportedException _) {
            return null;
        }
        if (values != null) {
            for (HashMap.Entry<String, byte[]> entry : values.entrySet()) {
                try {
                    configuration.values.put(entry.getKey(), entry.getValue().clone());
                } catch (IllegalStateException _) {
                    return null;
                }
            }
        }
        return configuration;
    }

    /**
     * Initialize this configuration's memory
     */
    public void initialize() {
        values = new HashMap<>();
    }

    /**
     * Initialize this configuration's memory with an initial set of key-value pairs
     * @param valueNames The set of names (keys) of this configuration's values
     * @param valueData This configuration's serialized data values
     * @return Whether the given key-value pairs were valid and this configuration was initialized successfully
     */
    public boolean initialize(String[] valueNames, byte[][] valueData) {
        initialize();
        if (valueNames == null || valueData == null) {
            return false;
        }
        if (valueNames.length != valueData.length) {
            return false;
        }
        for (int i = 0; i < valueNames.length; i++) {
            if (!setValueData(valueNames[i], valueData[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Free this configuration's memory
     */
    public void destroy() {
        if (values != null) {
            values.clear();
            values = null;
        }
    }

    /**
     * Get the key-value pairs contained in this configuration
     * @return The values contained in this configuration
     */
    public HashMap<String, byte[]> getValues() {
        return values;
    }

    /**
     * Get the number of key-value pairs in this configuration
     * @return This configuration's value count
     */
    public int getValueCount() {
        if (values == null) {
            return 0;
        }
        return values.size();
    }

    /**
     * Get the size of the data contained in this configuration's values in bytes
     * @return This configuration's value data size
     */
    public int getValueDataSize() {
        if (values == null) {
            return 0;
        }
        int valueDataSize = 0;
        for (HashMap.Entry<String, byte[]> entry : values.entrySet()) {
            try {
                valueDataSize += entry.getValue().length;
            } catch (IllegalStateException _) {
                return 0;
            }
        }
        return valueDataSize;
    }

    /**
     * Get a serialized data value in this configuration by its name
     * @param valueName The name (key) to retrieve the data by
     * @return The serialized data stored by the given name in this configuration or null if none was found
     */
    public byte[] getValueData(String valueName) {
        if (values == null) {
            return null;
        }
        if (!values.containsKey(valueName)) {
            return null;
        }
        return values.get(valueName);
    }

    /**
     * Get a vector value in this configuration by its name
     * @param valueName The name (key) to retrieve the vector by
     * @return The vector stored by the given name in this configuration or null if none was found
     */
    public Vector getVectorValue(String valueName) {
        return Vector.Deserialize(getValueData(valueName));
    }

    /**
     * Get a line value in this configuration by its name
     * @param valueName The name (key) to retrieve the line by
     * @return The line stored by the given name in this configuration or null if none was found
     */
    public Line getLineValue(String valueName) {
        return Line.Deserialize(getValueData(valueName));
    }

    /**
     * Get a colour value in this configuration by its name
     * @param valueName The name (key) to retrieve the colour by
     * @return The colour stored by the given name in this configuration or null if none was found
     */
    public Colour getColourValue(String valueName) {
        return Colour.Deserialize(getValueData(valueName));
    }

    /**
     * Get an angle value in this configuration by its name
     * @param valueName The name (key) to retrieve the angle by
     * @return The angle stored by the given name in this configuration or null if none was found
     */
    public Angle getAngleValue(String valueName) {
        return Angle.Deserialize(getValueData(valueName));
    }

    /**
     * Get an integer value in this configuration by its name
     * @param valueName The name (key) to retrieve the integer by
     * @return The integer stored by the given name in this configuration or null if none was found
     */
    public Integer getIntegerValue(String valueName) {
        byte[] valueData = getValueData(valueName);
        if (valueData == null) {
            return null;
        }
        if (valueData.length != Integer.BYTES) {
            return null;
        }
        int integerValue;
        try {
            integerValue = ByteBuffer.wrap(valueData).getInt();
        } catch (BufferUnderflowException _) {
            return null;
        }
        return integerValue;
    }

    /**
     * Get a float value in this configuration by its name
     * @param valueName The name (key) to retrieve the float by
     * @return The float stored by the given name in this configuration or null if none was found
     */
    public Float getFloatValue(String valueName) {
        byte[] valueData = getValueData(valueName);
        if (valueData == null) {
            return null;
        }
        if (valueData.length != Float.BYTES) {
            return null;
        }
        float floatValue;
        try {
            floatValue = ByteBuffer.wrap(valueData).getFloat();
        } catch (BufferUnderflowException _) {
            return null;
        }
        return floatValue;
    }

    /**
     * Get a string value in this configuration by its name
     * @param valueName The name (key) to retrieve the string by
     * @return The string stored by the given name in this configuration or null if none was found
     */
    public String getStringValue(String valueName) {
        byte[] valueData = getValueData(valueName);
        if (valueData == null) {
            return null;
        }
        return new String(valueData);
    }

    /**
     * Get a boolean value in this configuration by its name
     * @param valueName The name (key) to retrieve the boolean by
     * @return The boolean stored by the given name in this configuration or null if none was found
     */
    public Boolean getBooleanValue(String valueName) {
        byte[] valueData = getValueData(valueName);
        if (valueData == null) {
            return null;
        }
        if (valueData.length != 1) {
            return null;
        }
        return valueData[0] != 0;
    }

    /**
     * Set a serialized data value to this configuration
     * @param valueName The name to assign to the data in this configuration
     * @param valueData The serialized data value to add
     * @return Whether all parameters were valid and the data was set successfully
     */
    public boolean setValueData(String valueName, byte[] valueData) {
        if (values == null) {
            return false;
        }
        if (valueName == null || valueData == null) {
            return false;
        }
        values.put(valueName, valueData.clone());
        return true;
    }

    /**
     * Set a vector value to this configuration
     * @param valueName The name to assign to the vector in this configuration
     * @param vectorValue The vector value to set
     * @return Whether all parameters were valid and the vector was set successfully
     */
    public boolean setVectorValue(String valueName, Vector vectorValue) {
        if (vectorValue == null) {
            return false;
        }
        return setValueData(valueName, vectorValue.serialize());
    }

    /**
     * Set a line value to this configuration
     * @param valueName The name to assign to the line in this configuration
     * @param lineValue The line value to set
     * @return Whether all parameters were valid and the line was set successfully
     */
    public boolean setLineValue(String valueName, Line lineValue) {
        if (lineValue == null) {
            return false;
        }
        return setValueData(valueName, lineValue.serialize());
    }

    /**
     * Set a colour value to this configuration
     * @param valueName The name to assign to the colour in this configuration
     * @param colourValue The colour value to set
     * @return Whether all parameters were valid and the colour was set successfully
     */
    public boolean setColourValue(String valueName, Colour colourValue) {
        if (colourValue == null) {
            return false;
        }
        return setValueData(valueName, colourValue.serialize());
    }

    /**
     * Set an angle value to this configuration
     * @param valueName The name to assign to the angle in this configuration
     * @param angleValue The angle value to set
     * @return Whether all parameters were valid and the angle was set successfully
     */
    public boolean setAngleValue(String valueName, Angle angleValue) {
        if (angleValue == null) {
            return false;
        }
        return setValueData(valueName, angleValue.serialize());
    }

    /**
     * Set an integer value to this configuration
     * @param valueName The name to assign to the integer in this configuration
     * @param integerValue The integer to set
     * @return Whether all parameters were valid and the integer was set successfully
     */
    public boolean setIntegerValue(String valueName, int integerValue) {
        byte[] valueData;
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.putInt(integerValue);
            valueData = buffer.array();
        } catch (IllegalArgumentException | BufferOverflowException | UnsupportedOperationException _) {
            return false;
        }
        return setValueData(valueName, valueData);
    }

    /**
     * Set a float value to this configuration
     * @param valueName The name to assign to the float in this configuration
     * @param floatValue The float to set
     * @return Whether all parameters were valid and the float was set successfully
     */
    public boolean setFloatValue(String valueName, float floatValue) {
        byte[] valueData;
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocate(Float.BYTES);
            buffer.putFloat(floatValue);
            valueData = buffer.array();
        } catch (IllegalArgumentException | BufferOverflowException | UnsupportedOperationException _) {
            return false;
        }
        return setValueData(valueName, valueData);
    }

    /**
     * Set a string value to this configuration
     * @param valueName The name to assign to the string in this configuration
     * @param stringValue The string to set
     * @return Whether all parameters were valid and the string was set successfully
     */
    public boolean setStringValue(String valueName, String stringValue) {
        if (stringValue == null) {
            return false;
        }
        return setValueData(valueName, stringValue.getBytes());
    }

    /**
     * Set a boolean value to this configuration
     * @param valueName The name to assign to the boolean in this configuration
     * @param booleanValue The boolean to set
     * @return Whether all parameters were valid and the boolean was set successfully
     */
    public boolean setBooleanValue(String valueName, boolean booleanValue) {
        return setValueData(valueName, new byte[] { booleanValue ? (byte)0x01 : (byte)0x00 });
    }

    /**
     * Remove a value from this configuration by its name
     * @param valueName The name (key) to remove from this configuration
     * @return Whether the name was found and was removed successfully
     */
    public boolean removeValue(String valueName) {
        if (values == null) {
            return false;
        }
        if (!values.containsKey(valueName)) {
            return false;
        }
        return values.remove(valueName) != null;
    }

}
