/**
 * File:        NetClient.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines the main class of the MarasmiumKit application framework's network client
 */

package dev.marasmium.kit.applib.networking;

import dev.marasmium.kit.applib.App;
import dev.marasmium.kit.applib.logging.LogLevel;
import dev.marasmium.kit.applib.logging.LogSource;

import java.net.InetSocketAddress;
import java.util.ArrayList;

// The MarasmiumKit application framework's network client
public class NetClient implements NetListener {

    // The network connection managed by the network client
    private final NetConnection connection = new NetConnection();
    // The maximum number of incoming messages to process per logic update
    private int maxMPU = 0;
    // Whether the network client is waiting to connect to a server
    private volatile boolean connecting = false;
    // Thread for waiting for the network client to connect to a server
    private Thread connectThread = null;
    // The set of listeners subscribed to network event callbacks from the network client
    private final ArrayList<NetListener> listeners = new ArrayList<>();

    /**
     * Initialize the MarasmiumKit application framework's network client
     * @param config The network client's configuration
     * @return Whether the network client was initialized successfully
     */
    public boolean initialize(NetClientConfig config) {
        if (config == null) {
            App.Log.write(LogSource.Network, LogLevel.Error, "No configuration provided");
            return false;
        }
        // Initialize memory
        if (!setMaxMPU(config.maxMPU)) {
            App.Log.write(LogSource.Network, LogLevel.Error, "Failed to set maximum messages to process per update");
            return false;
        }
        // Initialize network connection
        if (!connection.initialize(this, 0)) {
            App.Log.write(LogSource.Network, LogLevel.Error, "Failed to initialize network connection");
            return false;
        }
        App.Log.write(LogSource.Network, LogLevel.Info, "Initialized network client");
        return true;
    }

