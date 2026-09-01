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
    public final LogManagerConfig log = new LogManagerConfig();
    /**
     * The parent class of the server to subscribe to network event callbacks
     */
    public final NetListener parent;
    /**
     * The port for the server to listen for new connections on
     */
    public int port;
    /**
     * The maximum number of incoming messages to process per update per client (-1 for infinite)
     */
    public int maxMPUPC = 0;
    /**
     * The maximum number of clients to allow to connect to the server simultaneously (-1 for infinite)
     */
    public int maxClients = 0;

    /**
     * Construct a network server configuration structure with default settings
     * @param parent The parent class of the server to subscribe to network event callbacks
     * @param port The port for the server to listen for client connections on
     */
    public NetServerConfig(NetListener parent, int port) {
        this.parent = parent;
        this.port = port;
    }

    /**
     * Apply the default settings to this network server configuration structure
     */
    public void applyDefaults() {
        log.applyDefaults();
        log.fileOutputPath = "MarasmiumKit-Server.log";
        maxMPUPC = -1;
        maxClients = -1;
    }

}
