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
public class NetMessage implements Serializable, Cloneable {

    /**
     * The type ID number of this message
     */
    private int type = 0;
    /**
     * The payload data of this message
     */
    private byte[] data = null;

    /**
     * Convert this message to a string representation of its attributes
     * @return The attributes of this message in a string
     */
    @Override
    public String toString() {
        return "network message(type " + type + ", " + getDataSize() + "B)";
    }

    /**
     * Create a copy of this message
     * @return A copy of this message with the same type and data
     */
    @Override
    public NetMessage clone() {
        NetMessage m;
        try {
            m = (NetMessage)super.clone();
        } catch (CloneNotSupportedException _) {
            return null;
        }
        m.type = this.type;
        if (data == null) {
            return m;
        }
        m.data = new byte[this.data.length];
        try {
            System.arraycopy(this.data, 0, m.data, 0, this.data.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException | NullPointerException _) {
            return m;
        }
        return m;
    }

    /**
     * Construct a message with a type
     * @param type The type ID number for this message
     */
    public void initialize(int type) {
        setType(type);
        data = new byte[0];
    }

    /**
     * Add a serializable object to the end of this message's payload data
     * @param data The object to add
     * @return Whether the object was added successfully
     */
    public boolean addData(Object data) {
        if (this.data == null) {
            this.data = new byte[0];
        }
        if (data == null) {
            return false;
        }
        byte[] bytes;
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
        try {
            byte[] copy = new byte[this.data.length];
            System.arraycopy(this.data, 0, copy, 0, this.data.length);
            this.data = new byte[copy.length + bytes.length];
            System.arraycopy(copy, 0, this.data, 0, copy.length);
            System.arraycopy(bytes, 0, this.data, copy.length, bytes.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException _) {
            return false;
        }
        return true;
    }

    /**
     * Remove and return the first serialized object from this message's payload data
     * @return The removed object
     */
    public Object removeData() {
        if (this.data == null) {
            return null;
        }
        if (this.data.length == 0) {
            return null;
        }
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
        try {
            byte[] copy = new byte[this.data.length];
            System.arraycopy(this.data, 0, copy, 0, this.data.length);
            this.data = new byte[copy.length - dataSize];
            System.arraycopy(copy, dataSize, this.data, 0, copy.length - dataSize);
        } catch (IndexOutOfBoundsException | ArrayStoreException _) {
            return null;
        }
        return data;
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
     * Get this message's payload data
     * @return This message's data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Set this message's payload data
     * @param data This message's new data
     * @return Whether the given data was valid and was set successfully
     */
    public boolean setData(byte[] data) {
        if (data == null) {
            return false;
        }
        this.data = new byte[data.length];
        try {
            System.arraycopy(data, 0, this.data, 0, data.length);
        } catch (IndexOutOfBoundsException | ArrayStoreException _) {
            return false;
        }
        return true;
    }

    /**
     * Get this message's size
     * @return This message's size in bytes
     */
    public int getDataSize() {
        if (data == null) {
            return 0;
        }
        return data.length;
    }

}
