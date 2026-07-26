/**
 * File:        NetServer.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines the main class of a network server for MarasmiumKit application framework clients
 */

package dev.marasmium.kit.applib.networking;

import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogManager;
import dev.marasmium.kit.applib.logging.LogSource;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Abstract server interface for managing many network connections from clients
 */
public class NetServer implements NetListener {

    /**
     * The server's logging system
     */
    private final LogManager log;
    /**
     * Log source flag for the abstract server interface
     */
    private final LogSource logSource = new LogSource("Network Server");
    /**
     * The parent class of the server subscribed to network event callbacks
     */
    private NetListener parent = null;
    /**
     * Port for the server to listen for new connections on
     */
    private int port = 0;
    /**
     * Socket for accepting new client connections
     */
    private ServerSocket serverSocket = null;
    /**
     * Whether the server is currently accepting new clients
     */
    private volatile boolean running = false;
    /**
     * Background thread for accepting new clients
     */
    private Thread acceptThread = null;
    /**
     * The next ID number of assign to a new client
     */
    private int nextClientID = 0;
    /**
     * The maximum number of clients allowed to connect to the server simultaneously
     */
    private int maxClients = 0;
    /**
     * The set of clients currently connected to the server
     */
    private final ConcurrentLinkedDeque<NetConnection> clients = new ConcurrentLinkedDeque<>();
    /**
     * The maximum number of incoming messages to process per logic update per client
     */
    private int maxMPUPC = 0;

    /**
     * Construct a new network server with only a logging system
     */
    public NetServer() {
        log = new LogManager();
    }

    /**
     * Initialize the server's memory and prepare it to listen for client connections
     * @param config The server's configuration structure
     * @return Whether the server was initialized successfully
     */
    public boolean initialize(NetServerConfig config) {
        if (config == null) {
            return false;
        }
        // Initialize the server log
        if (!log.initialize(config.log)) {
            return false;
        }
        log.write(logSource, LogLevel.Info, "Initialized logging system");
        // Initialize memory
        if (!setParent(config.parent)) {
            log.write(logSource, LogLevel.Error, "Failed to set parent listener");
            return false;
        }
        this.port = config.port;
        if (!setMaxClients(config.maxClients)) {
            log.write(logSource, LogLevel.Error, "Failed to set maximum client count");
            return false;
        }
        if (!setMaxMPUPC(config.maxMPUPC)) {
            log.write(logSource, LogLevel.Error, "Failed to set maximum messages processed per update per client");
            return false;
        }
        return true;
    }

