/**
 * File:        NetConnection.java
 * Author:      MarasmiumDev (info@marasmium.dev)
 * Created:     2026.06.20
 * Purpose:     Defines an intermediary between network clients and network servers for managing input/output operations
 */

package dev.marasmium.kit.applib.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Intermediate class for connecting network clients and network servers
 */
public class NetConnection {

    /**
     * This client's parent class subscribed to network event callbacks
     */
    private NetListener parent = null;
    /**
     * This client's ID number
     */
    private int clientID = 0;
    /**
     * This client's TCP socket
     */
    private Socket socket = null;
    /**
     * Whether this client is currently connected to a remote host
     */
    private volatile boolean connected = false;
    /**
     * Whether this client was connected to a remote host in the last logic update
     */
    private volatile boolean wasConnected = false;
    /**
     * Whether a connection from this client has been rejected by its parent class
     */
    private volatile boolean rejected = false;
    /**
     * Thread for reading messages from this connection's remote host
     */
    private Thread inputThread = null;
    /**
     * Input stream for reading messages from this connection's remote host
     */
    private ObjectInputStream inputStream = null;
    /**
     * Scope lock for managing incoming messages
     */
    private final ReentrantLock inputLock = new ReentrantLock();
    /**
     * The set of messages received by this connection since its last logic update
     */
    private final ArrayList<NetMessage> inputMessages = new ArrayList<>();
    /**
     * Thread for writing messages to this connection's remote host
     */
    private Thread outputThread = null;
    /**
     * Output stream for writing messages to this connection's remote host
     */
    private ObjectOutputStream outputStream = null;
    /**
     * Scope lock for managing outgoing messages
     */
    private final ReentrantLock outputLock = new ReentrantLock();
    /**
     * The set of messages to be sent by this connection
     */
    private final ArrayList<NetMessage> outputMessages = new ArrayList<>();

    /**
     * Initialize this network connection with a parent class and ID number
     * @param parent The parent class of this connection to subscribe to network event callbacks
     * @param clientID The ID number assigned to this client
     * @return Whether this client was initialized successfully
     */
    public boolean initialize(NetListener parent, int clientID) {
        // Initialize memory
        if (!setParent(parent)) {
            return false;
        }
        setClientID(clientID);
        return true;
    }

