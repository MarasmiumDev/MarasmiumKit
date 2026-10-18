/**
 * File:        NetMessageTypes.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.10.16
 * Purpose:     Defines constants for the network message types used in the AppTest and ServerTest programs
 */

package dev.marasmium.kit.netinterfacetest;

/**
 * Constants for shared network message types used in the AppTest and ServerTest programs
 */
public class NetMessageTypes {

    /**
     * Type for the client hello message
     */
    public static final int Client_Hello = 1;
    /**
     * Type for the server hello message
     */
    public static final int Server_Hello = 2;
    /**
     * Type for the server's client connection notification message
     */
    public static final int Server_Client_Connected = 3;
    /**
     * Type for the client's movement message
     */
    public static final int Client_Movement = 4;
    /**
     * Type for the server's client disconnection notification message
     */
    public static final int Server_Client_Disconnected = 5;

}