    /**
     * Start the server's background thread for accepting client connections
     */
    public void run() {
        acceptThread = new Thread(() -> {
            // Open the listening socket
            log.write(logSource, LogLevel.Info, "Opening server socket on port ", port);
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException | IllegalArgumentException _) {
                log.write(logSource, LogLevel.Error, "Failed to open server socket on port ", port);
                return;
            }
            running = true;
            log.write(logSource, LogLevel.Info, "Now accepting clients");
            // Accept clients
            while (running) {
                Socket socket;
                try {
                    log.write(logSource, LogLevel.Info, "Listening on port ", port);
                    socket = serverSocket.accept();
                } catch (IOException | IllegalBlockingModeException e) {
                    log.write(logSource, LogLevel.Warning, "Failed to accept client");
                    continue;
                }
                log.write(logSource, LogLevel.Info, "Found new connection from ",
                        socket.getInetAddress().getHostName());
                new Thread(() -> {
                    log.write(logSource, LogLevel.Info, "Initializing new client connection with ID ", nextClientID);
                    NetConnection client = new NetConnection();
                    if (!client.initialize(this, nextClientID++)) {
                        log.write(logSource, LogLevel.Warning, "Failed to initialize client connection");
                        return;
                    }
                    log.write(logSource, LogLevel.Info, "Connecting to client");
                    if (!client.connect(socket)) {
                        log.write(logSource, LogLevel.Warning, "Failed to connect to client connection");
                        return;
                    }
                    clients.add(client);
                }).start();
            }
        });
        try {
            acceptThread.start();
        } catch (IllegalThreadStateException _) {
            log.write(logSource, LogLevel.Error, "Failed to start client-accepting thread");
        }
    }

    /**
     * Process incoming messages for each of the server's connected clients
     */
    public void update() {
        for (NetConnection client : clients) {
            if (!client.isConnected()) {
                netDisconnected(client.getClientID());
                continue;
            }
            client.update(maxMPUPC);
        }
    }

    /**
     * Send a message to a client currently connected to the server
     * @param message The message to send
     * @param clientID The ID number of the client to send the message to
     * @return Whether the client was found and could dispatch the message
     */
    public boolean send(NetMessage message, int clientID) {
        if (message == null) {
            return false;
        }
        NetConnection client = getClient(clientID);
        if (client != null) {
            return client.send(message);
        }
        return false;
    }

    /**
     * Broadcast a message to all clients connected to the server
     * @param message The message to broadcast
     * @return Whether the message was dispatched successfully to all clients
     */
    public boolean broadcast(NetMessage message) {
        if (message == null) {
            return false;
        }
        return broadcast(message, -1);
    }

    /**
     * Broadcast a message to all clients connected to the server except one
     * @param message The message to broadcast
     * @param ignoredClientID The ID of the client to ignore when broadcasting the message (-1 for none)
     * @return Whether the message was dispatched successfully to all clients except the ignored one
     */
    public boolean broadcast(NetMessage message, int ignoredClientID) {
        if (message == null) {
            return false;
        }
        // Check the ignored client ID
        if (ignoredClientID < -1) {
            log.write(logSource, LogLevel.Error, "Ignored client ID ", ignoredClientID, " out of bounds for broadcast");
            return false;
        }
        boolean success = true;
        // Broadcast to all clients
        for (NetConnection client : clients) {
            if (client.getClientID() == ignoredClientID) {
                continue;
            }
            if (!client.send(message)) {
                success = false;
            }
        }
        return success;
    }

    /**
     * Stop the server's background client accepting thread and disconnect all current clients
     * @return Whether the server was stopped safely
     */
    public boolean stop() {
        boolean success = true;
        log.write(logSource, LogLevel.Info, "Stopping server");
        running = false;
        // Stop accepting new clients
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException _) {
                log.write(logSource, LogLevel.Info, "Failed to close server socket");
                success = false;
            }
            serverSocket = null;
        }
        log.write(logSource, LogLevel.Info, "Stopped server socket");
        if (acceptThread != null) {
            acceptThread.interrupt();
            try {
                acceptThread.join();
            } catch (InterruptedException _) {
                log.write(logSource, LogLevel.Info, "Failed to join client-accepting thread");
                success = false;
            }
            acceptThread = null;
        }
        log.write(logSource, LogLevel.Info, "Stopped client-accepting thread");
        // Disconnect all clients
        nextClientID = 0;
        for (NetConnection client : clients) {
            log.write(logSource, LogLevel.Info, "Disconnecting client ", client.getClientID());
            if (!client.disconnect()) {
                success = false;
            }
        }
        clients.clear();
        return success;
    }

    /**
     * Stop the server and free its memory
     * @return Whether the server was stopped safely and freed successfully
     */
    public boolean destroy() {
        log.write(logSource, LogLevel.Info, "Destroying network server");
        boolean success = true;
        // Stop the server
        if (running) {
            log.write(logSource, LogLevel.Info, "Stopping server connection threads");
            if (!stop()) {
                log.write(logSource, LogLevel.Info, "Failed to safely stop server connection threads");
                success = false;
            }
        }
        // Free memory
        maxClients = 0;
        maxMPUPC = 0;
        log.write(logSource, LogLevel.Info, "Destroying server log");
        if (!log.destroy()) {
            success = false;
        }
        return success;
    }

    /**
     * Get the server's logging system
     * @return The server log
     */
    public LogManager getLog() {
        return log;
    }

    /**
     * Get the server's parent class subscribed to network event callbacks
     * @return The server's parent class
     */
    public NetListener getParent() {
        return parent;
    }

    /**
     * Set the server's parent class to subscribe to network event callbacks
     * @param parent The server's new parent class (must not be null)
     * @return Whether the parent class was set successfully
     */
    public boolean setParent(NetListener parent) {
        if (parent == null) {
            return false;
        }
        this.parent = parent;
        return true;
    }

    /**
     * Get the port the server listens on for new client connections
     * @return The server's port
     */
    public int getPort() {
        return port;
    }

    /**
     * Test whether the server is currently accepting new client connections
     * @return Whether the server is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Get the maximum number of clients allowed to be connected to the server simultaneously
     * @return The maximum number of clients of the server
     */
    public int getMaxClients() {
        return maxClients;
    }

    /**
     * Set the maximum number of clients allowed to be connected to the server simultaneously
     * @param maxClients The new maximum number of clients of the server (-1 for infinite)
     * @return Whether the given count was valid (positive or -1)
     */
    public boolean setMaxClients(int maxClients) {
        if (maxClients == 0 || maxClients < -1) {
            return false;
        }
        this.maxClients = maxClients;
        return true;
    }

    /**
     * Get the ID numbers of the set of clients currently connected to the server
     * @return The IDs of the current set of clients
     */
    public ArrayList<Integer> getClientIDs() {
        ArrayList<Integer> clientIDs = new ArrayList<>();
        for (NetConnection client : clients) {
            clientIDs.add(client.getClientID());
        }
        return clientIDs;
    }

    /**
     * Get the server's connection to a client by its ID number
     * @param clientID The ID number of the client to get
     * @return The client connection with the given ID or null if not found
     */
    public NetConnection getClient(int clientID) {
        for (NetConnection client : clients) {
            if (client.getClientID() == clientID) {
                return client;
            }
        }
        return null;
    }

    /**
     * Find and disconnect a client of the server by its ID number
     * @param clientID The ID number of the client to disconnect
     * @return Whether the connection was found and disconnected safely
     */
    public boolean removeClient(int clientID) {
        NetConnection client = getClient(clientID);
        if (client == null) {
            return false;
        }
        return client.disconnect();
    }

    /**
     * Get the maximum number of incoming messages to process per logic update per client
     * @return The maximum number of messages to process
     */
    public int getMaxMPUPC() {
        return maxMPUPC;
    }

    /**
     * Set the maximum number of incoming messages to process per logic update per client
     * @param maxMPUPC The new maximum number of messages to process (-1 for infinite)
     * @return Whether the given count was valid (positive or -1)
     */
    public boolean setMaxMPUPC(int maxMPUPC) {
        if (maxMPUPC == 0 || maxMPUPC < -1) {
            return false;
        }
        this.maxMPUPC = maxMPUPC;
        return true;
    }

    /**
     * A new client has connected to the server
     * @param clientID The ID number of the connected network connection
     * @return Whether the client connection was accepted by the server
     */
    @Override
    public boolean netConnected(int clientID) {
        // Check against maximum client count
        if (clients.size() > maxClients && maxClients != -1) {
            log.write(logSource, LogLevel.Warning, "Maximum client count exceeded, rejecting client ", clientID);
            getClient(clientID).setRejected(true);
            return false;
        }
        // Check against server logic
        if (!parent.netConnected(clientID)) {
            log.write(logSource, LogLevel.Warning, "Client ", clientID, " rejected by server logic");
            getClient(clientID).setRejected(true);
            return false;
        }
        return true;
    }

    /**
     * The server has received a message from a client
     * @param clientID The ID number of the network connection
     * @param message The message received
     */
    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        if (message == null) {
            return;
        }
        parent.netMessageReceived(clientID, message);
    }

    /**
     * A client has disconnected from the server
     * @param clientID The ID number of the disconnected network connection
     */
    @Override
    public void netDisconnected(int clientID) {
        NetConnection client = getClient(clientID);
        if (client != null) {
            if (!client.isRejected()) {
                parent.netDisconnected(clientID);
            }
            clients.remove(client);
        }
    }

}
