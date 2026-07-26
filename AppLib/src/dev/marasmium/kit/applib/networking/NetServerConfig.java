/**
 * File:        NetServerConfig.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines a settings/configuration structure for MarasmiumKit network servers
 */

package dev.marasmium.kit.applib.networking;

import dev.marasmium.kit.applib.logging.LogManagerConfig;

/**
 * A configuration/settings structure for MarasmiumKit network servers
 */
public class NetServerConfig {

    /**
     * The server log's configuration
     */
    public final LogManagerConfig log;
    /**
     * The parent class of the server to subscribe to network event callbacks
     */
    public final NetListener parent;
    /**
     * The port for the server to listen for new connections on
     */
    public int port;
    /**
     * The maximum number of incoming messages to process per update per client
     */
    public int maxMPUPC;
    /**
     * The maximum number of clients to allow to connect to the server simultaneously
     */
    public int maxClients;

    /**
     * Construct a network server configuration structure with default settings
     * @param parent The parent class of the server to subscribe to network event callbacks
     * @param port The port for the server to listen for client connections on
     */
    public NetServerConfig(NetListener parent, int port) {
        log = new LogManagerConfig();
        log.fileOutputPath = "MarasmiumKit-Server.log";
        this.parent = parent;
        this.port = port;
        maxMPUPC = -1;
        maxClients = -1;
    }

}