    /**
     * Attempt to connect this connection to a remote host given the host address and a timeout
     * @param address The address to connect to
     * @param timeout The number of seconds to wait for the connection to be established
     * @return Whether the connection was established successfully
     */
    public boolean connect(InetSocketAddress address, int timeout) {
        if (address == null) {
            return false;
        }
        if (connected) {
            disconnect();
        }
        // Connect socket
        socket = new Socket();
        try {
            socket.connect(address, timeout * 1000);
        } catch (IOException | IllegalBlockingModeException | IllegalArgumentException e) {
            disconnect();
            return false;
        }
        // Open input and output streams
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
        } catch (IOException | NullPointerException _) {
            disconnect();
            return false;
        }
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException | IllegalStateException | NullPointerException _) {
            disconnect();
            return false;
        }
        // Start reading messages asynchronously
        read();
        return true;
    }

    /**
     * Attempt to connect this connection to a remote host given a pre-connected socket
     * @param socket The socket to open input and output streams from
     * @return Whether the connection was established successfully
     */
    public boolean connect(Socket socket) {
        if (socket == null) {
            return false;
        }
        if (connected) {
            disconnect();
        }
        rejected = false;
        this.socket = socket;
        // Open input and output streams
        try {
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException | IllegalStateException | NullPointerException _) {
            disconnect();
            return false;
        }
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
        } catch (IOException | NullPointerException _) {
            disconnect();
            return false;
        }
        read();
        return true;
    }

    /**
     * Process network events since the last logic updates
     * @param maxMessages The maximum number of incoming messages to process (-1 for infinite)
     */
    public void update(int maxMessages) {
        if (parent == null) {
            return;
        }
        // Process connect and disconnect events
        if (connected && !wasConnected) {
            wasConnected = true;
            if (!parent.netConnected(clientID)) {
                disconnect();
                return;
            }
        }
        if (!connected && wasConnected) {
            wasConnected = false;
            parent.netDisconnected(clientID);
        }
        // Process incoming messages
        int i = 0;
        inputLock.lock();
        while (!inputMessages.isEmpty() && (i < maxMessages || maxMessages == -1)) {
            parent.netMessageReceived(clientID, inputMessages.removeFirst());
            i++;
        }
        try {
            inputLock.unlock();
        } catch (IllegalMonitorStateException _) {}
    }

    /**
     * Attempt to send a message to this connection's remote host
     * @param message The message to send
     * @return Whether the message was sent successfully
     */
    public boolean send(NetMessage message) {
        if (message == null) {
            return false;
        }
        if (!connected) {
            return false;
        }
        // Add message to outgoing list
        outputLock.lock();
        boolean writeRequired = outputMessages.isEmpty();
        outputMessages.addLast(message);
        try {
            outputLock.unlock();
        } catch (IllegalMonitorStateException _) {
            disconnect();
            return false;
        }
        // Start asynchronous writing thread if necessary
        if (writeRequired) {
            write();
        }
        return true;
    }

    /**
     * Disconnect this connection from any remote host connected
     * @return Whether this connection was disconnected successfully
     */
    public boolean disconnect() {
        boolean success = true;
        connected = false;
        // Close output stream
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException | NullPointerException _) {
                success = false;
            }
            outputStream = null;
        }
        if (outputThread != null) {
            outputThread.interrupt();
            try {
                outputThread.join();
            } catch (InterruptedException | NullPointerException _) {
                success = false;
            }
            outputThread = null;
        }
        outputMessages.clear();
        // Close input stream
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException | NullPointerException _) {
                success = false;
            }
            inputStream = null;
        }
        if (inputThread != null) {
            inputThread.interrupt();
            try {
                inputThread.join();
            } catch (InterruptedException | NullPointerException _) {
                success = false;
            }
            inputThread = null;
        }
        inputMessages.clear();
        // Close TCP socket connection
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException | NullPointerException _) {
                success = false;
            }
            socket = null;
        }
        return success;
    }

    /**
     * Disconnect this connection if connected to a remote host and free its memory
     * @return Whether this connection was destroyed safely
     */
    public boolean destroy() {
        boolean success = true;
        // Disconnect if connected
        if (connected) {
            if (!disconnect()) {
                success = false;
            }
        }
        // Free memory
        parent = null;
        clientID = 0;
        return success;
    }

    /**
     * Start asynchronously reading incoming messages from a newly connected remote host
     */
    private void read() {
        connected = true;
        inputThread = new Thread(() -> {
            while (connected) {
                if (inputStream == null) {
                    return;
                }
                // Read a message
                NetMessage message;
                try {
                    message = (NetMessage)inputStream.readObject();
                } catch (ClassNotFoundException | ClassCastException | IOException | NullPointerException _) {
                    disconnect();
                    return;
                }
                // Add to the incoming messages queue
                inputLock.lock();
                inputMessages.addLast(message);
                try {
                    inputLock.unlock();
                } catch (IllegalMonitorStateException _) {
                    disconnect();
                    return;
                }
            }
        });
        try {
            inputThread.start();
        } catch (IllegalThreadStateException _) {
            disconnect();
        }
    }

    /**
     * Start asynchronously writing all outgoing messages to the connected remote host
     */
    private void write() {
        outputThread = new Thread(() -> {
            boolean done = false;
            while (!done) {
                if (outputStream == null) {
                    return;
                }
                // Retrieve and write a message
                NetMessage nextMessage;
                outputLock.lock();
                nextMessage = outputMessages.removeFirst();
                try {
                    outputStream.writeObject(nextMessage);
                    outputStream.flush();
                } catch (IOException | NullPointerException _) {
                    disconnect();
                    return;
                }
                done = outputMessages.isEmpty();
                try {
                    outputLock.unlock();
                } catch (IllegalMonitorStateException _) {
                    disconnect();
                    return;
                }
            }
        });
        try {
            outputThread.start();
        } catch (IllegalThreadStateException _) {
            disconnect();
        }
    }

    /**
     * Get the parent class of this network connection subscribed to network event callbacks
     * @return This connection's parent class
     */
    public NetListener getParent() {
        return parent;
    }

    /**
     * Set the parent class of this network connection to be subscribed to network event callbacks
     * @param parent This connection's new parent (must not be null)
     * @return Whether this connection's parent was set successfully
     */
    public boolean setParent(NetListener parent) {
        if (parent == null) {
            return false;
        }
        this.parent = parent;
        return true;
    }

    /**
     * Get this connection's assigned client ID number
     * @return This connection's client ID
     */
    public int getClientID() {
        return clientID;
    }

    /**
     * Set this connection's assigned client ID number
     * @param clientID This connection's new client ID
     */
    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    /**
     * Get the host name of the currently connected remote host or an empty string if not connected
     * @return This connection's remote host's name
     */
    public String getHostName() {
        if (socket != null) {
            InetAddress address = socket.getInetAddress();
            if (address != null) {
                return address.getHostName();
            }
        }
        return "";
    }

    /**
     * Get the remote port of the currently connected remote host or 0 if not connected
     * @return This connection's remote host's port
     */
    public int getPort() {
        if (socket != null) {
            return socket.getPort();
        }
        return 0;
    }

    /**
     * Test whether this connection is currently connected to a remote host
     * @return Whether this connection is connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Test whether this connection has been rejected by its parent class
     * @return Whether this connection has been rejected
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     * Set whether this connection has been rejected by its parent class
     * @param rejected Whether this connected has been rejected
     */
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    /**
     * Get this connection's set of incoming messages to be processed since the last logic update
     * @return The set of incoming messages
     */
    public ArrayList<NetMessage> getMessages() {
        ArrayList<NetMessage> messages = new ArrayList<>();
        inputLock.lock();
        for (NetMessage message : inputMessages) {
            messages.add(message);
        }
        try {
            inputLock.unlock();
        } catch (IllegalMonitorStateException _) {
        }
        return messages;
    }

}
