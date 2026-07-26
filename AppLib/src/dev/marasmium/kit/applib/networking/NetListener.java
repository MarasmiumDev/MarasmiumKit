/**
 * File:        NetListener.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines an abstract interface for network event callbacks
 */

package dev.marasmium.kit.applib.networking;

/**
 * Abstract interface for network event callbacks
 */
public interface NetListener {

    /**
     * Callback for a network connection connecting to a remote host
     * @param clientID The ID number of the connected network connection
     * @return Whether the connection should be accepted
     */
    default boolean netConnected(int clientID) {
        return true;
    }

    /**
     * Callback for a message received from a remote host by a network connection
     * @param clientID The ID number of the network connection
     * @param message The message received
     */
    default void netMessageReceived(int clientID, NetMessage message) {}

    /**
     * Callback for a network connection disconnecting from a remote host
     * @param clientID The ID number of the disconnected network connection
     */
    default void netDisconnected(int clientID) {}

}