    /**
     * Start an attempt to connect the network client to a remote server
     * @param hostName The host name / IP of the server to connect to
     * @param port The port to connect to the server on
     * @param timeout The number of seconds to wait for a connection to the given server to open
     * @return Whether the connection attempt was started successfully
     */
    public boolean connect(String hostName, int port, int timeout) {
        if (hostName == null) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "No host name provided");
            return false;
        }
        App.Log.write(LogSource.Network, LogLevel.Info, "Attempting connection to ", hostName, ":", port, " within ",
                timeout, "s");
        if (connecting) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Already connected to ", getHostName(), ":", getPort());
            disconnect();
        }
        // Attempt to resolve the server address
        InetSocketAddress address;
        try {
            address = new InetSocketAddress(hostName, port);
        } catch (IllegalArgumentException _) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Invalid port on host address ", hostName, ":", port);
            return false;
        }
        if (address.isUnresolved()) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to resolve host address ", hostName, ":", port);
            return false;
        }
        // Start the connection attempt
        connectThread = new Thread(() -> {
            App.Log.write(LogSource.Network, LogLevel.Info, "Started connection thread");
            connecting = true;
            if (!connection.connect(address, timeout)) {
                App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to connect");
                disconnect();
            }
        });
        try {
            connectThread.start();
        } catch (IllegalThreadStateException _) {
            App.Log.write(LogSource.Network, LogLevel.Error, "Failed to start connection thread");
            return false;
        }
        return true;
    }

    /**
     * Update the network client's connection to process incoming messages
     */
    public void update() {
        connection.update(maxMPU);
    }

    /**
     * Attempt to send a message to a remote server over the network client's connection
     * @param message The message to send
     * @return Whether the message was dispatched successfully
     */
    public boolean send(NetMessage message) {
        if (message == null) {
            return false;
        }
        return connection.send(message);
    }

    /**
     * Disconnect the network client from any remote server connected
     * @return Whether the network client was safely disconnected or was already disconnected
     */
    public boolean disconnect() {
        boolean success = true;
        App.Log.write(LogSource.Network, LogLevel.Info, "Disconnecting network client from ", getHostName(), ":",
                getPort());
        // Disconnect the network connection
        if (!connection.disconnect()) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to safely disconnect network connection");
            success = false;
        }
        // Stop the connection thread
        if (connectThread != null) {
            connectThread.interrupt();
            try {
                connectThread.join();
            } catch (InterruptedException _) {
                App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to join connection thread");
                success = false;
            }
            connectThread = null;
            App.Log.write(LogSource.Network, LogLevel.Info, "Stopped connection thread");
        }
        connecting = false;
        return success;
    }

    /**
     * Disconnect the network client if connected and free its memory
     * @return Whether the network client was disconnected and freed safely
     */
    public boolean destroy() {
        App.Log.write(LogSource.Network, LogLevel.Info, "Destroying network client");
        boolean success = true;
        // Disconnect the network client if connected
        if (connecting || connection.isConnected()) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Currently connecting or connected to ", getHostName(),
                    ":", getPort());
            if (!disconnect()) {
                App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to safely disconnect");
                success = false;
            }
        }
        // Free memory
        if (!connection.destroy()) {
            success = false;
        }
        listeners.clear();
        App.Log.write(LogSource.Network, LogLevel.Info, "Destroyed network client");
        return success;
    }

    /**
     * Test whether the network client is currently connected to a remote server
     * @return Whether the network client is connected
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * Get the host name of the remote server the network client is currently connected to
     * @return The current host name or empty string if the network client is disconnected
     */
    public String getHostName() {
        return connection.getHostName();
    }

    /**
     * Get the port on which the network client is connected to its current remote server
     * @return The port on which the network client is connected or 0 if the network client is disconnected
     */
    public int getPort() {
        return connection.getPort();
    }

    /**
     * Get the set of incoming messages received from the network client's current server since the last logic update
     * @return The most recent incoming messages
     */
    public ArrayList<NetMessage> getMessages() {
        return connection.getMessages();
    }

    /**
     * Get the maximum number of incoming messages to process per logic update
     * @return The maximum number of incoming messages to process per logic update
     */
    public int getMaxMPU() {
        return maxMPU;
    }

    /**
     * Set the maximum number of incoming messages to process per logic update
     * @param maxMPU The new maximum number of incoming messages to process per logic update (-1 for infinite)
     * @return Whether the given number was valid (positive or -1)
     */
    public boolean setMaxMPU(int maxMPU) {
        if (maxMPU == 0 || maxMPU < -1) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Invalid maximum messages to process per update ",
                    maxMPU);
            return false;
        }
        this.maxMPU = maxMPU;
        App.Log.write(LogSource.Network, LogLevel.Info, "Set maximum messages to process per update ", maxMPU);
        return true;
    }

    /**
     * Whether the network client is currently waiting for a connection from a remote server
     * @return Whether the network client is currently connecting
     */
    public boolean isConnecting() {
        return connecting;
    }

    /**
     * Subscribe a listener to receive network event callbacks from the network client
     * @param listener The listener to add
     * @return Whether the listener was added successfully
     */
    public boolean addListener(NetListener listener) {
        if (listener == null) {
            return false;
        }
        if (listeners.contains(listener)) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to add network listener, already present");
            return false;
        }
        App.Log.write(LogSource.Network, LogLevel.Info, "Adding network listener");
        listeners.add(listener);
        return true;
    }

    /**
     * Unsubscribe a listener from receiving network event callbacks from the network client
     * @param listener The listener to remove
     * @return Whether the listener was removed successfully
     */
    public boolean removeListener(NetListener listener) {
        if (listener == null) {
            return false;
        }
        if (!listeners.contains(listener)) {
            App.Log.write(LogSource.Network, LogLevel.Warning, "Failed to remove network listener, not present");
            return false;
        }
        App.Log.write(LogSource.Network, LogLevel.Info, "Removing network listener");
        listeners.remove(listener);
        return true;
    }

    /**
     * The network client has connected to a remote server
     * @param clientID The ID number of the connected network connection (unused by the network client)
     * @return Whether the connection should be accepted (unused by the network client)
     */
    @Override
    public boolean netConnected(int clientID) {
        App.Log.write(LogSource.Network, LogLevel.Info, "Connected to ", getHostName(), ":", getPort());
        for (NetListener listener : listeners) {
            listener.netConnected(clientID);
        }
        return true;
    }

    /**
     * The network client has received a message from a remote server
     * @param clientID The ID number of the network connection (unused by the network client)
     * @param message The message received
     */
    @Override
    public void netMessageReceived(int clientID, NetMessage message) {
        if (message == null) {
            return;
        }
        for (NetListener listener : listeners) {
            listener.netMessageReceived(clientID, message);
        }
    }

    /**
     * The network client has disconnected from a remote server
     * @param clientID The ID number of the disconnected network connection (unused by the network client)
     */
    @Override
    public void netDisconnected(int clientID) {
        App.Log.write(LogSource.Network, LogLevel.Info, "Disconnected");
        for (NetListener listener : listeners) {
            listener.netDisconnected(clientID);
        }
    }

}
