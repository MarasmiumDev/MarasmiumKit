/**
 * File:        NetMessage.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines a serializable message structure to be sent and received by network connections
 */

package dev.marasmium.kit.applib.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A serialized message structure to be sent and received by network connections
 */
public class NetMessage implements Serializable {

    /**
     * The type ID number of this message
     */
    private int type = 0;
    /**
     * The payload data of this message
     */
    private byte[] data = null;

    /**
     * Construct a message with a type
     * @param type The type ID number for this message
     */
    public void initialize(int type) {
        setType(type);
        data = new byte[0];
    }

    /**
     * Free this message's memory
     */
    public void destroy() {
        type = 0;
        data = null;
    }

    /**
     * Get this message's type ID number
     * @return This message's type ID
     */
    public int getType() {
        return type;
    }

    /**
     * Set this message's type ID number
     * @param type This message's new type ID
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Add a serializable object to the end of this message's payload data
     * @param data The object to add
     * @return Whether the object was added successfully
     */
    public boolean addData(Object data) {
        byte[] bytes;
        // Serialize the object to add
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(byteOutput);
            objectOutput.writeObject(data);
            objectOutput.flush();
            bytes = byteOutput.toByteArray();
            objectOutput.close();
            byteOutput.close();
        } catch (IOException | NullPointerException _) {
            return false;
        }
        // Append data
        byte[] copy = new byte[this.data.length];
        System.arraycopy(this.data, 0, copy, 0, this.data.length);
        this.data = new byte[copy.length + bytes.length];
        System.arraycopy(copy, 0, this.data, 0, copy.length);
        System.arraycopy(bytes, 0, this.data, copy.length, bytes.length);
        return true;
    }

    /**
     * Remove and return the first serialized object from this message's payload data
     * @return The removed object
     */
    public Object removeData() {
        if (this.data.length == 0) {
            return null;
        }
        // Deserialize the first available object
        Object data;
        int dataSize;
        try {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(this.data);
            ObjectInputStream objectInput = new ObjectInputStream(byteInput);
            data = objectInput.readObject();
            dataSize = this.data.length - byteInput.available();
            objectInput.close();
            byteInput.close();
        } catch (IOException | IllegalStateException | NullPointerException | ClassNotFoundException _) {
            return null;
        }
        // Remove data
        byte[] copy = new byte[this.data.length];
        System.arraycopy(this.data, 0, copy, 0, this.data.length);
        this.data = new byte[copy.length - dataSize];
        System.arraycopy(copy, 0, this.data, 0, copy.length - dataSize);
        return data;
    }

    /**
     * Get this message's size
     * @return This message's size in bytes
     */
    public int getSize() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

    /**
     * Convert this message to a string representation of its attributes
     * @return The attributes of this message in a string
     */
    @Override
    public String toString() {
        return "network message(type " + type + ", " + getSize() + "B)";
    }

    /**
     * Create a copy of this message
     * @return A copy of this message with the same type and data
     */
    @Override
    public NetMessage clone() {
        NetMessage message = new NetMessage();
        message.type = this.type;
        if (data == null) {
            return message;
        }
        message.data = new byte[this.data.length];
        try {
            System.arraycopy(this.data, 0, message.data, 0, this.data.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException | NullPointerException _) {
            return message;
        }
        return message;
    }

}
