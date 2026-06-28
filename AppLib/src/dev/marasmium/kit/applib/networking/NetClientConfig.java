/**
 * File:        NetClientConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines a configuration/settings structure for the MarasmiumKit application framework's network client
 */

package dev.marasmium.kit.applib.networking;

// A configuration/settings structure for the network client
public class NetClientConfig {

    // The maximum number of messages to process per logic update (-1 for infinite)
    public int maxMPU;

    /**
     * Create a network client configuration structure with default settings
     */
    public NetClientConfig() {
        maxMPU = -1;
    }

}
